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
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class FarmerMessageBoard extends RecyclerView.Adapter<com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder> {


    private List<FarmerMessageInfo> messagesList;
    private String messageSent = "message sent";
    private Context context;
    private ArrayList<MessagesTable> messagesTable;
    private int position;
    private FirebaseFirestore db;

    public FarmerMessageBoard(List<FarmerMessageInfo> messages, ArrayList<MessagesTable> messagesTable) {
        this.messagesList = messages;
        this.messagesTable = messagesTable;
    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public void onBindViewHolder(com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder contactViewHolder, int position) {

        FarmerMessageInfo Mi = messagesList.get(position);
        contactViewHolder.setData(Mi);
        this.position = position;
    }

    @Override
    public com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout_farmer_messages, viewGroup, false);
        this.context = viewGroup.getContext();
        db = FirebaseFirestore.getInstance();
        return new com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder(itemView);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView vName;
        private TextView vMessage;
        private ImageView cropImage;
        private Button vDelBtn;
        private Button vAnsBtn;
        private FarmerMessageInfo mi = null;
        private RoomDB database;

        public ContactViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txtNameFarmerMessages);
            vMessage = (TextView) v.findViewById(R.id.messageContentFarmerMessages);
            cropImage = (ImageView) v.findViewById(R.id.backdrop3FarmerMessages);
            vDelBtn = (Button) v.findViewById(R.id.deletebtnFarmerMessages);
            //  vAnsBtn = (Button) v.findViewById(R.id.answerbtnFarmerMessages);
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

                                        db.collection("messages").document(mi.messageID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                               StorageReference storageRef = mFirebaseStorage.getReference();
//                                                               StorageReference imageRef = storageRef.child(imageName);


                                                        // File deleted successfully
                                                        //create a snackbar that informe the user the delete proccess has succeeded.
                                                        Snackbar sb = null;
                                                        sb = Snackbar.make(v, R.string.messageDeleted, Snackbar.LENGTH_LONG);
                                                        sb.setDuration(4500);
                                                        sb.show();
                                                        database.mainDao().delete(messagesTable.get(position));
                                                        messagesList.remove(mi.location);
                                                        com.example.ex1_205790488_315680397.FarmerMessageBoard.this.notifyDataSetChanged();
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

                                    }


                                }
                            }


                        });
                    }
                    else {
                        Snackbar snackbar;
                        snackbar = Snackbar.make(v, R.string.noInternet, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(4500);
                        snackbar.show();
                    }
                }
            });
        }


//                        // if farmer didnt delete the message we want to delete it only for agronomist.
//                        if (com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder.this.mi.agronomistBool == true) {
//                            database.mainDao().updateFarmerBool(com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder.this.mi.id, false);
//                        }
//                        // if the farmer deleted this message too we dont need it in our db anymore.
//                        else if(com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder.this.mi.agronomistBool == false){
//                            database.mainDao().delete(messagesTable.get(position));
//                        }
//                        for(FarmerMessageInfo mi: messagesList){
//                            if(mi.id == com.example.ex1_205790488_315680397.FarmerMessageBoard.ContactViewHolder.this.mi.id){
//                                messagesList.remove(mi);
//                                break;
//                            }
//                        }
//                        com.example.ex1_205790488_315680397.FarmerMessageBoard.this.notifyDataSetChanged();
//                        Toast.makeText(context , R.string.messageDeleted, Toast.LENGTH_SHORT).show();
//                    }
//                });

        // }
        //set the data the user inserted
        public void setData(FarmerMessageInfo mi) {
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


