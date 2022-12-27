package com.example.ex1_205790488_315680397;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FarmerInventory extends AppCompatActivity {
    //    protected InventoryEditDialog frag = null;
    static FarmerInventory instance;
    private Button addNew;
    private EditText newCropNameEditText;
    private EditText newCropAmountEditText;
    private TextView newCropNameTextView;
    private TextView newCropAmountTextView;
    private Button cancel;
    private Button approve;
    private Menu menu;
    private Context context = null;
    private ArrayList<SeedsTable> seeds = null;
    private RoomDB database;
    private boolean connected = false;
    //for database uses:
    private FirebaseFirestore db;
    private String mail;

    // Array of strings...
    ListView simpleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        this.context = this;
        // seeds.addAll(Arrays.asList(seedsArray));
        setContentView(R.layout.activity_inventory);
        //create the database instance and the arraylist of the items with relevant information
        database = RoomDB.getInstance(this);
        seeds = new ArrayList<>(database.mainDao().getAllSeeds());

        //for firestore:
        db = FirebaseFirestore.getInstance();

        simpleList = (ListView) findViewById(R.id.seedInventory);
        simpleList.setBackgroundColor(Color.LTGRAY);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.seedInventoryScreen, seeds);


        addNew = (Button) findViewById(R.id.addNewSeedToInventory);

        newCropNameTextView = (TextView) findViewById(R.id.newSeedNameTextView);
        newCropAmountTextView = (TextView) findViewById(R.id.newSeedAmountTextView);
        newCropNameEditText = (EditText) findViewById(R.id.newSeedNameEditText);
        newCropAmountEditText = (EditText) findViewById(R.id.newSeedAmountEditText);
        cancel = (Button) findViewById(R.id.cancelNewSeed);
        approve = (Button) findViewById(R.id.aproveActionNewSeed);

        newCropNameEditText.setVisibility(View.GONE);
        newCropAmountEditText.setVisibility(View.GONE);
        newCropNameTextView.setVisibility(View.GONE);
        newCropAmountTextView.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        approve.setVisibility(View.GONE);

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
                    db.collection("seeds")
                            .whereEqualTo("usermail", mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               database.mainDao().nukeTableSeeds();
                                                               seeds.clear();

                                                               //runs on all ducoments with user mail equal to our users and load it to database
                                                               for (QueryDocumentSnapshot document : task.getResult()) {
                                                                   SeedsTable tempSeedsTable = new SeedsTable();
                                                                   tempSeedsTable.setSeedName(document.get("name").toString());
                                                                   tempSeedsTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                                                   database.mainDao().insert(tempSeedsTable);

                                                               }
                                                               seeds.addAll(database.mainDao().getAllSeeds());
                                                           }


                                                           CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), seeds, FarmerInventory.this);
                                                           simpleList.setAdapter(customAdapter);
                                                       }
                                                   });


                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            newCropNameEditText.setText("");
                                            newCropAmountEditText.setText("");

                                            newCropNameEditText.setVisibility(View.GONE);
                                            newCropAmountEditText.setVisibility(View.GONE);
                                            newCropNameTextView.setVisibility(View.GONE);
                                            newCropAmountTextView.setVisibility(View.GONE);
                                            cancel.setVisibility(View.GONE);
                                            approve.setVisibility(View.GONE);

                                        }
                                    });

                                    approve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String newCropName = newCropNameEditText.getText().toString().toLowerCase();
                                            ;
                                            String newCropAmount = newCropAmountEditText.getText().toString();
                                            SeedsTable st = new SeedsTable();

                                            Snackbar sb = null;
                                            //check if seed already exist
                                            for (int i = 0; i < seeds.size(); i++) {
                                                if (seeds.get(i).getSeedName().equals(newCropName)) {
                                                    sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.itemExist, Snackbar.LENGTH_LONG);
                                                    sb.setDuration(4500);
                                                    sb.show();
                                                    return;
                                                }
                                            }
                                            //check that the user added information to all fields.
                                            if ((newCropName == "") || (newCropAmount == "")) {
                                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.fillAllFields, Snackbar.LENGTH_LONG);
                                                sb.setDuration(4500);
                                                sb.show();
                                                return;
                                            } else if (!newCropAmount.matches("[0-9]+")) {
                                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                                sb.setDuration(4500);
                                                sb.show();
                                            } else {
                                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.changedSuccessfully, Snackbar.LENGTH_LONG);
                                                sb.setDuration(4500);
                                                sb.show();

                                                // seeds.add(newCropName + ":      " + newCropAmount + " kg");

                                                newCropNameEditText.setText("");
                                                newCropAmountEditText.setText("");

                                                newCropNameEditText.setVisibility(View.GONE);
                                                newCropAmountEditText.setVisibility(View.GONE);
                                                newCropNameTextView.setVisibility(View.GONE);
                                                newCropAmountTextView.setVisibility(View.GONE);
                                                cancel.setVisibility(View.GONE);
                                                approve.setVisibility(View.GONE);

                                                //add the text from the edit text to the item
                                                st.setSeedName(newCropName);
                                                st.setAmount(Integer.parseInt(newCropAmount));

                                                //add the seed inforamtion to firebase
                                                String currentseed = mail + newCropName;
                                                Map<String, Object> seed = new HashMap<>();
                                                CollectionReference seedsRef = db.collection("seeds");
                                                seed.put("name", newCropName.toLowerCase());
                                                seed.put("amount", Integer.parseInt(newCropAmount));
                                                seed.put("usermail", mail);

                                                Task task2 = seedsRef.document(currentseed).set(seed);

                                                //add the new created item to teh db and the arraylist
                                                database.mainDao().insert(st);
                                                seeds.clear();
                                                seeds.addAll((database.mainDao().getAllSeeds()));
                                            }

                                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), seeds, FarmerInventory.this);
                                            simpleList.setAdapter(customAdapter);

                                        }
                                    });
                                    ///test
//                                }
//                            });
                }
            });
        }
        else {
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), seeds, FarmerInventory.this);
            simpleList.setAdapter(customAdapter);
        }
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    newCropNameEditText.setVisibility(View.VISIBLE);
                    newCropAmountEditText.setVisibility(View.VISIBLE);
                    newCropNameTextView.setVisibility(View.VISIBLE);
                    newCropAmountTextView.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    approve.setVisibility(View.VISIBLE);
                }
            }
        });
    }


        public static FarmerInventory getInstance () {
            return instance;
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


        // set the seed amount that the user inserted
        public void setSeedsAmount ( int i, String newAmount){

            int selectedItem = seeds.get(i).getId();
            String seedName = seeds.get(i).getSeedName();
            if (newAmount.equals("")) {

                Snackbar sb = null;
                sb = Snackbar.make(this.findViewById(android.R.id.content), R.string.amountCantBeEmpty, Snackbar.LENGTH_LONG);
                sb.setDuration(4500);
                sb.show();
                return;
            }
            //  String[] tempArray = selectedItem.split(":");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.updateAmount)
                    .setMessage(getString(R.string.areYouSureSetAmount) + " " + newAmount + " " + getString(R.string.asTheNewAmount) + " " + seedName + getString(R.string.questionMark))

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            Snackbar sb = null;
                            if (!newAmount.matches("[0-9]+")) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                            } else {
                                //update the current crop in firebase database
                                String getSeedToUpdate = mail + seedName;
                                DocumentReference docRef = db.collection("seeds").document(getSeedToUpdate);
                                docRef.update("amount", Integer.parseInt(newAmount))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar sb = null;
                                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.changedSuccessfully, Snackbar.LENGTH_LONG);
                                                sb.setDuration(4500);
                                                sb.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar sb = null;
                                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.failedUpdatingDocument, Snackbar.LENGTH_LONG);
                                                sb.setDuration(4500);
                                                sb.show();
                                            }
                                        });

                                database.mainDao().updateSeedsAmount(selectedItem, newAmount);
                                seeds.clear();
                                seeds.addAll((database.mainDao().getAllSeeds()));
                                // seeds.set(i, seedName + ":      " + newAmount + " kg");

                            }
                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), seeds, FarmerInventory.this);
                            simpleList.setAdapter(customAdapter);

                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .show();


        }

        //remove item from the list
        public void removeSeed ( int i){
            //get the item name
            int selectedItemName = this.seeds.get(i).getId();
            String itemName = this.seeds.get(i).getSeedName();

            //create an alert dialog to make sure the user want to delete this item.
            new AlertDialog.Builder(this)
                    .setTitle(R.string.remove)
                    .setMessage(getString(R.string.areYouSureRemove) + " " + itemName + getString(R.string.questionMark))

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String remove = getString(R.string.removeFromList);

                            //delete the item from firestore
                            String temp = mail + itemName.toLowerCase();
                            db.collection("seeds").document(temp)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //create a snackbar that informe the user the delete proccess has succeeded.
                                            Snackbar sb = null;
                                            sb = Snackbar.make(instance.findViewById(android.R.id.content), itemName + " " + remove, Snackbar.LENGTH_LONG);
                                            sb.setDuration(4500);
                                            sb.show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //create a snackbar that informe the user the delete proccess has failed.
                                            Snackbar sb = null;
                                            sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.failedDeletingDocument, Snackbar.LENGTH_LONG);
                                            sb.setDuration(4500);
                                            sb.show();
                                        }
                                    });


                            //delete the item from the database and from the arraylist.
                            database.mainDao().delete(seeds.get(i));
                            seeds.remove(i);

                            //load the listview after the update

                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), seeds, FarmerInventory.this);
                            simpleList.setAdapter(customAdapter);
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .show();

        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            return MenuLogic.getInstance().onOptionsItemSelected(item, context, this);
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            return MenuLogic.getInstance().onCreateOptionsMenu(menu, context, this);

        }

    }