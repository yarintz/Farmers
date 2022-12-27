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

public class CustomeAdapterSuppliersScreen extends BaseAdapter implements ListAdapter {



    private Context context;
    private ArrayList<String> items;
    private LayoutInflater inflter;
    private Activity activity;
    private ArrayList<SupplierTable> supplierTableArrayList;
    private RoomDB database;


    public CustomeAdapterSuppliersScreen(Context applicationContext, List<SupplierTable>supplierTable, Activity activity) {
        this.context = applicationContext;
        this.activity = activity;
        this.supplierTableArrayList = new ArrayList<>(supplierTable);
        //   inflter = (LayoutInflater.from(applicationContext));
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
            view = inflter.inflate(R.layout.activity_supplier_listview, null);
        }
        TextView item = (TextView) view.findViewById(R.id.supplierTextView);
        item.setText(supplierTableArrayList.get(i).getCropName() + " " + supplierTableArrayList.get(i).getPrice() + context.getString(R.string.dollar ));
        ImageButton editbtn = (ImageButton) view.findViewById(R.id.editItemAmount);
        EditText newAmount = (EditText) view.findViewById(R.id.newAmountItemEditText);
        newAmount.setHint(R.string.editAmount);
        ImageButton removebtn = (ImageButton) view.findViewById(R.id.removeItem);

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = newAmount.getText().toString();
                String temp = getItem(i).toString();
                SupplierMainScreen.instance.setItemsPrice(i,newAmount.getText().toString());


            }
        });
        removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = getItem(i).toString();
                SupplierMainScreen.instance.removeItem(i);


            }
        });


        return view;
    }
}
