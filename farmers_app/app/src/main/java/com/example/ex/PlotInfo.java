package com.example.ex1_205790488_315680397;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlotInfo {

    protected String name;
    protected String cropDate = "";
    protected int location;
    protected int row;
    protected int column;
    protected int color = 0;

    public void setColor() throws ParseException {
        if(cropDate != ""){
            Date date1 =new SimpleDateFormat("dd/MM/yy").parse(cropDate);
            Date date2 = Calendar.getInstance().getTime();
            long difference_In_Time = date1.getTime()-date2.getTime();
            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;
            if(difference_In_Days <= 30){
                color = Color.GREEN;
            }
            else if(difference_In_Days > 30 && difference_In_Days <60){
                color = Color.YELLOW;
            }
            else
                color = Color.RED;
        }

    }
}


