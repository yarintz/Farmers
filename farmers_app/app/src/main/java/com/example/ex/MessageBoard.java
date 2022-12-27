package com.example.ex1_205790488_315680397;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageBoard extends RecyclerView.Adapter<com.example.ex1_205790488_315680397.MessageBoard.ContactViewHolder> {


    private List<MessageInfo> messagesList;
    private String messageSent = "message sent";
    private Context context;
    private ArrayList<MessagesTable> messagesTable;
    private int position;
    private FirebaseFirestore db;
    private MessageInfo mi = null;
    private FirebaseStorage mFirebaseStorage;
    private String messageImageURL;

    public MessageBoard(List<MessageInfo> messages, ArrayList<MessagesTable> messagesTable) {
        this.messagesList = messages;
        this.messagesTable = messagesTable;
    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public void onBindViewHolder(com.example.ex1_205790488_315680397.MessageBoard.ContactViewHolder contactViewHolder, int position) {

        MessageInfo Mi = messagesList.get(position);
        contactViewHolder.setData(Mi);
        this.position = position;
    }

    @Override
    public com.example.ex1_205790488_315680397.MessageBoard.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout_agronomist, viewGroup, false);
        this.context = viewGroup.getContext();
        //for firestore:
        db = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        return new com.example.ex1_205790488_315680397.MessageBoard.ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView vName;
        private TextView vMessage;
        private ImageView cropImage;
        private Button vDelBtn;
        private Button vAnsBtn;
        private MessageInfo mi = null;
        private RoomDB database;

        public ContactViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txtName);
            vMessage = (TextView) v.findViewById(R.id.messageContent);
            cropImage = (ImageView) v.findViewById(R.id.backdrop3);
            vDelBtn = (Button) v.findViewById(R.id.deletebtn);
            vAnsBtn = (Button) v.findViewById(R.id.answerbtn);
            database = RoomDB.getInstance(v.getContext());

            vDelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /////check if there is connection to internet
                    Boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                    } else {
                        connected = false;
                    }


                    if (connected) {
                        DocumentReference docRef = db.collection("messages").document(mi.messageID);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if (document.get("imageurl") != null) {
                                            messageImageURL = document.get("imageurl").toString();
                                            db.collection("messages").document(mi.messageID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            StorageReference photoRef = mFirebaseStorage.getReference(messageImageURL);
                                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // File deleted successfully
                                                                    //create a snackbar that informe the user the delete proccess has succeeded.
                                                                    Snackbar sb = null;
                                                                    sb = Snackbar.make(v, R.string.messageDeleted, Snackbar.LENGTH_LONG);
                                                                    sb.setDuration(4500);
                                                                    sb.show();
                                                                    database.mainDao().delete(messagesTable.get(position));
                                                                    com.example.ex1_205790488_315680397.MessageBoard.this.notifyDataSetChanged();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception exception) {
                                                                    //create a snackbar that informe the user the delete proccess has failed.
                                                                    Snackbar sb = null;
                                                                    sb = Snackbar.make(v, R.string.failedDeletingDocument, Snackbar.LENGTH_LONG);
                                                                    sb.setDuration(4500);
                                                                    sb.show();
                                                                }
                                                            });
                                                            database.mainDao().delete(messagesTable.get(position));
                                                            messagesList.remove(mi.location);
                                                        }
                                                    });
                                        } else {
                                            db.collection("messages").document(mi.messageID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            //create a snackbar that informe the user the delete proccess has succeeded.
                                                            Snackbar sb = null;
                                                            sb = Snackbar.make(v, R.string.messageDeleted, Snackbar.LENGTH_LONG);
                                                            sb.setDuration(4500);
                                                            sb.show();
                                                            database.mainDao().delete(messagesTable.get(position));
                                                            com.example.ex1_205790488_315680397.MessageBoard.this.notifyDataSetChanged();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    //create a snackbar that informe the user the delete proccess has failed.
                                                    Snackbar sb = null;
                                                    sb = Snackbar.make(v, R.string.failedDeletingDocument, Snackbar.LENGTH_LONG);
                                                    sb.setDuration(4500);
                                                    sb.show();
                                                }
                                            });
                                            database.mainDao().delete(messagesTable.get(position));
                                            messagesList.remove(mi.location);
                                        }
                                    }
                                }


                            }

                        });
                    } else {
                        Snackbar snackbar;
                        snackbar = Snackbar.make(v, R.string.noInternet, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(4500);
                        snackbar.show();
                    }
                }

            });


            vAnsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AgronomistMainScreen a = AgronomistMainScreen.getInstance();


                    String from = mi.from;
                    a.showCustomAlertDialog(view, from);
                    com.example.ex1_205790488_315680397.MessageBoard.this.notifyDataSetChanged();

                }
            });
        }

        //set the data the user inserted
        public void setData(MessageInfo mi) {
            this.mi = mi;
            vName.setText(mi.name);
            vMessage.setText(mi.messageContent);
            if (mi.image == null) {
                return;
            }
            Drawable d = Drawable.createFromStream(new ByteArrayInputStream(mi.image), null);
            cropImage.setImageDrawable(d);

        }
    }


}
