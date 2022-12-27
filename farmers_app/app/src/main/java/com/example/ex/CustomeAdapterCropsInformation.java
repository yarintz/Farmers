package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomeAdapterCropsInformation extends BaseAdapter implements ListAdapter {


        private Context context;
        private ArrayList<String> cropsInformation;
        private LayoutInflater inflter;
        private Activity activity;
        private ArrayList<CropsTable> cropsTableArrayList;
        private RoomDB database;


        public CustomeAdapterCropsInformation(Context applicationContext, List<CropsTable> cropsTable, Activity activity) {
            this.context = applicationContext;
          //  this.cropsInformation = cropsInformation;
            this.activity = activity;
            this.cropsTableArrayList = new ArrayList<>(cropsTable);
            inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cropsTableArrayList.size();
        }

        @Override
        public CropsTable getItem(int i) {
            return cropsTableArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.acticity_list_view_crops_information, null);
            }
            //get the crop information to show in the crops table.
            //get crop name
            TextView cropName = (TextView) view.findViewById(R.id.cropNameCropsInformationAdapterTextView);
            cropName.setText(cropsTableArrayList.get(i).getCropName());

            //get crop suggested plant time.
            TextView cropDate = (TextView) view.findViewById(R.id.cropDateCropsInformationAdapterTextView);
            cropDate.setText(Integer.toString(cropsTableArrayList.get(i).getMonthsPlantedTime()));

            //get crop suggested plant time.
            TextView cropSeason = (TextView) view.findViewById(R.id.cropSeasonCropsInformationAdapterTextView);
            cropSeason.setText(cropsTableArrayList.get(i).getSeason());

            Button remove = (Button) view.findViewById(R.id.removeCropInformation);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   CropsInformation.instance.removeCropInformation(i);
                }
            });


            return view;
        }

    }



