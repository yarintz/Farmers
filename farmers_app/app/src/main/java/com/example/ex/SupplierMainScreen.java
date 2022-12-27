package com.example.ex1_205790488_315680397;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

public class SupplierMainScreen extends AppCompatActivity {

    //for screen manipulations.
    static SupplierMainScreen instance;
    private Context context;

    //holding screen items.
    private Button addNew;
    private EditText newItemNameEditText;
    private EditText newItemAmountEditText;
    private TextView newItemNameTextView;
    private TextView newItemAmountTextView;
    private Button cancel;
    private Button approve;
    private ImageButton exit;
    private boolean connected;
    //for database uses:
    private FirebaseFirestore db;
    private String mail;
    private String fullName;
    private String phoneNumber;

    //local db
    private ArrayList<SupplierTable> items = null;
    private RoomDB database;





    // Array of items the supplier want to purchase and their prices
    ListView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        context = this;
        setContentView(R.layout.activity_supplier_main_screen);

        //create the database instance and the arraylist of the items with relevant information
        database = RoomDB.getInstance(this);
        items = new ArrayList<>(database.mainDao().getAllsupplierItems());
        //for firestore:
        db = FirebaseFirestore.getInstance();

        simpleList = (ListView) findViewById(R.id.supplierMain);
        simpleList.setBackgroundColor(Color.LTGRAY);


        addNew = (Button) findViewById(R.id.addNewItem);

        newItemNameTextView = (TextView) findViewById(R.id.newItemNameTextView);
        newItemAmountTextView = (TextView) findViewById(R.id.newItemAmountTextView);
        newItemNameEditText = (EditText) findViewById(R.id.newItemNameEditText);
        newItemAmountEditText = (EditText) findViewById(R.id.newItemAmountEditText);
        cancel = (Button) findViewById(R.id.cancelNewItem);
        approve = (Button) findViewById(R.id.aproveActionNewItem);
        exit = (ImageButton) findViewById(R.id.supplierLogOut);
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

        setVisibleButtons(false);
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
                            fullName = document.get("Name").toString() + " " + document.get("Surname").toString();
                            phoneNumber = document.get("Phone number").toString();
                        }
                    }
                    db.collection("supplierPrices")
                            .whereEqualTo("suppliermail", mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        database.mainDao().nukeTableSupplier();
                                        items.clear();

                                        //runs on all ducoments with user mail equal to our users and load it to database
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            SupplierTable tempSupplierTable = new SupplierTable();
                                            tempSupplierTable.setCropName(document.get("cropname").toString());
                                            tempSupplierTable.setPrice((document.get("price").toString()));
                                            database.mainDao().insert(tempSupplierTable);

                                        }
                                        items.addAll(database.mainDao().getAllsupplierItems());
                                    }

                                    CustomeAdapterSuppliersScreen customeAdapterSuppliersScreen = new CustomeAdapterSuppliersScreen(getApplicationContext(), items, SupplierMainScreen.this);
                                    simpleList.setAdapter(customeAdapterSuppliersScreen);
                                }
                            });




                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            newItemNameEditText.setText("");
                            newItemAmountEditText.setText("");

                            setVisibleButtons(false);
                        }
                    });

                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String newCropName = newItemNameEditText.getText().toString().toLowerCase();
                            String newCropAmount = newItemAmountEditText.getText().toString();
                            SupplierTable st = new SupplierTable();
                            AllSuppliersItemsTable asit = new AllSuppliersItemsTable();
                            Snackbar sb = null;
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCropName().equals(newCropName)) {
                                    sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.itemExist, Snackbar.LENGTH_LONG);
                                    sb.setDuration(4500);
                                    sb.show();
                                    return;
                                }
                            }

                            if (!newCropAmount.matches("\\d+\\.?\\d*")) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                            } else {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.changedSuccessfully, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();

                                //   items.add(newCropName + ":      " + newCropAmount + "$");

                                newItemNameEditText.setText("");
                                newItemAmountEditText.setText("");

                                setVisibleButtons(false);

                                //add the text from the edit text to the item
                                st.setCropName(newCropName);
                                st.setPrice(newCropAmount);

                                //add information to the tables of all the suppliers
                                asit.setCropName(newCropName);
                                asit.setPrice(newCropAmount);
                                asit.setMail(mail);
                                asit.setName(fullName);
                                asit.setPhone(phoneNumber);
                                //add the crop inforamtion to firebase
                                String currentSupplierItem = mail + newCropName;
                                Map<String, Object> item = new HashMap<>();
                                CollectionReference itemRef = db.collection("supplierPrices");
                                item.put("cropname", newCropName.toLowerCase());
                                item.put("price", newCropAmount);
                                item.put("suppliermail", mail);
                                item.put("suppliername", fullName);
                                item.put("suppliephone", phoneNumber);

                                Task task2 = itemRef.document(currentSupplierItem).set(item);

                                //add the new created item to teh db and the arraylist
                                database.mainDao().insert(st);
                                database.mainDao().insert(asit);
                                ArrayList<AllSuppliersItemsTable> temp = new ArrayList(database.mainDao().getAllsupplierItemsForFarmer());

                                items.clear();
                                items.addAll((database.mainDao().getAllsupplierItems()));
                            }

                            CustomeAdapterSuppliersScreen customeAdapterSuppliersScreen = new CustomeAdapterSuppliersScreen(getApplicationContext(), items, SupplierMainScreen.this);
                            simpleList.setAdapter(customeAdapterSuppliersScreen);

                        }
                    });
                }
            });
        }
        else{
            CustomeAdapterSuppliersScreen customeAdapterSuppliersScreen = new CustomeAdapterSuppliersScreen(getApplicationContext(), items, SupplierMainScreen.this);
            simpleList.setAdapter(customeAdapterSuppliersScreen);
        }
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    setVisibleButtons(true);
                }
            }
        });
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

    public static SupplierMainScreen getInstance() {
        return instance;
    }

    // control if we see the add items buttons and labels - true means we want to see them false means not.
    public void setVisibleButtons(boolean bool){
        if(bool == false){
            newItemNameTextView.setVisibility(View.GONE);
            newItemAmountTextView.setVisibility(View.GONE);
            newItemNameEditText.setVisibility(View.GONE);
            newItemAmountEditText.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            approve.setVisibility(View.GONE);
        }
        if(bool == true){
            newItemNameTextView.setVisibility(View.VISIBLE);
            newItemAmountTextView.setVisibility(View.VISIBLE);
            newItemNameEditText.setVisibility(View.VISIBLE);
            newItemAmountEditText.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            approve.setVisibility(View.VISIBLE);
        }
    }
    //set data according to the price the user inserted
    public void setItemsPrice(int i, String newPrice) {
      //  String selectedItem = items.get(i);
       // String[] tempArray = selectedItem.split(":");
        int selectedItem = items.get(i).getId();
        String cropName = items.get(i).getCropName();
        if (newPrice.equals("")) {

            Snackbar sb = null;
            sb = Snackbar.make(this.findViewById(android.R.id.content), R.string.amountCantBeEmpty, Snackbar.LENGTH_LONG);
            sb.setDuration(4500);
            sb.show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.updateAmount)
                .setMessage(getString(R.string.areYouSureSetAmount) + " " + newPrice +" " + getString(R.string.asTheNewPrice) + " "+ cropName+ getString(R.string.questionMark))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Snackbar sb = null;

                        if (!newPrice.matches("\\d+\\.?\\d*")) {
                            sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                            sb.setDuration(4500);
                            sb.show();
                        }
                        else{
                            //update the current crop in firebase database
                            String getCropToUpdate = mail + cropName;
                            DocumentReference docRef = db.collection("supplierPrices").document(getCropToUpdate);
                            docRef.update("price", newPrice)
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



                            database.mainDao().updateSupplierCropPrice(selectedItem, newPrice);
                            database.mainDao().updateAllSupplierCropPrice(mail, cropName, newPrice);
                            items.clear();
                            items.addAll((database.mainDao().getAllsupplierItems()));
                        }
                        CustomeAdapterSuppliersScreen customeAdapterSuppliersScreen = new CustomeAdapterSuppliersScreen(getApplicationContext(), items, SupplierMainScreen.this);
                        simpleList.setAdapter(customeAdapterSuppliersScreen);

                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();




    }
    //Remove item after supplier clicked remove
    public void removeItem(int i) {


        int selectedItemName = this.items.get(i).getId();
        String itemName = this.items.get(i).getCropName();
        String price = this.items.get(i). getPrice();

        new AlertDialog.Builder(this)
                .setTitle(R.string.remove)
                .setMessage(getString(R.string.areYouSureRemove) + " " + itemName + getString(R.string.questionMark))

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String remove = getString(R.string.removeFromList);

                        //delete the item from firestore
                        String temp = mail + itemName.toLowerCase();
                        db.collection("supplierPrices").document(temp)
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
                        //delete from database of all suppliers

                        database.mainDao().deleteAllSupplierItemsTable(mail, itemName);

                        //delete the item from the database and from the arraylist.
                        database.mainDao().delete(items.get(i));
                        items.remove(i);


                        CustomeAdapterSuppliersScreen customeAdapterSuppliersScreen = new CustomeAdapterSuppliersScreen(getApplicationContext(), items, SupplierMainScreen.this);
                        simpleList.setAdapter(customeAdapterSuppliersScreen);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();

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
}