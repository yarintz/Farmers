package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

//Define table name
@Entity(tableName = "plots")
public class PlotTable implements Serializable {


    //create id column
    @PrimaryKey(autoGenerate = true)
    private int id;

    //create crop name column
    @ColumnInfo(name = "plantedCropName")
    private String plantedCropName;

    //create date column
    @ColumnInfo(name = "dateOfHarvest")
    private long dateOfHarvest;

    //create amount column
    @ColumnInfo(name = "amount")
    private int amount;

    //create row column
    @ColumnInfo(name = "row")
    private int row;

    //create column column
    @ColumnInfo(name = "column")
    private int column;






    //create getters and setters

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlantedCropName(String plantedCropName) {
        this.plantedCropName = plantedCropName;
    }

    public void setDateOfHarvest(long dateOfHarvest) {
        this.dateOfHarvest = dateOfHarvest;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getPlantedCropName() {
        return plantedCropName;
    }

    public long getDateOfHarvest() {
        return dateOfHarvest;
    }

    public int getAmount() {
        return amount;
    }
}


