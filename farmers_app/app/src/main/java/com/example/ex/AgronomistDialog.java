package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AgronomistDialog extends DialogFragment {


    private EditText idField;
    private Button saveBtn;
    private Button cancelBtn;
    private Activity activity;
    private RoomDB database;
    private ArrayList<MessagesTable> messsages = null;
    private String mail = null;
    private String toMail = null;
    private String mailID = null;
    public void setToMail(String mail) {
        this.toMail = mail;
    }
    // Use this instance of the interface to deliver action events
    MyAlertDialogFragmentListener mListener;


    //for fire base
    private FirebaseFirestore db;

    // Override the Fragment.onAttach() method to instantiate the MyAlertDialogFragmentListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MyAlertDialogFragmentListener so we can send events to the host
            mListener = (MyAlertDialogFragmentListener) activity;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_fragment_dialog_alert, null);
        Bundle b = getArguments();
        mail = b.getString("From");
        toMail = b.getString("To");
        mailID = b.getString("mailID");
        db = FirebaseFirestore.getInstance();
        idField = (EditText) v.findViewById(R.id.idfieldContactAgronomist);
        saveBtn = (Button) v.findViewById(R.id.saveBtnContactAgronomist);
        cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        database = RoomDB.getInstance(activity);
        messsages = new ArrayList<>(database.mainDao().getAllMessages());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = getIdField();



                // create a new data with a user details;
                HashMap<String, Object> message = new HashMap<>();
                message.put("frommail", mail);
                message.put("showagronomist", true);
                message.put("showfarmer", true);
                message.put("text", res);
                message.put("tomail", toMail);

                CollectionReference messages = db.collection("messages");

                Task task2 = messages.document(mailID).set(message);

                DocumentReference docRef = db.collection("messages").document(mailID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               DocumentSnapshot document = task.getResult();
                                                               //upload message to firebase firestore

                                                               // for screen

                                                               idField.setText("");

                                                               MessagesTable mt = new MessagesTable();
                                                               //add the text from the edit text to the item
                                                               mt.setFrom(mail);
                                                               mt.setTo(toMail);
                                                               mt.setContent(res);
                                                               mt.setFarmerBool(true);
                                                               mt.setAgronomistBool(true);
                                                               mt.setMessageID(mailID);


                                                               //add the new created item to teh db and the arraylist
                                                               database.mainDao().insert(mt);
                                                               messsages.clear();
                                                               messsages.addAll((database.mainDao().getAllFarmerUndeletedMessages()));
                                                           }


                                                       }
                                                   });


                Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;

    }

    public String getIdField() {
        return idField.getText().toString();
    }
}
