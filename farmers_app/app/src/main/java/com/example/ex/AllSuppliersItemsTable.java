package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
//Define table name
@Entity(tableName = "allsupplier")
public class AllSuppliersItemsTable implements Serializable{

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    //create id column
    @PrimaryKey(autoGenerate = true)
    private int id;

    //create name column
    @ColumnInfo(name = "mail")
    private String mail;

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    //create name column
    @ColumnInfo(name = "name")
    private String name;
    //create name column
    @ColumnInfo(name = "phoneNumber")
    private String phone;

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




