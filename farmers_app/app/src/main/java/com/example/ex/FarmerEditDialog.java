package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ParseException;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FarmerEditDialog extends DialogFragment {


    private Button setNameBtn;
    private Button setDateBtn;
    private Button harvestBtn;
    private Button plantBtn;
    private EditText setNameField;
    private Activity activity;
    private PlotInfo plotInfo = null;
    private Calendar myCalendar;
    private Spinner plantField;
    private EditText plantFieldAmount;
    private ArrayList<SeedsTable> seeds = null;
    private RoomDB database;
    private Spinner spinner;
    ArrayList<String> seedsNames = new ArrayList<>();
    private ArrayList<PlotTable> plotTables;
    private ArrayList<WareTable> wareTable;
    private ArrayList<CropsTable> cropsTable;

    //for fire base date
    private String newDateForFireBase;
    private Date newDateForRoomDB;

    public void setPlotInfo(PlotInfo pl) {
        this.plotInfo = pl;
    }

    private int seedLocation;

    //for firestore database
    private String mail;
    private FirebaseFirestore db;

    // Use this instance of the interface to deliver action events
    //
    MyAlertDialogFarmerEdit mListener;

    // Override the Fragment.onAttach() method to instantiate the MyAlertDialogFragmentListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MyAlertDialogFragmentListener so we can send events to the host
            mListener = (MyAlertDialogFarmerEdit) activity;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // View v = inflater.inflate(R.layout.activity_fragment_dialog_farmer_edit, null);
        View v = inflater.inflate(R.layout.activity_fragment_dialog_farmer_edit, null);
        setNameBtn = (Button) v.findViewById(R.id.setAmountBtn);
        setDateBtn = (Button) v.findViewById(R.id.setDateBtn);
        harvestBtn = (Button) v.findViewById(R.id.harvestBtn);
        plantBtn = (Button) v.findViewById(R.id.plantBtn);
        setNameField = (EditText) v.findViewById(R.id.setAmountField);
        plantField = (Spinner) v.findViewById(R.id.plantField);
        plantFieldAmount = (EditText) v.findViewById(R.id.plantFieldAmount);
        //for firestore:
        db = FirebaseFirestore.getInstance();
        database = RoomDB.getInstance(v.getContext());

        //for firebase loading
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                mail = document.get("Mail").toString();
                //create the database instance and the arraylist of the items with relevant information
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
                                            } catch (java.text.ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        tempPlotTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                        database.mainDao().insert(tempPlotTable);

                                    }
                                }
                                plotTables = new ArrayList<>(database.mainDao().getAllPlots());
                                db.collection("cropsInformation")
                                        .whereEqualTo("userMail", mail)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    database.mainDao().nukeTableCrops();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        CropsTable tempCropsInformation = new CropsTable();
                                                        if (document.get("cropName") != null) {
                                                            tempCropsInformation.setCropName(document.get("cropName").toString());
                                                        }
                                                        tempCropsInformation.setSeason(document.get("season").toString());
                                                        tempCropsInformation.setMonthsPlantedTime(Integer.parseInt(document.get("monthsPlantedTime").toString()));
                                                        database.mainDao().insert(tempCropsInformation);

                                                    }
                                                }
                                                cropsTable = new ArrayList<>(database.mainDao().getAllCrops());

                                                db.collection("seeds")
                                                        .whereEqualTo("usermail", mail)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    database.mainDao().nukeTableSeeds();


                                                                    //runs on all ducoments with user mail equal to our users and load it to database
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        SeedsTable tempSeedsTable = new SeedsTable();
                                                                        tempSeedsTable.setSeedName(document.get("name").toString());
                                                                        tempSeedsTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                                                        database.mainDao().insert(tempSeedsTable);

                                                                    }
                                                                }


                                                                seeds = new ArrayList<>(database.mainDao().getAllSeeds());


                                                                db.collection("readyToSupply")
                                                                        .whereEqualTo("usermail", mail)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    database.mainDao().nukeTableWare();


                                                                                    //runs on all ducoments with user mail equal to our users and load it to database
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        WareTable tempWareTable = new WareTable();
                                                                                        tempWareTable.setWareCropName(document.get("name").toString());
                                                                                        tempWareTable.setAmount(Integer.parseInt(document.get("amount").toString()));
                                                                                        database.mainDao().insert(tempWareTable);

                                                                                    }
                                                                                }
                                                                                wareTable = new ArrayList<>(database.mainDao().getAllWareCrops());
                                                                                for (SeedsTable seed : seeds) {
                                                                                    seedsNames.add(seed.getSeedName());
                                                                                }

                                                                                setNameField.setHint(R.string.newCropNameHint);

                                                                                // Create an ArrayAdapter using the string array and a default spinner layout
                                                                                ArrayAdapter<String> spinnerArrayAdapter =
                                                                                        new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, seedsNames);
                                                                                // Specify the layout to use when the list of choices appears
                                                                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                // Apply the adapter to the spinner
                                                                                plantField.setAdapter(spinnerArrayAdapter);


                                                                                setNameBtn.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override

                                                                                    public void onClick(View view) {
                                                                                        String newName = getIdField();
                                                                                        int row = plotInfo.row;
                                                                                        int col = plotInfo.column;

                                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(FarmerMainScreen.getInstance());
                                                                                        builder.setTitle(R.string.setname);
                                                                                        builder.setMessage(getString(R.string.areYouSureSetName) + " " + plotInfo.name + " " + getString(R.string.smallTo) + " " + newName + getString(R.string.questionMark));

                                                                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                                                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                                                                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                Snackbar sb = null;
                                                                                                if (plotInfo.name.equals("EMPTY")) {
                                                                                                    sb = Snackbar.make(v, R.string.cantSetNameOnEmptyPlot, Snackbar.LENGTH_LONG);
                                                                                                    sb.setDuration(4500);
                                                                                                    sb.show();
                                                                                                    setNameField.setText("");
                                                                                                } else if (newName.equals("")) {
                                                                                                    sb = Snackbar.make(v, R.string.pleaseProvideCropName, Snackbar.LENGTH_LONG);
                                                                                                    sb.setDuration(4500);
                                                                                                    sb.show();
                                                                                                    setNameField.setText("");
                                                                                                } else {
                                                                                                    String key = mail + String.valueOf(row) + String.valueOf(col);
                                                                                                    DocumentReference docRefFarmmerFarm = db.collection("farmPlot").document(key);
                                                                                                    docRefFarmmerFarm.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                            if (task2.isSuccessful()) {
                                                                                                                DocumentSnapshot document = task2.getResult();
                                                                                                                docRefFarmmerFarm.update("cropName", newName);
                                                                                                                database.mainDao().updatePlantedCropName(plotTables.get(plotInfo.location).getId(), newName);
                                                                                                                plotTables.clear();
                                                                                                                plotTables.addAll((database.mainDao().getAllPlots()));
                                                                                                                plotInfo.name = newName;
                                                                                                                FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                                dismiss();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });

                                                                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                                                                        builder.setNegativeButton(android.R.string.no, null);
                                                                                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                                                                                        builder.show();
                                                                                    }
                                                                                });
                                                                                plantBtn.setOnClickListener(new View.OnClickListener() {

                                                                                    int row = plotInfo.row;
                                                                                    int col = plotInfo.column;
                                                                                    public void onClick(View view) {
                                                                                        if (seeds.isEmpty()) {
                                                                                            Snackbar sb = null;
                                                                                            sb = Snackbar.make(v, R.string.pleaseAddSeedsToYourSeedsInventory, Snackbar.LENGTH_LONG);
                                                                                            sb.setDuration(4500);
                                                                                            sb.show();
                                                                                            return;
                                                                                        }
                                                                                        SeedsTable seedsTable = seeds.get(seedLocation);
                                                                                        String newName = getIdPlantField();
                                                                                        Snackbar sb = null;
                                                                                        if (newName.equals("")) {
                                                                                            sb = Snackbar.make(v, R.string.pleaseProvideCropName, Snackbar.LENGTH_LONG);
                                                                                            sb.setDuration(4500);
                                                                                            sb.show();
                                                                                            setNameField.setText("");
                                                                                        } else if (!plotInfo.name.equals("EMPTY")) {
                                                                                            sb = Snackbar.make(v, R.string.cantPlantOnExistingCrop, Snackbar.LENGTH_LONG);
                                                                                            sb.setDuration(4500);
                                                                                            sb.show();
                                                                                            setNameField.setText("");
                                                                                        } else if (!plantFieldAmount.getText().toString().matches("[0-9]+")) {
                                                                                            sb = Snackbar.make(v, R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                                                                            sb.setDuration(4500);
                                                                                            sb.show();
                                                                                            return;
                                                                                        } else {
                                                                                            if (seedsTable.getAmount() < Integer.parseInt(plantFieldAmount.getText().toString())) {
                                                                                                sb = Snackbar.make(v, R.string.cantPlantThatMuch, Snackbar.LENGTH_LONG);
                                                                                                sb.setDuration(4500);
                                                                                                sb.show();
                                                                                                return;
                                                                                            }

                                                                                            int currentAmount = database.mainDao().getSeedAmount(newName);
                                                                                            int newAmount = currentAmount - Integer.parseInt(plantFieldAmount.getText().toString());
                                                                                            String finalAmount = String.valueOf(newAmount);
//                                                                                            database.mainDao().updateSeedsAmountByName(newName, finalAmount);
//                                                                                            seeds.clear();
//                                                                                            seeds.addAll((database.mainDao().getAllSeeds()));

                                                                                            database.mainDao().updatePlantedCropName(plotTables.get(plotInfo.location).getId(), newName);
                                                                                            plotTables.clear();
                                                                                            plotTables.addAll((database.mainDao().getAllPlots()));


                                                                                            int timeToHarvest = database.mainDao().getTimeToHarvest(newName);
                                                                                            boolean isCropInformationExist = false;

                                                                                            for (int i = 0; i < cropsTable.size(); i++) {
                                                                                                if (cropsTable.get(i).getCropName().equals(newName)) {

                                                                                                    isCropInformationExist = true;
                                                                                                }
                                                                                            }

                                                                                            if (isCropInformationExist == true) {


                                                                                                Calendar calendar = Calendar.getInstance();
                                                                                                calendar.add(Calendar.MONTH, timeToHarvest);
                                                                                                SimpleDateFormat formatForStringAddDate = new SimpleDateFormat("dd/MM/yy");
                                                                                                String formatted = formatForStringAddDate.format(calendar.getTime());
                                                                                                Date date = null;
                                                                                                try {
                                                                                                    date = new SimpleDateFormat("dd/mm/yy").parse(formatted);

                                                                                                } catch (java.text.ParseException e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                                String newDate = calendar.toString();
                                                                                                plotInfo.name = newName;
                                                                                                plotInfo.cropDate = formatted;
                                                                                                database.mainDao().updatePlantedCropDateOfHarvest(plotTables.get(plotInfo.location).getId(), date.getTime());
                                                                                            } else if (isCropInformationExist == false) {

                                                                                                plotInfo.name = newName;
                                                                                                plotInfo.cropDate = "";
                                                                                                database.mainDao().updatePlantedCropDateOfHarvest(plotTables.get(plotInfo.location).getId(), 0);
                                                                                            }
                                                                                            String key = mail + String.valueOf(row) + String.valueOf(col);
                                                                                            DocumentReference docRefFarmmerFarm = db.collection("farmPlot").document(key);
                                                                                            docRefFarmmerFarm.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                    if (task2.isSuccessful()) {
                                                                                                        DocumentSnapshot document = task2.getResult();
                                                                                                        docRefFarmmerFarm.update("cropName", newName);
                                                                                                        docRefFarmmerFarm.update("amount", newAmount );
                                                                                                        docRefFarmmerFarm.update("dateOfHarvest", plotInfo.cropDate );
                                                                                                        database.mainDao().updatePlantedCropName(plotTables.get(plotInfo.location).getId(), newName);
                                                                                                        plotTables.clear();
                                                                                                        plotTables.addAll((database.mainDao().getAllPlots()));

                                                                                                        plotInfo.name = newName;
                                                                                                        FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                        dismiss();
                                                                                                    }
                                                                                                    plotTables.clear();
                                                                                                    plotTables.addAll((database.mainDao().getAllPlots()));
                                                                                                    Calendar cal = Calendar.getInstance();
                                                                                                    FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                    dismiss();
                                                                                                }
                                                                                            });
                                                                                            String keyForSeeds = mail+newName;
                                                                                            DocumentReference docRefSeed = db.collection("seeds").document(keyForSeeds);
                                                                                            docRefFarmmerFarm.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                    if (task2.isSuccessful()) {
                                                                                                        DocumentSnapshot document = task2.getResult();
                                                                                                        docRefSeed.update("amount", newAmount);
                                                                                                        database.mainDao().updateSeedsAmountByName(newName, finalAmount);
                                                                                                        seeds.clear();
                                                                                                        seeds.addAll((database.mainDao().getAllSeeds()));
                                                                                                    }

                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                });

                                                                                myCalendar = Calendar.getInstance();


                                                                                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                                                                    //set the time from the calendar
                                                                                    @Override
                                                                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                                                                          int dayOfMonth) {
                                                                                        myCalendar.set(Calendar.YEAR, year);
                                                                                        myCalendar.set(Calendar.MONTH, monthOfYear);
                                                                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                                                                                         newDateForFireBase = null;
                                                                                        try {
                                                                                            newDateForFireBase = sdf.format(myCalendar.getTime());
                                                                                        } catch (ParseException e1) {
                                                                                            e1.printStackTrace();

                                                                                        }
                                                                                        dismiss();
                                                                                         newDateForRoomDB = null;
                                                                                        try {
                                                                                            newDateForRoomDB = new SimpleDateFormat("dd/mm/yy").parse(newDateForFireBase);
                                                                                        } catch (java.text.ParseException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                        int row = plotInfo.row;
                                                                                        int col = plotInfo.column;
                                                                                        String key = mail + String.valueOf(row) + String.valueOf(col);
                                                                                        DocumentReference docRefFarmmerFarmSetDate = db.collection("farmPlot").document(key);
                                                                                        docRefFarmmerFarmSetDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                if (task2.isSuccessful()) {
                                                                                                    DocumentSnapshot document = task2.getResult();
                                                                                                    docRefFarmmerFarmSetDate.update("dateOfHarvest",  newDateForFireBase);
//
                                                                                                    database.mainDao().updatePlantedCropDateOfHarvest(plotTables.get(plotInfo.location).getId(),  newDateForRoomDB.getTime());
                                                                                                    plotTables.clear();
                                                                                                    plotTables.addAll((database.mainDao().getAllPlots()));
                                                                                                    FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                    dismiss();
                                                                                                }
                                                                                            }
                                                                                        });



                                                                                        plotInfo.cropDate = newDateForFireBase;
                                                                                        try {
                                                                                            plotInfo.setColor();
                                                                                        } catch (java.text.ParseException e) {
                                                                                            e.printStackTrace();
                                                                                        }

                                                                                        FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                    }


                                                                                };


                                                                                setDateBtn.setOnClickListener(new View.OnClickListener() {

                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Snackbar sb = null;
                                                                                        int row = plotInfo.row;
                                                                                        int col = plotInfo.column;
                                                                                        if (plotInfo.name.equals("EMPTY")) {
                                                                                            sb = Snackbar.make(v, R.string.cantChangeDateOnEmptyPlot, Snackbar.LENGTH_LONG);
                                                                                            sb.setDuration(4500);
                                                                                            sb.show();
                                                                                        } else {
                                                                                            DatePickerDialog temp = new DatePickerDialog(activity, date, myCalendar
                                                                                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                                                                    myCalendar.get(Calendar.DAY_OF_MONTH));
                                                                                            temp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                                                                            temp.show();

                                                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                                                                                           // SimpleDateFormat sdfFireBase = new SimpleDateFormat("dd/mm/yy");

                                                                                            //Date date = myCalendar.getTime();
                                                                                            String newDate = null;
                                                                                           // newDateForFireBase = null;
                                                                                            try {
                                                                                                //   date = new SimpleDateFormat("dd/MM/yy").parse(temp);
                                                                                                newDate = sdf.format(myCalendar.getTime());


                                                                                            } catch (ParseException e1) {
                                                                                                e1.printStackTrace();

                                                                                            }

                                                                                            String key = mail + String.valueOf(row) + String.valueOf(col);
                                                                                            DocumentReference docRefFarmmerFarmSetDate = db.collection("farmPlot").document(key);
                                                                                            docRefFarmmerFarmSetDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                    if (task2.isSuccessful()) {
                                                                                                        DocumentSnapshot document = task2.getResult();
                                                                                                        docRefFarmmerFarmSetDate.update("dateOfHarvest",  newDateForFireBase);

                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    }
                                                                                });


                                                                                harvestBtn.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View view) {
                                                                                        int row = plotInfo.row;
                                                                                        int col = plotInfo.column;
                                                                                        new AlertDialog.Builder(FarmerMainScreen.getInstance())
                                                                                                .setTitle(R.string.harvest)
                                                                                                .setMessage(getString(R.string.areYouSureHarvest) + " " + plotInfo.name + getString(R.string.questionMark))

                                                                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        Snackbar sb = null;
                                                                                                        if (plotInfo.name.equals("EMPTY")) {
                                                                                                            sb = Snackbar.make(v, R.string.cantHarvestEmptyPlot, Snackbar.LENGTH_LONG);
                                                                                                            sb.setDuration(4500);
                                                                                                            sb.show();
                                                                                                        } else {
                                                                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(FarmerMainScreen.getInstance());
                                                                                                            builder1.setTitle("How much kg did you harvest?");
                                                                                                            builder1.setMessage("Please enter harvested amount");
                                                                                                            final EditText input = new EditText(FarmerMainScreen.getInstance());
                                                                                                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                                                    LinearLayout.LayoutParams.MATCH_PARENT);
                                                                                                            input.setLayoutParams(lp);
                                                                                                            builder1.setView(input);
                                                                                                            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                                                                    String YouEditTextValue = input.getText().toString();
                                                                                                                    if (!YouEditTextValue.matches("[0-9]+")) {
                                                                                                                        Snackbar sb = null;
                                                                                                                        sb = Snackbar.make(FarmerMainScreen.getInstance().findViewById(android.R.id.content), R.string.onlyNumbersAllowd, Snackbar.LENGTH_LONG);
                                                                                                                        sb.setDuration(4500);
                                                                                                                        sb.show();
                                                                                                                        return;
                                                                                                                    }
                                                                                                                    int currentAmount = database.mainDao().getWareCropAmount(plotInfo.name);
                                                                                                                    int newWareAmount = currentAmount + Integer.parseInt(YouEditTextValue);
                                                                                                                    String finalWareAmount = String.valueOf(newWareAmount);
                                                                                                                    boolean isCropExist = false;
                                                                                                                    for (int i = 1; i < wareTable.size(); i++) {
                                                                                                                        if (wareTable.get(i).getWareCropName().equals(plotInfo.name)) {
                                                                                                                            isCropExist = true;
                                                                                                                        }
                                                                                                                    }
                                                                                                                    if (isCropExist == true) {
                                                                                                                        //update crop amount in firebase
                                                                                                                        String keyForWare = mail+plotInfo.name;
                                                                                                                        DocumentReference docRefWare = db.collection("readyToSupply").document(keyForWare);
                                                                                                                        docRefWare.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                                                if (task2.isSuccessful()) {
                                                                                                                                    DocumentSnapshot document = task2.getResult();
                                                                                                                                    docRefWare.update("amount", newWareAmount);
                                                                                                                                    database.mainDao().updateWareCropAmountByName(finalWareAmount, plotInfo.name);
                                                                                                                                    wareTable.clear();
                                                                                                                                    wareTable.addAll((database.mainDao().getAllWareCrops()));
                                                                                                                                }

                                                                                                                            }
                                                                                                                        });

                                                                                                                    }
                                                                                                                    if (isCropExist == false) {
                                                                                                                        //add the crop inforamtion to firebase
                                                                                                                        String currentFarmerWare = mail+plotInfo.name;
                                                                                                                        Map<String, Object> ware = new HashMap<>();
                                                                                                                        CollectionReference wareRef = db.collection("readyToSupply");
                                                                                                                        ware.put("name",plotInfo.name.toLowerCase() );
                                                                                                                        ware.put("amount", newWareAmount);
                                                                                                                        ware.put("usermail", mail);

                                                                                                                        //String farmPlotString = mail + String.valueOf(row) + String.valueOf(col);


                                                                                                                        Task task2 = wareRef.document(currentFarmerWare).set(ware);

                                                                                                                        WareTable wt = new WareTable();
                                                                                                                        wt.setWareCropName(plotInfo.name);
                                                                                                                        wt.setAmount(newWareAmount);
                                                                                                                        database.mainDao().insert(wt);
                                                                                                                        wareTable.clear();
                                                                                                                        wareTable.addAll((database.mainDao().getAllWareCrops()));
                                                                                                                    }
                                                                                                                    String key = mail + String.valueOf(row) + String.valueOf(col);
                                                                                                                    DocumentReference docRefFarmmerFarm = db.collection("farmPlot").document(key);
                                                                                                                    docRefFarmmerFarm.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                                                            if (task2.isSuccessful()) {
                                                                                                                                DocumentSnapshot document = task2.getResult();
                                                                                                                                docRefFarmmerFarm.update("cropName", "EMPTY");
                                                                                                                                docRefFarmmerFarm.update("amount", 0 );
                                                                                                                                docRefFarmmerFarm.update("dateOfHarvest", null );
                                                                                                                                database.mainDao().updatePlantedCropName(plotTables.get(plotInfo.location).getId(), "EMPTY");
                                                                                                                                database.mainDao().updatePlantedCropDateOfHarvest(plotTables.get(plotInfo.location).getId(), 0);
                                                                                                                                plotTables.clear();
                                                                                                                                plotTables.addAll((database.mainDao().getAllPlots()));
                                                                                                                                plotInfo.name = "EMPTY";
                                                                                                                                plotInfo.cropDate = "";
                                                                                                                                FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                                            }

                                                                                                                        }
                                                                                                                    });


                                                                                                                }
                                                                                                            });
                                                                                                            builder1.setNegativeButton(android.R.string.cancel, null);
                                                                                                            builder1.show();

                                                                                                            FarmerMainScreen.getInstance().fp.refreshFarm();
                                                                                                            dismiss();
                                                                                                        }

                                                                                                    }

                                                                                                })

                                                                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                                                                .setNegativeButton(android.R.string.no, null)
                                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                                .show();
                                                                                        FarmerMainScreen.getInstance().fp.refreshFarm();

                                                                                    }
                                                                                });
                                                                                plantField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                    @Override
                                                                                    public void onItemSelected
                                                                                            (AdapterView<?> parentView, View
                                                                                                    selectedItemView, int position,
                                                                                             long id) {
                                                                                        SeedsTable seedsTable = seeds.get(position);
                                                                                        plantFieldAmount.setHint("Max: " + String.valueOf(seedsTable.getAmount()));
                                                                                        seedLocation = position;
                                                                                    }

                                                                                    @Override
                                                                                    public void onNothingSelected
                                                                                            (AdapterView<?> parent) {

                                                                                    }
                                                                                });

                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        });
        return v;
    }


    public String getIdField() {
        return setNameField.getText().toString();
    }

    public String getIdPlantField() {
        return plantField.getSelectedItem().toString();
    }

}
