package com.example.ex1_205790488_315680397;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
;import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private Context context;
   // private ArrayList<String> seeds;
    private  LayoutInflater inflter;
    private Activity activity;


    private ArrayList<String> seeds;
    private ArrayList<SeedsTable> seedsTableArrayList;
    private RoomDB database;

     public CustomAdapter(Context applicationContext, List<SeedsTable> seedsTable, Activity activity) {
        this.context = applicationContext;
         this.activity = activity;
         this.seedsTableArrayList = new ArrayList<>(seedsTable);
         inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         notifyDataSetChanged();

       // inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return seedsTableArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return seedsTableArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
/**
 * param int i -
 * param view -
 * param viewGroup -
 */
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflter.inflate(R.layout.activity_listview, null);
        }
        TextView seed = (TextView) view.findViewById(R.id.seedInventoryScreen);
//        String tempString = context.getString(seedsTableArrayList.get(i).getSeedName()) + " " + seedsTableArrayList.get(i).getAmount() + getString(R.string.kg );
        seed.setText(seedsTableArrayList.get(i).getSeedName() + " " + seedsTableArrayList.get(i).getAmount() + context.getString(R.string.kg ));
        ImageButton editbtn = (ImageButton) view.findViewById(R.id.editSeedAndInventoryAmount);
        ImageButton deletebtn = (ImageButton) view.findViewById(R.id.deleteSeedAndInventoryAmount);
        EditText newAmount = (EditText) view.findViewById(R.id.newAmountEditText);
        newAmount.setHint(R.string.editAmount);

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FarmerInventory.instance.removeSeed(i);

            }
        });

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FarmerInventory.instance.setSeedsAmount(i,newAmount.getText().toString());
            }
        });

        return view;
    }

}