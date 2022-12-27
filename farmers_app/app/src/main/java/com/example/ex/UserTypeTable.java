package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.io.Serializable;

@Entity(tableName = "type")
public class UserTypeTable implements Serializable {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //this database is created to store the current user type
    //we need this table to save time when log-in beacuse
    //if we dont have this table we need to check every
    //time we start the app which type of user it is
    //and it takes time to finish the oncomplete function.
    //create id column
    @PrimaryKey(autoGenerate = true)
    private int id;
    //create name column
    @ColumnInfo(name = "type")
    private String type;




    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
