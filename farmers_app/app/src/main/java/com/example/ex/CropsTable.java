package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table name
@Entity(tableName = "crops")
public class CropsTable implements Serializable {

    //create id column
    @PrimaryKey(autoGenerate = true)
    private int id;

    //create name column
    @ColumnInfo(name = "cropName")
    private String cropName;

    //create the column that suggests the time the crop needs to be planted
    @ColumnInfo(name = "monthsPlantedTime")
    private int monthsPlantedTime;

    //create the column that suggests the season to plant the crop
    @ColumnInfo(name = "season")
    private String season;

    //create getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public int getMonthsPlantedTime() {
        return monthsPlantedTime;
    }

    public void setMonthsPlantedTime(int monthsPlantedTime) {
        this.monthsPlantedTime = monthsPlantedTime;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
