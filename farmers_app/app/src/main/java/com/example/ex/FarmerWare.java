package com.example.ex1_205790488_315680397;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FarmerWare extends AppCompatActivity {
    //    protected InventoryEditDialog frag = null;
    static FarmerWare instance;
    private Button addNew;
    private EditText newCropNameEditText;
    private EditText newCropAmountEditText;
    private TextView newCropNameTextView;
    private TextView newCropAmountTextView;
    private Button cancel;
    private Button approve;
    private Menu menu;
    private Context context = null;
    private ArrayList<WareTable> wareCrops = null;
    private RoomDB database;
    private View classView;
    //for database uses:
    private FirebaseFirestore db;
    private String mail;
    private boolean connected = false;

    // Array of strings...
    ListView simpleList;

    ArrayList<String> cropsToSell = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
        this.context = this;
        setContentView(R.layout.activity_ready_to_supply);
        database = RoomDB.getInstance(this);
        wareCrops = new ArrayList<>(database.mainDao().getAllWareCrops());
        // cropsToSell.addAll(Arrays.asList(cropsToSellArray));

        //for firestore:
        db = FirebaseFirestore.getInstance();

        simpleList = (ListView) findViewById(R.id.cropsToSupply);
        simpleList.setBackgroundColor(Color.LTGRAY);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.seedInventoryScreen, seeds);
//        CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, this);
//        simpleList.setAdapter(customAdapterCrops);

        addNew = (Button) findViewById(R.id.addNewCropToInventory);

        newCropNameTextView = (TextView) findViewById(R.id.newCropNameTextView);
        newCropAmountTextView = (TextView) findViewById(R.id.newCropAmountTextView);
        newCropNameEditText = (EditText) findViewById(R.id.newCropNameEditText);
        newCropAmountEditText = (EditText) findViewById(R.id.newCropAmountEditText);
        cancel = (Button) findViewById(R.id.cancelNewCrop);
        approve = (Button) findViewById(R.id.aproveActionNewCrop);

        newCropNameEditText.setVisibility(View.GONE);
        newCropAmountEditText.setVisibility(View.GONE);
        newCropNameTextView.setVisibility(View.GONE);
        newCropAmountTextView.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        approve.setVisibility(View.GONE);
       // database.mainDao().nukeTableWare();

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


                    db.collection("readyToSupply")
                            .whereEqualTo("usermail", mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        database.mainDao().nukeTableWare();
                                        wareCrops.clear();

                                        //runs on all ducoments with user mail equal to our users and load it to database
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            WareTable tempWareTable = new WareTable();
                                            tempWareTable.setWareCropName(document.get("name").toString());
                                            tempWareTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                            database.mainDao().insert(tempWareTable);

                                        }
                                        wareCrops.addAll(database.mainDao().getAllWareCrops());
                                    }

                                    CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, FarmerWare.this);
                                    simpleList.setAdapter(customAdapterCrops);
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
                            String newCropAmount = newCropAmountEditText.getText().toString();
                            WareTable wt = new WareTable();

                            Snackbar sb = null;
                            for (int i = 0; i < wareCrops.size(); i++) {
                                if (wareCrops.get(i).getWareCropName().equals(newCropName)) {
                                    sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.itemExist, Snackbar.LENGTH_LONG);
                                    sb.setDuration(4500);
                                    sb.show();
                                    return;
                                }
                            }
                            if (!newCropAmount.matches("[0-9]+")) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                            } else {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.changedSuccessfully, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();

                                cropsToSell.add(newCropName + ":      " + newCropAmount + " kg");

                                newCropNameEditText.setText("");
                                newCropAmountEditText.setText("");

                                newCropNameEditText.setVisibility(View.GONE);
                                newCropAmountEditText.setVisibility(View.GONE);
                                newCropNameTextView.setVisibility(View.GONE);
                                newCropAmountTextView.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);
                                approve.setVisibility(View.GONE);

                                wt.setWareCropName(newCropName);
                                wt.setAmount(Integer.parseInt(newCropAmount));

                                //add the crop inforamtion to firebase
                                String currentFarmerWare = mail + newCropName;
                                Map<String, Object> ware = new HashMap<>();
                                CollectionReference wareRef = db.collection("readyToSupply");
                                ware.put("name", newCropName.toLowerCase());
                                ware.put("amount", Integer.parseInt(newCropAmount));
                                ware.put("usermail", mail);

                                //String farmPlotString = mail + String.valueOf(row) + String.valueOf(col);


                                Task task2 = wareRef.document(currentFarmerWare).set(ware);


                                //add the new created item to teh db and the arraylist
                                database.mainDao().insert(wt);
                                wareCrops.clear();
                                wareCrops.addAll((database.mainDao().getAllWareCrops()));
                            }

                            CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, FarmerWare.this);
                            simpleList.setAdapter(customAdapterCrops);

                        }
                    });


                }
            });
            //close if
        }
        else{
            CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, FarmerWare.this);
            simpleList.setAdapter(customAdapterCrops);
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

    public static FarmerWare getInstance() {
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

    //set the crops amount that the user inserted
    public void setCropsAmount(int i, String newAmount) {
        int selectedItem = wareCrops.get(i).getId();
        String cropName = wareCrops.get(i).getWareCropName();
        if (newAmount.equals("")) {

            Snackbar sb = null;
            sb = Snackbar.make(this.findViewById(android.R.id.content), R.string.amountCantBeEmpty, Snackbar.LENGTH_LONG);
            sb.setDuration(4500);
            sb.show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.updateAmount)
                .setMessage(getString(R.string.areYouSureSetAmount) + " " + newAmount + " " + getString(R.string.asTheNewAmount) + " " + cropName + getString(R.string.questionMark))

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
                            String getCropToUpdate = mail + cropName;
                            DocumentReference docRef = db.collection("readyToSupply").document(getCropToUpdate);
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


                            //update the current crop in local database
                            database.mainDao().updateWareCropAmount(selectedItem, newAmount);
                            wareCrops.clear();
                            wareCrops.addAll((database.mainDao().getAllWareCrops()));

                        }
                        CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, FarmerWare.this);
                        simpleList.setAdapter(customAdapterCrops);

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    //remove item from the list
    public void removeWareCrop(int i) {
        //get the item name
        int selectedItemName = this.wareCrops.get(i).getId();
        String itemName = this.wareCrops.get(i).getWareCropName();

        //create an alert dialog to make sure the user want to delete this item.
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove)
                .setMessage(getString(R.string.areYouSureRemove) + " " + itemName + getString(R.string.questionMark))

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String remove = getString(R.string.removeFromList);


                        //delete the item from firestore
                        String temp = mail + itemName.toLowerCase();
                        db.collection("readyToSupply").document(temp)
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
                        database.mainDao().delete(wareCrops.get(i));
                        wareCrops.remove(i);

                        //load the listview after the update

                        CustomAdapterCrops customAdapterCrops = new CustomAdapterCrops(getApplicationContext(), wareCrops, FarmerWare.this);
                        simpleList.setAdapter(customAdapterCrops);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLogic.getInstance().onOptionsItemSelected(item, context, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuLogic.getInstance().onCreateOptionsMenu(menu, context, this);

    }


}
