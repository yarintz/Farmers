package com.example.ex1_205790488_315680397;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FarmPlot extends RecyclerView.Adapter<com.example.ex1_205790488_315680397.FarmPlot.ContactViewHolder> {

    private List<PlotInfo> plotList;
    protected Button vEditButton;
    private ArrayList<PlotTable> plotsTable;

    public FarmPlot(List<PlotInfo> plotList) {
        this.plotList = plotList;
    }


    @Override
    public int getItemCount() {
        return plotList.size();
    }

    @Override
    public void onBindViewHolder(com.example.ex1_205790488_315680397.FarmPlot.ContactViewHolder contactViewHolder, int position) {

        PlotInfo pi = plotList.get(position);
        try {
            contactViewHolder.setData(pi);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout_farmer, viewGroup, false);

        return new com.example.ex1_205790488_315680397.FarmPlot.ContactViewHolder(itemView);
    }
    public void refreshFarm(){
        com.example.ex1_205790488_315680397.FarmPlot.this.notifyDataSetChanged();
    }

    public  class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView vName;
        private TextView vCropDate;
        private PlotInfo pi = null;

        public ContactViewHolder(View v) {
            super(v);

            vName =  (TextView) v.findViewById(R.id.txtName);
            vCropDate = (TextView) v.findViewById(R.id.cropDate);
            vEditButton = (Button) v.findViewById(R.id.edit);

            vEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FarmerMainScreen a = FarmerMainScreen.getInstance();

                    PlotInfo fp = a.fp.plotList.get(pi.location);
                    String temp = fp.name;
                    a.showCustomAlertDialog(view,fp);
                    com.example.ex1_205790488_315680397.FarmPlot.this.notifyDataSetChanged();
                }
            });
        }
        //set the data in the card list according to the crop that belong to the plot
        public void setData(PlotInfo pi) throws ParseException {
            this.pi = pi;
            if(vName.equals("EMPTY"))
                vName.setText(R.string.empty);
            else
                vName.setText(pi.name);
            if(vCropDate.equals(""))
                vCropDate.setText(R.string.emptyString);
            else {
                vCropDate.setText(pi.cropDate);
            }
           pi.setColor();
            if(pi.color != 0){
                vCropDate.setTextColor(pi.color);
            }
        }
    }

}
