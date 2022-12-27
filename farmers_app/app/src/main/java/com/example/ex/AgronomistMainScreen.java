package com.example.ex1_205790488_315680397;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgronomistMainScreen extends AppCompatActivity {

    private RecyclerView messageBoardRecycler;
    private GridLayoutManager messageBaordGrid;
    static AgronomistMainScreen instance;
    private Context context;
    private ArrayList<MessagesTable> messages = null;
    private RoomDB database;
    //for database uses:
    private FirebaseFirestore db;
    private String mail;
    private ImageView imageView;
    protected MessageBoard mb = null;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agronomist_main_screen);
        instance = this;
        context = this;
        database = RoomDB.getInstance(this);
        messages = new ArrayList<>(database.mainDao().getAllMessages());

        //check if there is connection to internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }



            //for firestore:
            db = FirebaseFirestore.getInstance();
            messageBoardRecycler = (RecyclerView) findViewById(R.id.cardListAgronomist);

            ImageButton exit = (ImageButton) findViewById(R.id.agronomistLogOut);

            FirebaseStorage storage = FirebaseStorage.getInstance();

            //for exiting app alert dialog
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog exitDialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.logOut)
                            .setMessage(R.string.areYouSureYouWantToLogOut)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mainActivity = new Intent(context, MainActivity.class);
                                    database.mainDao().updateUserType(null);
                                    // MainActivity.setType(null);
                                    FirebaseAuth.getInstance().signOut();
                                    context.startActivity(mainActivity);

                                    finish();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(R.drawable.exit_icon)
                            .show();
                }
            });
        if(connected) {
            //for firebase loading
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
            docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mail = document.get("Mail").toString();
                        }
                    }
                    db.collection("messages")
                            .whereEqualTo("tomail", mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        database.mainDao().nukeTableMessages();
                                        messages.clear();

                                        //runs on all ducoments with user mail equal to our users and load it to database
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            MessagesTable tempMessagesTable = new MessagesTable();
                                            StorageReference storageRef = storage.getReference();
                                            if(document.get("imageurl") != null) {
                                                String imageUrl = document.get("imageurl").toString();
                                                StorageReference storageReference = storageRef.child(imageUrl);
                                                storageReference.getDownloadUrl().addOnCompleteListener(
                                                        new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    final ImageView imageView = findViewById(R.id.forAgronomistMainScreen);
                                                                    String downloadUrl = task.getResult().toString();
                                                                    Glide.with(context)
                                                                            .asBitmap()
                                                                            .load(downloadUrl)
                                                                            .into(new CustomTarget<Bitmap>() {
                                                                                @Override
                                                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                                    imageView.setImageBitmap(resource);
//                                                                            imageView.setDrawingCacheEnabled(true);
//                                                            imageView.buildDrawingCache();
                                                                                    Drawable drawable = imageView.getDrawable();
                                                                                    Bitmap bitmap = drawableToBitmap(drawable);
                                                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                                                    byte[] data = baos.toByteArray();
                                                                                    tempMessagesTable.setImage(data);
                                                                                    tempMessagesTable.setAgronomistBool(Boolean.valueOf(document.get("showagronomist").toString()));
                                                                                    tempMessagesTable.setFarmerBool((Boolean.valueOf(document.get("showfarmer").toString())));
                                                                                    tempMessagesTable.setTo(document.get("tomail").toString());
                                                                                    tempMessagesTable.setFrom(document.get("frommail").toString());
                                                                                    tempMessagesTable.setContent(document.get("text").toString());
                                                                                    tempMessagesTable.setMessageID(document.getId());
                                                                                    database.mainDao().insert(tempMessagesTable);
                                                                                    messages.addAll(database.mainDao().getAllMessages());
                                                                                    messageBaordGrid = new GridLayoutManager(AgronomistMainScreen.this, 1, GridLayoutManager.VERTICAL, false);
                                                                                    messageBoardRecycler.setLayoutManager(messageBaordGrid);
                                                                                    database = RoomDB.getInstance(AgronomistMainScreen.this);
                                                                                    messages = new ArrayList<>(database.mainDao().getAllAgronomistUndeletedMessages());
                                                                                    MessageBoard mb = new MessageBoard(createList(messages.size()), messages);
                                                                                    messageBoardRecycler.setAdapter(mb);
                                                                                }


                                                                                @Override
                                                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                                                }
                                                                            });

                                                                } else {
                                                                    System.out.println("Getting download url was not successful." +
                                                                            task.getException());
                                                                }

                                                            }
                                                        });
                                            }
                                            else{
                                                tempMessagesTable.setAgronomistBool(Boolean.valueOf(document.get("showagronomist").toString()));
                                                tempMessagesTable.setFarmerBool((Boolean.valueOf(document.get("showfarmer").toString())));
                                                tempMessagesTable.setTo(document.get("tomail").toString());
                                                tempMessagesTable.setFrom(document.get("frommail").toString());
                                                tempMessagesTable.setContent(document.get("text").toString());
                                                tempMessagesTable.setMessageID(document.getId());
                                                database.mainDao().insert(tempMessagesTable);
                                                messages.addAll(database.mainDao().getAllMessages());
                                                messageBaordGrid = new GridLayoutManager(AgronomistMainScreen.this, 1, GridLayoutManager.VERTICAL, false);
                                                messageBoardRecycler.setLayoutManager(messageBaordGrid);
                                                database = RoomDB.getInstance(AgronomistMainScreen.this);
                                                messages = new ArrayList<>(database.mainDao().getAllAgronomistUndeletedMessages());
                                                MessageBoard mb = new MessageBoard(createList(messages.size()), messages);
                                                messageBoardRecycler.setAdapter(mb);
                                            }
                                        }
                                    }

                                }

                            });
                }
            });
        }
        else{
            messageBaordGrid = new GridLayoutManager(AgronomistMainScreen.this, 1, GridLayoutManager.VERTICAL, false);
            messageBoardRecycler.setLayoutManager(messageBaordGrid);
            MessageBoard mb = new MessageBoard(createList(messages.size()), messages);
            messageBoardRecycler.setAdapter(mb);
        }
    }

    public static AgronomistMainScreen getInstance() {
        return instance;
    }

    public  void showCustomAlertDialog(View view, String tomail) {
        if(checkConnection()) {
            AgronomistDialog frag = new AgronomistDialog();
            Bundle args = new Bundle();
            args.putString("From", mail);
            args.putString("To", tomail);
            String messageKey = UUID.randomUUID().toString();
            args.putString("mailID", messageKey);
            args.putInt("title", R.string.alertDialogTwoButtonsTitle);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "dialog");
        }

    }

//create list of messages that has been sent to the agronomist
    private List<MessageInfo> createList(int size) {

        List<MessageInfo> result = new ArrayList<MessageInfo>();
       for (int i = 0 ; i < size; i++) {
            MessageInfo mi = new MessageInfo();
            mi.name = MessageInfo.NAME_PREFIX.concat(messages.get(i).getFrom());
            mi.messageContent = messages.get(i).getContent();
            mi.id = messages.get(i).getId();
            mi.farmerBool = messages.get(i).isFarmerBool();
            mi.agronomistBool = messages.get(i).isAgronomistBool();
            mi.from = messages.get(i).getFrom();
            mi.location = i;
            mi.messageID = messages.get(i).getMessageID();

            if(messages.get(i).getImage() != null) {
                mi.image = messages.get(i).getImage();

            }
            result.add(mi);
        }
        return result;
    }
    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.areYouSureYouWantToExit)

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.quit)
                .create();
        return myQuittingDialogBox;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public boolean checkConnection(){
        if(connected){
            return true;
        }
        else
        if(!connected){
            Snackbar snackbar;
            snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.noInternet, Snackbar.LENGTH_LONG);
            snackbar.setDuration(4500);
            snackbar.show();
        }
        return false;
    }

}
