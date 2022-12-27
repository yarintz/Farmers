package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
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

public class CropsInformation extends AppCompatActivity {
    private Context context;
    private Menu menu;
    private TableLayout cropTable;
    static CropsInformation instance;
    private Button addNew;
    private EditText newCropInformationNameEditText;
    private EditText newCropInformationAmountEditText;
    private TextView newCropInformationNameTextView;
    private TextView newCropInformationAmountTextView;
    private EditText newCropInformationSeasonEditText;
    private TextView newCropInformationSeasonTextView;
    private Button cancel;
    private Button approve;
    ListView simpleList;
    private boolean connected = false;
    private ArrayList<CropsTable> crops = null;
    RoomDB database;

    //for database uses:
    private FirebaseFirestore db;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = CropsInformation.this;
        instance = this;
        setContentView(R.layout.activity_crops_information);

        //create the database instance and the arraylist of the items with relevant information
        database = RoomDB.getInstance(this);
        crops = new ArrayList<CropsTable>(database.mainDao().getAllCrops());

        //for firestore:
        db = FirebaseFirestore.getInstance();

        //get a hold on all the items in our screen.
        simpleList = (ListView) findViewById(R.id.cropsInformationListView);
        addNew = (Button) findViewById(R.id.addNewCropInformation);
        newCropInformationNameTextView = (TextView) findViewById(R.id.newCropInformationNameTextView);
        newCropInformationSeasonTextView = (TextView) findViewById(R.id.newCropInformationSeasonTextView);
        newCropInformationAmountTextView = (TextView) findViewById(R.id.newCropInformationTimeTextView);
        newCropInformationNameEditText = (EditText) findViewById(R.id.newCropInformationNameEditText);
        newCropInformationAmountEditText = (EditText) findViewById(R.id.newCropInformationTimeEditText);
        newCropInformationSeasonEditText = (EditText) findViewById(R.id.newCropInformationSeasonEditText);
        cancel = (Button) findViewById(R.id.cancelNewCropInformation);
        approve = (Button) findViewById(R.id.aproveActionCropInformation);


        //make all irrelevant items invisible as they are not yet needed.
        newCropInformationNameEditText.setVisibility(View.GONE);
        newCropInformationAmountEditText.setVisibility(View.GONE);
        newCropInformationSeasonEditText.setVisibility(View.GONE);
        newCropInformationNameTextView.setVisibility(View.GONE);
        newCropInformationAmountTextView.setVisibility(View.GONE);
        newCropInformationSeasonTextView.setVisibility(View.GONE);
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
                    db.collection("cropsInformation")
                            .whereEqualTo("userMail", mail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        database.mainDao().nukeTableCrops();
                                        crops.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            CropsTable tempCropsInformation = new CropsTable();
                                            if (document.get("cropName") != null) {
                                                tempCropsInformation.setCropName(document.get("cropName").toString());
                                            }
                                            tempCropsInformation.setSeason(document.get("season").toString());
                                            tempCropsInformation.setMonthsPlantedTime(Integer.parseInt(document.get("monthsPlantedTime").toString()));
                                            database.mainDao().insert(tempCropsInformation);

                                        }
                                        crops.addAll(database.mainDao().getAllCrops());
                                    }

                                    //load items in the list view.
                                    CustomeAdapterCropsInformation customAdapterCropsInformation = new CustomeAdapterCropsInformation(getApplicationContext(), crops, CropsInformation.this);
                                    simpleList.setAdapter(customAdapterCropsInformation);
                                }

                            });




                    //if canceled the process then reset all textviews and make all relevant items invisible.
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            newCropInformationNameEditText.setText("");
                            newCropInformationAmountEditText.setText("");
                            newCropInformationSeasonEditText.setText("");

                            newCropInformationNameEditText.setVisibility(View.GONE);
                            newCropInformationAmountEditText.setVisibility(View.GONE);
                            newCropInformationSeasonEditText.setVisibility(View.GONE);
                            newCropInformationNameTextView.setVisibility(View.GONE);
                            newCropInformationAmountTextView.setVisibility(View.GONE);
                            newCropInformationSeasonTextView.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            approve.setVisibility(View.GONE);
                        }
                    });

                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Take the Edit text text and make them a string so we can create a new crop instance
                            String newCropName = newCropInformationNameEditText.getText().toString();
                            String newCropTime = newCropInformationAmountEditText.getText().toString();
                            String newCropSeason = newCropInformationSeasonEditText.getText().toString();
                            CropsTable ct = new CropsTable();

                            Snackbar sb = null;

                            //check that the user added information to all fields.
                            if ((newCropName == "") || (newCropTime == "") || (newCropSeason == "")) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.fillAllFields, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                                return;
                                //check if something that is not number entered to months field.
                            } else if (!newCropTime.matches("[0-9]+")) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.timeNumbersOnly, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                                return;
                                //check that the season entered is one of the 4 seasons.
                            } else if (!(newCropSeason.equalsIgnoreCase("SUMMER") || newCropSeason.equalsIgnoreCase("SPRING") ||
                                    newCropSeason.equalsIgnoreCase("WINTER") || newCropSeason.equalsIgnoreCase("Fall"))) {
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.onlySeasonsAllowd, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                                return;
                                // if passed all checks can create the new item and add it to the crops list
                            } else {

                                // process succeeded now need to delete data from edit text.
                                newCropInformationNameEditText.setText("");
                                newCropInformationAmountEditText.setText("");
                                newCropInformationSeasonEditText.setText("");

                                //make all items invisible because they are not needed unless user wanted to add another item.
                                newCropInformationNameEditText.setVisibility(View.GONE);
                                newCropInformationAmountEditText.setVisibility(View.GONE);
                                newCropInformationSeasonEditText.setVisibility(View.GONE);
                                newCropInformationNameTextView.setVisibility(View.GONE);
                                newCropInformationAmountTextView.setVisibility(View.GONE);
                                newCropInformationSeasonTextView.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);
                                approve.setVisibility(View.GONE);


                                //add the crop inforamtion to firebase
                                String currentCropInformation = mail + newCropName;
                                Map<String, Object> crop = new HashMap<>();
                                CollectionReference cropsRef = db.collection("cropsInformation");
                                crop.put("cropName", newCropName);
                                crop.put("monthsPlantedTime", Integer.parseInt(newCropTime));
                                crop.put("season", newCropSeason.toLowerCase());
                                crop.put("userMail", mail);

                                //String farmPlotString = mail + String.valueOf(row) + String.valueOf(col);


                                Task task2 = cropsRef.document(currentCropInformation).set(crop);


                                //add the text from the edit text to the item
                                ct.setCropName(newCropName);
                                ct.setMonthsPlantedTime(Integer.parseInt(newCropTime));
                                ct.setSeason(newCropSeason.toLowerCase());


                                //add the new created item to teh db and the arraylist
                                database.mainDao().insert(ct);
                                crops.clear();
                                crops.addAll((database.mainDao().getAllCrops()));

                                //load the data to the listview
                                CustomeAdapterCropsInformation customAdapterCropsInformation = new CustomeAdapterCropsInformation(getApplicationContext(), crops, CropsInformation.this);
                                simpleList.setAdapter(customAdapterCropsInformation);

                                //show success message
                                sb = Snackbar.make(instance.findViewById(android.R.id.content), R.string.addedNewCropSuccesfully, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();

                            }
                        }
                    });
                }
            });
        }
        else{
            CustomeAdapterCropsInformation customAdapterCropsInformation = new CustomeAdapterCropsInformation(getApplicationContext(), crops, CropsInformation.this);
            simpleList.setAdapter(customAdapterCropsInformation);
        }
        //add new button make all relevant fields visible so the user can add a new item as requested.
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    newCropInformationNameEditText.setVisibility(View.VISIBLE);
                    newCropInformationAmountEditText.setVisibility(View.VISIBLE);
                    newCropInformationSeasonEditText.setVisibility(View.VISIBLE);
                    newCropInformationNameTextView.setVisibility(View.VISIBLE);
                    newCropInformationAmountTextView.setVisibility(View.VISIBLE);
                    newCropInformationSeasonTextView.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    approve.setVisibility(View.VISIBLE);
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

    public void removeCropInformation(int i) {
        //get the item name
        String selectedItemName = this.crops.get(i).getCropName();

        //create an alert dialog to make sure the user want to delete this item.
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove)
                .setMessage(getString(R.string.areYouSureRemove) + " " + selectedItemName + getString(R.string.questionMark))

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String remove = getString(R.string.removeFromList);

                        //create a snackbar that informe the user the delete proccess has succeeded.
                        Snackbar sb = null;
                        sb = Snackbar.make(instance.findViewById(android.R.id.content), selectedItemName + " " + remove, Snackbar.LENGTH_LONG);
                        sb.setDuration(4500);
                        sb.show();

                        //delete the item from firestore
                        String temp = mail + selectedItemName;
                        db.collection("cropsInformation").document(temp)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println( "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Error deleting document");
                                    }
                                });

                        //delete the item from the database and from the arraylist.
                        database.mainDao().delete(crops.get(i));
                        crops.remove(i);

                        //load the listview after the update
                        CustomeAdapterCropsInformation customeAdapterCropsInformation = new CustomeAdapterCropsInformation(getApplicationContext(), crops, CropsInformation.this);
                        simpleList.setAdapter(customeAdapterCropsInformation);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();

    }

    //call menu class to give functunallity to all buttons on menu.
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLogic.getInstance().onOptionsItemSelected(item, context, this);
    }

    //call menu class to present the menu to the user.
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuLogic.getInstance().onCreateOptionsMenu(menu, context, this);
    }
}







