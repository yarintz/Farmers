package com.example.ex1_205790488_315680397;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterSuppliersRealtions extends BaseAdapter implements ListAdapter {




        private Context context;
        private ArrayList<String> suppliers;
        private LayoutInflater inflter;
        private Activity activity;
         private ArrayList<AllSuppliersItemsTable> supplierTableArrayList;
         private RoomDB database;


        public CustomAdapterSuppliersRealtions(Context applicationContext, List<AllSuppliersItemsTable> supplierTable, Activity activity) {
            this.context = applicationContext;
            //this.suppliers = seeds;
            this.activity = activity;
            //   inflter = (LayoutInflater.from(applicationContext));
            this.supplierTableArrayList = new ArrayList<>(supplierTable);
            inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return supplierTableArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return supplierTableArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.activity_listview_suppliers_relations, null);
            }
            TextView supplierName = (TextView) view.findViewById(R.id.supplierRelationsListSupplierName);
            TextView item = (TextView) view.findViewById(R.id.supplierRelationsListItemName);
            TextView price = (TextView) view.findViewById(R.id.supplierRelationsListItemPrice);
            TextView phone = (TextView) view.findViewById(R.id.supplierRelationsListSupplierPhone);
            supplierName.setText(supplierTableArrayList.get(i).getName());
            phone.setText(supplierTableArrayList.get(i).getPhone());
            item.setText(supplierTableArrayList.get(i).getCropName());
            price.setText(supplierTableArrayList.get(i).getPrice() + context.getString(R.string.dollar ));
            return view;
        }

    }

