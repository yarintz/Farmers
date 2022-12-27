package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuLogic extends AppCompatActivity {

    protected static AppCompatDelegate mDelegate;
    protected static Context myContext;
    protected static Activity myActivity;
    protected static Menu myMenu;
    protected static MenuInflater myInflater;
    private static MenuLogic instance;
    private RoomDB database;

    public static MenuLogic getInstance() {
        if (instance == null) {
            instance = new MenuLogic();
        }
        return instance;
    }

    public boolean onCreateOptionsMenu(Menu menu, Context context, Activity activity) {

        myMenu = menu;
        myContext = context;
        myActivity = activity;
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.farmer_main_screen_menu, menu);
        database = RoomDB.getInstance(this);


        if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.ContactAgronomist")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_contactAgronomist);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerContactAgronomistScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.FarmerWare")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_readyToSupply);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerWareInventoryScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.FarmerInventory")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_seedInventory);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerSeedInventoryScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.FarmerMainScreen")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_home_screen);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerHomeScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.SuppliersRelations")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_suppliersRelations);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerSuppliersPricesScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.CropsInformation")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_crops_information);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.farmerCropInfomationScreenHeader);
        } else if (context.getClass().getName().equals("com.example.ex1_205790488_315680397.FarmerMessages")) {
            MenuItem item2 = menu.findItem(R.id.menuitem_farmer_messsages);
            item2.setVisible(false);
            MenuItem item3 = menu.findItem(R.id.design_menu_item_text_farmer_app);
            item3.setTitle(R.string.messagesScreenMenu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item, Context context, Activity activity) {
        myContext = context;
        myActivity = activity;

        switch (item.getItemId()) {


            case R.id.menuitem_logout:
                AlertDialog exitDialog = new AlertDialog.Builder(myContext)
                        .setTitle(R.string.logOut)
                        .setMessage(R.string.areYouSureYouWantToLogOut)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mainActivity = new Intent(myContext, MainActivity.class);
                                database.mainDao().updateUserType(null);
                                // MainActivity.setType(null);
                                FirebaseAuth.getInstance().signOut();
                                myActivity.finish();
                                myContext.startActivity(mainActivity);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.exit_icon)
                        .show();
                return true;

            case R.id.menuitem_home_screen:
                Intent farmerMainScreen = new Intent(myContext, FarmerMainScreen.class);
                myContext.startActivity(farmerMainScreen);
                return true;

            case R.id.menuitem_contactAgronomist:
                Intent intentContactAgronomist = new Intent(myContext, ContactAgronomist.class);
                myContext.startActivity(intentContactAgronomist);
                return true;

            case R.id.menuitem_readyToSupply:
                Intent intentFamrerWare = new Intent(myContext, FarmerWare.class);
                myContext.startActivity(intentFamrerWare);
                return true;

            case R.id.menuitem_seedInventory:
                Intent intentSeedInventory = new Intent(myContext, FarmerInventory.class);
                myContext.startActivity(intentSeedInventory);
                return true;

            case R.id.menuitem_suppliersRelations:
                Intent intentSupplierRelations = new Intent(myContext, SuppliersRelations.class);
                myContext.startActivity(intentSupplierRelations);
                return true;

            case R.id.menuitem_crops_information:
                Intent intentCropsInformation = new Intent(myContext, CropsInformation.class);
                myContext.startActivity(intentCropsInformation);
                return true;

            case R.id.menuitem_farmer_messsages:
                Intent intentMessagesScreen = new Intent(myContext, FarmerMessages.class);
                myContext.startActivity(intentMessagesScreen);
                return true;
        }
        return false;
    }

}
