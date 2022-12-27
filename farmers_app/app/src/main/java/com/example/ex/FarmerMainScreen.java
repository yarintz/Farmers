package com.example.ex1_205790488_315680397;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FarmerMainScreen extends AppCompatActivity {

    private TextView welcomeFarmerName;
    private RecyclerView farmLayoutRecycler;
    private GridLayoutManager farmLayoutGridManager;
    static FarmerMainScreen instance;
    protected FarmPlot fp = null;
    private FragmentManager fm;
    private Menu menu;
    private Context context = null;
    private RoomDB database;
    private ArrayList<PlotTable> plotTables;
    private int farmRows = 5;
    private int farmColumns = 5;
    private Button setSize;
    private boolean connected = false;
    ArrayList<String> columnNumbersArrayList = new ArrayList<>();


    //for database uses:
    private FirebaseFirestore db;
    private String mail;
    private ArrayList<DocumentSnapshot> plotsToAddToFarm;
    private String tempFarmerPlotString;
    private boolean flagForNewUserLogIn = false;
    private int currenRow=0;
    private int currentColumn=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_main_screen);
        instance = this;
        this.context = this;
        fm = getFragmentManager();


        // cropsToSell.addAll(Arrays.asList(cropsToSellArray));
        database = RoomDB.getInstance(this);
        plotTables = new ArrayList<>(database.mainDao().getAllPlots());
        tempFarmerPlotString = null;
        setSize = (Button) findViewById(R.id.setSizeButton);


        //for firestore:
        db = FirebaseFirestore.getInstance();
        plotsToAddToFarm = new ArrayList<>();

        farmLayoutRecycler = (RecyclerView) findViewById(R.id.cardListFarmer);

        //check if there is connection to internet
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
                            String farmerFarmString = mail + " farm";
                            DocumentReference docRefFarmmerFarm = db.collection("farmerFarm").document(farmerFarmString);
                            docRefFarmmerFarm.get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                    if (task2.isSuccessful()) {
                                        DocumentSnapshot document = task2.getResult();
                                        if (document.get("farmPlot") == null) {
                                            currenRow = document.getLong("row").intValue();
                                            currentColumn = document.getLong("column").intValue();
                                            farmRows = document.getLong("row").intValue();
                                            farmColumns = document.getLong("column").intValue();
                                            fp = new FarmPlot(createNewList(farmColumns * farmRows));
                                            farmLayoutRecycler.setAdapter(fp);

                                        }
                                        //if the user has already have a farm in firestore
                                        else {

                                            db.collection("farmPlot")
                                                    .whereEqualTo("usermail", mail)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                database.mainDao().nukeTablePlots();
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    PlotTable tempPlotTable = new PlotTable();
                                                                    tempPlotTable.setPlantedCropName(document.get("cropName").toString());
                                                                    tempPlotTable.setColumn(Integer.parseInt(document.get("column").toString()));
                                                                    tempPlotTable.setRow(Integer.parseInt(document.get("row").toString()));
                                                                    if (document.getString("dateOfHarvest") == null) {
                                                                        tempPlotTable.setDateOfHarvest(0);
                                                                    } else {
                                                                        SimpleDateFormat f = new SimpleDateFormat("dd/mm/yy");
                                                                        try {
                                                                            String temp = document.getString("dateOfHarvest");
                                                                            Date d = f.parse(temp);
                                                                            long milliseconds = d.getTime();
                                                                            tempPlotTable.setDateOfHarvest(milliseconds);
                                                                        } catch (ParseException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                    tempPlotTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                                                    database.mainDao().insert(tempPlotTable);

                                                                }
                                                                plotTables.addAll(database.mainDao().getAllPlots());
                                                            }

                                                            int maxRow = 0;
                                                            int maxCol = 0;
                                                            for (PlotTable pt : plotTables) {
                                                                if (maxRow < pt.getRow())
                                                                    maxRow = pt.getRow();
                                                                if (maxCol < pt.getColumn())
                                                                    maxCol = pt.getColumn();
                                                            }
                                                            farmRows = maxRow;
                                                            farmColumns = maxCol;
                                                            farmLayoutGridManager = new GridLayoutManager(context, farmColumns, RecyclerView.VERTICAL, false);
                                                            farmLayoutRecycler.setLayoutManager(farmLayoutGridManager);
                                                            fp = new FarmPlot(createList(farmColumns * farmRows));
                                                            farmLayoutRecycler.setAdapter(fp);
                                                        }
                                                    });


                                        }
                                    }

                                }
                            });



                            Resources res = context.getResources();
                            String[] columnNumbersArray = res.getStringArray(R.array.columnNumbers);
                            columnNumbersArrayList = new ArrayList<>(Arrays.asList(columnNumbersArray));

                               }
                    }
                }
            });
        }
        else{
            int maxRow = 0;
            int maxCol = 0;
            for (PlotTable pt : plotTables) {
                if (maxRow < pt.getRow())
                    maxRow = pt.getRow();
                if (maxCol < pt.getColumn())
                    maxCol = pt.getColumn();
            }
            farmRows = maxRow;
            farmColumns = maxCol;
            farmLayoutGridManager = new GridLayoutManager(context, farmColumns, RecyclerView.VERTICAL, false);
            farmLayoutRecycler.setLayoutManager(farmLayoutGridManager);
            fp = new FarmPlot(createList(farmColumns * farmRows));
            farmLayoutRecycler.setAdapter(fp);
        }
        setSize.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if (checkConnection()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FarmerMainScreen.getInstance());
                    alertDialog.setTitle(R.string.setFarmSize);
                    alertDialog.setMessage(R.string.pleaseEnterRowsAndCols);


                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    TableLayout layout = new TableLayout(FarmerMainScreen.getInstance());
                    layout.setOrientation(TableLayout.VERTICAL);
                    TableRow tablerow1 = new TableRow(FarmerMainScreen.getInstance());

                    // Add a TextView here for the "Title" label, as noted in the comments
                    final TextView columns = new TextView(FarmerMainScreen.getInstance());
                    columns.setText(R.string.columns);
                    tablerow1.addView(columns, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    // Notice this is an add method

                    // Add a TextView here for the "Title" label, as noted in the comments
                    final Spinner columnsNumber = new Spinner(FarmerMainScreen.getInstance());
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> spinnerArrayAdapter =
                            new ArrayAdapter<String>(FarmerMainScreen.getInstance(), android.R.layout.simple_spinner_item, columnNumbersArrayList);
                    // Specify the layout to use when the list of choices appears
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    columnsNumber.setAdapter(spinnerArrayAdapter);
                    tablerow1.addView(columnsNumber, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));// Notice this is an add method

                    layout.addView(tablerow1);

                    TableRow tablerow2 = new TableRow(FarmerMainScreen.getInstance());

                    final TextView row = new TextView(FarmerMainScreen.getInstance());
                    row.setText(R.string.row);
                    tablerow2.addView(row, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));// Notice this is an add method

                    // Add another TextView here for the "Description" label
                    final EditText descriptionBox = new EditText(FarmerMainScreen.getInstance());
                    tablerow2.addView(descriptionBox, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Another add method

                    layout.addView(tablerow2);
                    alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            farmColumns = Integer.parseInt(columnsNumber.getSelectedItem().toString());
                            if (!descriptionBox.getText().toString().matches("[0-9]+")) {
                                Snackbar sb = Snackbar.make(view, R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                sb.setDuration(4500);
                                sb.show();
                                return;
                            }
                            farmRows = Integer.parseInt(descriptionBox.getText().toString());
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(FarmerMainScreen.getInstance());
                            alertDialog2.setTitle(R.string.setFarmSize);
                            alertDialog2.setMessage(R.string.notice);
                            alertDialog2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String farmerFarmString = mail + " farm";
                                    DocumentReference docRefFarmmerFarm = db.collection("farmerFarm").document(farmerFarmString);
                                    docRefFarmmerFarm.get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                            if (task2.isSuccessful()) {
                                                DocumentSnapshot document = task2.getResult();
                                                currenRow = document.getLong("row").intValue();
                                                currentColumn = document.getLong("column").intValue();
                                                docRefFarmmerFarm.update("row", farmRows);
                                                docRefFarmmerFarm.update("column", farmColumns);
//                                                            ArrayList array = new ArrayList();
                                                docRefFarmmerFarm.update("farmPlot", null);
                                                farmLayoutGridManager = new GridLayoutManager(FarmerMainScreen.getInstance(), farmColumns, RecyclerView.VERTICAL, false);
                                                farmLayoutRecycler.setLayoutManager(farmLayoutGridManager);
                                                flagForNewUserLogIn = true;

                                                // we need to delete unnecessary documents so we call to create new list to create the new farm and delete previous documnets.
                                                fp = new FarmPlot(createNewList(farmColumns * farmRows));
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        }
                                    });

                                }
                            });

                            alertDialog2.setNegativeButton(android.R.string.cancel, null);
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            alertDialog2.show();
                        }
                    });
                    alertDialog.setNegativeButton(android.R.string.no, null);
                    alertDialog.setView(layout); // Again this is a set method, not add
                    alertDialog.show();
                }
            }
        });

    }


    public static FarmerMainScreen getInstance() {
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

    //for edit dialog fragment
    public void showCustomAlertDialog(View view, PlotInfo pl) {
        if(checkConnection()) {
            // frag = new FarmerEditDialog();
            FarmerEditDialog frag = new FarmerEditDialog();
            frag.setPlotInfo(pl);
            Bundle args = new Bundle();
            args.putInt("title", R.string.alertDialogTwoButtonsTitle);
            frag.setArguments(args);
            frag.show(getFragmentManager(), "dialog");
            database = RoomDB.getInstance(this);
        }
    }


    //create list of crops
    private List<PlotInfo> createNewList(int size) {
        int row = 1;
        int col = 1;
        database.mainDao().resetPlots(plotTables);
        if (flagForNewUserLogIn) {
            for (int j = 1; j <= currenRow*currentColumn; j++){
                String temp = mail + String.valueOf(row) + String.valueOf(col);
                db.collection("farmPlot").document(temp)
                        .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println( "DocumentSnapshot successfully deleted!" + temp + " Current row and current columns are " + currenRow*currentColumn);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Error deleting document");
                            }
                        });
                if (col == currentColumn) {
                    col = 1;
                    row = row + 1;
                } else
                    col++;
        }
            plotsToAddToFarm = new ArrayList<>();
    }
        flagForNewUserLogIn = false;
        row = 1;
        col = 1;
        List<PlotInfo> result = new ArrayList<PlotInfo>();
        Map<String, Object> plot = new HashMap<>();
        CollectionReference plots = db.collection("farmPlot");

        for (int i = 0; i < size; i++) {
            PlotTable pt = new PlotTable();
            PlotInfo pi = new PlotInfo();
            pt.setPlantedCropName("EMPTY");
            pt.setAmount(0);
            pt.setDateOfHarvest(0);

            pi.name = "EMPTY";
            pi.cropDate = "";
            pi.location = i;
            pi.row = row;
            pi.column = col;


            plot.put("amount", 0);
            plot.put("column", col);
            plot.put("cropName", "EMPTY");
            plot.put("dateOfHarvest", null);
            plot.put("row", row);
            String farmPlotString = mail + String.valueOf(row) + String.valueOf(col);
            plot.put("usermail", mail);

            Task task2 = plots.document(farmPlotString).set(plot);

            DocumentReference docRef = db.collection("farmPlot").document(farmPlotString);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        plotsToAddToFarm.add(document);
                        //  tempFarmerPlotString = document.getId();
                    }
                }
            });

            pt.setRow(row);
            pt.setColumn(col);
            if (col == farmColumns) {
                col = 1;
                row = row + 1;
            } else
                col++;
            plotTables.add(pt);
            database.mainDao().insert(pt);
            result.add(pi);
        }
        insertPlotsToFarm();


        //add the new created item to teh db and the arraylist
        plotTables.clear();
        plotTables.addAll((database.mainDao().getAllPlots()));
        return result;
    }

    //create list of crops
    private List<PlotInfo> createList(int size) {
       List<PlotInfo> result = new ArrayList<PlotInfo>();
        for (int i = 0; i < size; i++) {
            PlotTable pt = plotTables.get(i);
            PlotInfo pi = new PlotInfo();

            pi.name = pt.getPlantedCropName();
            if (pt.getDateOfHarvest() == 0) {
                pi.cropDate = "";
            } else {
                String dateString = new SimpleDateFormat("dd/mm/yy").format(pt.getDateOfHarvest());
                pi.cropDate = dateString;
            }
            pi.location = i;
            pi.row = plotTables.get(i).getRow();
            pi.column =  plotTables.get(i).getColumn();


            result.add(pi);
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

    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
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

    public void insertPlotsToFarm() {
        String farmerFarmString = mail + " farm";
        DocumentReference docRefFarmmerFarm = db.collection("farmerFarm").document(farmerFarmString);
        docRefFarmmerFarm.get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot document = task2.getResult();
//                            if (document.get("farmPlot") == null ) {
//                                farmRows = Integer.parseInt(document.get("row").toString());
//                                farmColumns = Integer.parseInt(document.get("column").toString());
                                //  List<PlotInfo> plotInfoForFB = createNewList(farmRows * farmColumns);


                                //   DocumentReference docRefFarmmerFarm = db.collection("farmerPlot").document(tempFarmerPlotString);
                                docRefFarmmerFarm.get().addOnCompleteListener((Activity) context, new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task3) {
                                        docRefFarmmerFarm.update("farmPlot", plotsToAddToFarm);
                                    }
                                });
//                            }
                        }
                    }
                }

        );
        makeFarm();
    }

    public void makeFarm() {
        farmLayoutGridManager = new GridLayoutManager(context, farmColumns, RecyclerView.VERTICAL, false);
        farmLayoutRecycler.setLayoutManager(farmLayoutGridManager);

    }
}