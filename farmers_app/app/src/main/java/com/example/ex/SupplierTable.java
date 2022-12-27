package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
//Define table name
@Entity(tableName = "supplier")
public class SupplierTable implements Serializable{

        //create id column
        @PrimaryKey(autoGenerate = true)
        private int id;

        //create name column
        @ColumnInfo(name = "cropName")
        private String cropName;

        //create the column of the price per 1 kg
        @ColumnInfo(name = "price")
        private String price;


        //create getters and setters
        public void setId(int id) {
            this.id = id;
        }

        public void setCropName(String cropName) {
            this.cropName = cropName;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getCropName() {
            return cropName;
        }

        public String getPrice() {
            return price;
        }
    }




