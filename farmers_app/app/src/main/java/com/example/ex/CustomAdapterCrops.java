package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterCrops extends BaseAdapter implements ListAdapter{


        private Context context;
        private ArrayList<String> cropsToSell;
        private LayoutInflater inflter;
        private Activity activity;
        private ArrayList<WareTable> WareTableArrayList;
        private RoomDB database;


        public CustomAdapterCrops(Context applicationContext, List<WareTable> cropsToSell, Activity activity) {
            this.context = applicationContext;
           // this.cropsToSell = cropsToSell;
            this.activity = activity;
            this.WareTableArrayList = new ArrayList<>(cropsToSell);
            //   inflter = (LayoutInflater.from(applicationContext));
            inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return WareTableArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return WareTableArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.activity_ware, null);
            }
            TextView seed = (TextView) view.findViewById(R.id.supplyTextView);
            seed.setText(WareTableArrayList.get(i).getWareCropName() + " " + WareTableArrayList.get(i).getAmount() + context.getString(R.string.kg ));
            ImageButton editbtn = (ImageButton) view.findViewById(R.id.editCropAmount);
            ImageButton deletebtn = (ImageButton) view.findViewById(R.id.deleteWareCropInListView);
            EditText newAmount = (EditText) view.findViewById(R.id.newAmountCropsEditText);
            newAmount.setHint(R.string.editAmount);

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FarmerWare.instance.removeWareCrop(i);

                }
            });
            editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String res = newAmount.getText().toString();
                    String temp = getItem(i).toString();
                    FarmerWare.instance.setCropsAmount(i,newAmount.getText().toString());


                }
            });

            return view;
        }

    }

