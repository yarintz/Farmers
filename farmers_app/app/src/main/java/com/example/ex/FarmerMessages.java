package com.example.ex1_205790488_315680397;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class FarmerMessages extends AppCompatActivity {


    private RecyclerView messageBoardRecycler;
    private GridLayoutManager messageBaordGrid;
    static com.example.ex1_205790488_315680397.FarmerMessages instance;
    private ArrayList<MessagesTable> messages = null;
    private RoomDB database;
    private Context context;
    private boolean connected = false;
    //for database uses:
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String mail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_messages_to_agronomist);
        instance = this;

        messageBoardRecycler = (RecyclerView) findViewById(R.id.cardListFarmerMessages);
        messageBaordGrid = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        messageBoardRecycler.setLayoutManager(messageBaordGrid);

        database = RoomDB.getInstance(this);
        messages = new ArrayList<>(database.mainDao().getAllFarmerUndeletedMessages());
        //for firestore:
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        context = this;

        /////check if there is connection to internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }


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

                                            tempMessagesTable.setAgronomistBool(Boolean.valueOf(document.get("showagronomist").toString()));
                                            tempMessagesTable.setFarmerBool((Boolean.valueOf(document.get("showfarmer").toString())));
                                            tempMessagesTable.setTo(document.get("tomail").toString());
                                            tempMessagesTable.setFrom(document.get("frommail").toString());
                                            tempMessagesTable.setContent(document.get("text").toString());
                                            tempMessagesTable.setMessageID(document.getId());
                                            database.mainDao().insert(tempMessagesTable);

                                        }
                                        messages.addAll(database.mainDao().getAllMessages());
                                        messageBaordGrid = new GridLayoutManager(FarmerMessages.this, 1, GridLayoutManager.VERTICAL, false);
                                        messageBoardRecycler.setLayoutManager(messageBaordGrid);
                                        database = RoomDB.getInstance(FarmerMessages.this);
                                        messages = new ArrayList<>(database.mainDao().getAllAgronomistUndeletedMessages());
                                        FarmerMessageBoard mb = new FarmerMessageBoard(createList(messages.size()), messages);
                                        messageBoardRecycler.setAdapter(mb);
                                    }
                                }
                            });
                }
            });
        }
        else{
            FarmerMessageBoard mb = new FarmerMessageBoard(createList(messages.size()), messages);
            messageBoardRecycler.setAdapter(mb);
        }


    }


    public static com.example.ex1_205790488_315680397.FarmerMessages getInstance() {
        return instance;
    }


    //create list of messages that has been sent to the agronomist
    private List<FarmerMessageInfo> createList(int size) {

        List<FarmerMessageInfo> result = new ArrayList<FarmerMessageInfo>();
        for (int i = 0; i < size; i++) {
            FarmerMessageInfo fmi = new FarmerMessageInfo();
            fmi.name = FarmerMessageInfo.NAME_PREFIX_TO.concat(messages.get(i).getTo());
            fmi.messageContent = messages.get(i).getContent();
            fmi.id = messages.get(i).getId();
            fmi.farmerBool = messages.get(i).isFarmerBool();
            fmi.agronomistBool = messages.get(i).isAgronomistBool();
            fmi.from = messages.get(i).getFrom();
            fmi.location = i;
            fmi.messageID = messages.get(i).getMessageID();
            if (messages.get(i).getImage() != null) {
                fmi.image = messages.get(i).getImage();

            }
            result.add(fmi);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLogic.getInstance().onOptionsItemSelected(item, context, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuLogic.getInstance().onCreateOptionsMenu(menu, context, this);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}


