package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table name
@Entity(tableName = "seeds")
public class SeedsTable implements Serializable{


        //create id column
        @PrimaryKey(autoGenerate = true)
        private int id;

        //create name column
        @ColumnInfo(name = "seedName")
        private String seedName;

        //create the column of the amount in kg for each seed
        @ColumnInfo(name = "amount")
        private int amount;



        //create getters and setters

        public void setId(int id) {
                this.id = id;
        }

        public void setSeedName(String seedName) {
                this.seedName = seedName;
        }

        public void setAmount(int amount) {
                this.amount = amount;
        }

        public int getId() {
                return id;
        }

        public String getSeedName() {
                return seedName;
        }

        public int getAmount() {
                return amount;
        }
}
