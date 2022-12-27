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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class SuppliersRelations extends AppCompatActivity {


    static SuppliersRelations instance;
    private Button addNew;
    private EditText newCropNameEditText;
    private EditText newCropAmountEditText;
    private TextView newCropNameTextView;
    private TextView newCropAmountTextView;
    private Button cancel;
    private Button approve;
    private Menu menu;
    private Context context = null;
    private ArrayList<AllSuppliersItemsTable> items = null;
    private RoomDB database;
    private boolean connected = false;

    // Array of suppliers and their requests
    ListView simpleList;

    //for database uses:
    private FirebaseFirestore db;

   // ArrayList<String> suppliers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
        this.context = this;
        database = RoomDB.getInstance(this);
        //for firestore:
        db = FirebaseFirestore.getInstance();
        //items = new ArrayList<>(database.mainDao().getAllsupplierItems());          ( local db)
        items = new ArrayList(database.mainDao().getAllsupplierItemsForFarmer());
        setContentView(R.layout.activity_suppliers_relations);
        simpleList = (ListView) findViewById(R.id.suppliersPricesListView);
        simpleList.setBackgroundColor(Color.LTGRAY);
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
//        CustomAdapterSuppliersRealtions customAdapterSuppliersRealtions = new CustomAdapterSuppliersRealtions(getApplicationContext(), items, this);
//        simpleList.setAdapter(customAdapterSuppliersRealtions);
        if(connected) {
            db.collection("supplierPrices")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                database.mainDao().nukeTableAllSupplier();
                                items.clear();

                                //runs on all ducoments with user mail equal to our users and load it to database
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    AllSuppliersItemsTable allSuppliersItemsTable = new AllSuppliersItemsTable();
                                    allSuppliersItemsTable.setCropName(document.get("cropname").toString());
                                    allSuppliersItemsTable.setPrice((document.get("price").toString()));
                                    allSuppliersItemsTable.setMail(document.get("suppliermail").toString());
                                    allSuppliersItemsTable.setName(document.get("suppliername").toString());
                                    allSuppliersItemsTable.setPhone(document.get("suppliephone").toString());
                                    database.mainDao().insert(allSuppliersItemsTable);

                                }
                                items.addAll(database.mainDao().getAllsupplierItemsForFarmer());
                            }

                            CustomAdapterSuppliersRealtions customAdapterSuppliersRealtions = new CustomAdapterSuppliersRealtions(getApplicationContext(), items, SuppliersRelations.this);
                            simpleList.setAdapter(customAdapterSuppliersRealtions);
                        }
                    });
        }
        else{
            CustomAdapterSuppliersRealtions customAdapterSuppliersRealtions = new CustomAdapterSuppliersRealtions(getApplicationContext(), items, SuppliersRelations.this);
            simpleList.setAdapter(customAdapterSuppliersRealtions);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLogic.getInstance().onOptionsItemSelected(item, context,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return MenuLogic.getInstance().onCreateOptionsMenu(menu, context,this);

    }

}
