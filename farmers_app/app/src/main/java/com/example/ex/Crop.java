package com.example.ex1_205790488_315680397;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Crop {
    private String item;
    private Date plantDate;
    private String cropDate;

    public Crop(String item) {
        setItem(item);
    }

    public String getItem() {
        return item;
    }

    public Date getPlantDate() {
        return plantDate;
    }

    public String getCropDate() {
        return cropDate;
    }

    public void setItem(String item) {
        this.item = item;
        setPlantDate();
        setCropDate();
    }

    public void setPlantDate() {
        plantDate = Calendar.getInstance().getTime();
    }

    public void setCropDate() {
        Calendar cal = Calendar.getInstance();
        if (item.equalsIgnoreCase("banana"))
        {
            cal.add(Calendar.MONTH, 22);
        }
        else if(item.equalsIgnoreCase("carrot")){
            cal.add(Calendar.DAY_OF_YEAR, 80);
        }
        else if(item.equalsIgnoreCase("orange")){
            cal.add(Calendar.MONTH, 10);
        }

        java.util.Date dt = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yy");

        try {
            cropDate = format1.format(dt);

        } catch (ParseException e1) {
            e1.printStackTrace();

        }


    }

    public void setCropDate(String newDate) {
            cropDate = newDate;

    }

}

