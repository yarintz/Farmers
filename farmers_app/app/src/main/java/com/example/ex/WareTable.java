package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table name
@Entity(tableName = "ware")
public class WareTable  implements Serializable{

        //create id column
        @PrimaryKey(autoGenerate = true)
        private int id;

        //create name column
        @ColumnInfo(name = "wareCropName")
        private String wareCropName;

        //create the column of the amount in kg for each seed
        @ColumnInfo(name = "amount")
        private int amount;


        //create getters and setters
        public void setId(int id) {
            this.id = id;
        }

        public void setWareCropName(String cropName) {
            this.wareCropName = cropName;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public String getWareCropName() {
            return wareCropName;
        }

        public int getAmount() {
            return amount;
        }
    }

