package com.example.ex1_205790488_315680397;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ContactAgronomist extends AppCompatActivity {
    private EditText idField;
    private Button sendBtn;
    private Context context = null;
    private Spinner spinner = null;
    private Button attachPhoto;
    private ArrayList<String> allAgronomistArrayList = null;
    private ArrayList<MessagesTable> messsages = null;
    private RoomDB database;
    private boolean flag = false;
    private View view;
    //for database uses:
    private FirebaseFirestore db;
    private String mail;
    private boolean connected = false;
    private boolean tookPicture = false;

    //for autocomplete spinner
    private AutoCompleteTextView textView;


    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView = null;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = ContactAgronomist.this;
        this.view = null;
        setContentView(R.layout.activity_contact_agronomist);
        idField = (EditText) findViewById(R.id.idfieldContactAgronomist);
        sendBtn = (Button) findViewById(R.id.saveBtnContactAgronomist);
        attachPhoto = (Button) findViewById(R.id.attachPhoto);
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
//        spinner = (Spinner) findViewById(R.id.spinner);
        textView = (AutoCompleteTextView) findViewById(R.id.conactAgronomistTextView);
//        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropdown, 0);


        //for firestore:
        db = FirebaseFirestore.getInstance();
        database = RoomDB.getInstance(this);
        messsages = new ArrayList<>(database.mainDao().getAllFarmerUndeletedMessages());

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
                    ArrayList<String> allAgronomist = new ArrayList<>();
                    db.collection("users")
                            .whereEqualTo("Type", "Agronomist")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            allAgronomist.add(document.get("Mail").toString());

                                        }
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                            context, android.R.layout.simple_dropdown_item_1line,
                                            allAgronomist);

                                    textView.setAdapter(arrayAdapter);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View arg0) {
                                            textView.showDropDown();
                                        }
                                    });
                                }
                            });


                    attachPhoto.setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            ContactAgronomist.this.view = view;
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    });

                    sendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String res = getIdField();
                            if (res.equals("")) {
                                Snackbar sb = Snackbar.make(view, R.string.pleaseEnterATextMessage, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                            } else {

                                //for firebase -
                                if (tookPicture) {
                                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    // Create a storage reference from our app
                                    StorageReference storageRef = storage.getReference();
                                    final String imageName = UUID.randomUUID().toString();
                                    StorageReference imageRef = storageRef.child(imageName);

                                    UploadTask uploadTask = imageRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Snackbar sb = null;
                                            sb = Snackbar.make(view, R.string.failedCreatingNewMessage, Snackbar.LENGTH_LONG);
                                            sb.setDuration(4500);
                                            sb.show();
                                            return;

                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // the upload complete successfully
                                            // create a new data with a user details;
                                            HashMap<String, Object> message = new HashMap<>();
                                            message.put("frommail", mail);
                                            message.put("imageurl", imageName);
                                            message.put("showagronomist", true);
                                            message.put("showfarmer", true);
                                            message.put("text", res);
                                            message.put("tomail", getAgronomistName());

                                            CollectionReference messages = db.collection("messages");
                                            String messageKey = UUID.randomUUID().toString();
                                            Task task2 = messages.document(messageKey).set(message);

                                            DocumentReference docRef = db.collection("messages").document(messageKey);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        //upload message to firebase firestore

                                                        // for screen
                                                        String agronomistName = getAgronomistName();
                                                        Toast.makeText(context, getString(R.string.message) + " " + res + " " + getString(R.string.sent_to) + " " + agronomistName, Toast.LENGTH_SHORT).show();
                                                        idField.setText("");

                                                        MessagesTable mt = new MessagesTable();
                                                        //add the text from the edit text to the item
                                                        mt.setFrom(mail);
                                                        mt.setTo(agronomistName);
                                                        mt.setContent(res);
                                                        mt.setFarmerBool(true);
                                                        mt.setAgronomistBool(true);


                                                        Resources res2 = getResources();
                                                        if (flag) {
                                                            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                                                            Bitmap bitmap = drawable.getBitmap();
                                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                            byte[] byteArray = stream.toByteArray();
                                                            mt.setImage(byteArray);
                                                            imageView.setImageBitmap(null);
                                                        } else
                                                            mt.setImage(null);


                                                        //add the new created item to teh db and the arraylist
                                                        database.mainDao().insert(mt);
                                                        messsages.clear();
                                                        messsages.addAll((database.mainDao().getAllFarmerUndeletedMessages()));
                                                        imageView.setImageResource(R.drawable.icon);
                                                        tookPicture = false;


                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            else {
                                    HashMap<String, Object> message = new HashMap<>();
                                    message.put("frommail", mail);
                                    message.put("showagronomist", true);
                                    message.put("showfarmer", true);
                                    message.put("text", res);
                                    message.put("tomail", getAgronomistName());

                                    CollectionReference messages = db.collection("messages");
                                    String messageKey = UUID.randomUUID().toString();
                                    Task task2 = messages.document(messageKey).set(message);

                                    DocumentReference docRef = db.collection("messages").document(messageKey);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                //upload message to firebase firestore

                                                // for screen
                                                String agronomistName = getAgronomistName();
                                                Toast.makeText(context, getString(R.string.message) + " " + res + " " + getString(R.string.sent_to) + " " + agronomistName, Toast.LENGTH_SHORT).show();
                                                idField.setText("");

                                                MessagesTable mt = new MessagesTable();
                                                //add the text from the edit text to the item
                                                mt.setFrom(mail);
                                                mt.setTo(agronomistName);
                                                mt.setContent(res);
                                                mt.setFarmerBool(true);
                                                mt.setAgronomistBool(true);


                                                Resources res2 = getResources();
                                                if (flag) {
                                                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                                                    Bitmap bitmap = drawable.getBitmap();
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                    byte[] byteArray = stream.toByteArray();
                                                    mt.setImage(byteArray);
                                                    imageView.setImageBitmap(null);
                                                } else
                                                    mt.setImage(null);


                                                //add the new created item to teh db and the arraylist
                                                database.mainDao().insert(mt);
                                                messsages.clear();
                                                messsages.addAll((database.mainDao().getAllFarmerUndeletedMessages()));
                                                imageView.setImageResource(R.drawable.icon);
                                                tookPicture = false;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
        }
        else{
            Snackbar snackbar;
            snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.noInternetMessage, Snackbar.LENGTH_LONG);
            snackbar.setDuration(4500);
            snackbar.show();
        }

    }

    public String getIdField() {
        return idField.getText().toString();
    }


    public String getAgronomistName() {
        return textView.getText().toString();
    }


    /**
     * checks if user gave permission to use camera, if he gave the permission opens it, otherwise snackbar will be shown.
     *
     * @param requestCode - the code for permission of denial of using camera.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else if (MY_CAMERA_PERMISSION_CODE == 100) {
                Snackbar snackbar = Snackbar.make(view, R.string.cantOpenCamera, BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setDuration(4500);
                View snackbarView = snackbar.getView();
                TextView snackTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

                snackTextView.setMaxLines(3);
                snackbar.show();

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            tookPicture = true;
            flag = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuLogic.getInstance().onCreateOptionsMenu(menu, context, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLogic.getInstance().onOptionsItemSelected(item, context, this);
    }

}






