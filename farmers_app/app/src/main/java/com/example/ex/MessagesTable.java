package com.example.ex1_205790488_315680397;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//Define table name
@Entity(tableName = "messages")
public class MessagesTable implements Serializable {
    //create id column
    @PrimaryKey(autoGenerate = true)
    private int id;

    //create from column
    @ColumnInfo(name = "from")
    private String from;

    //create a column that save the message
    @ColumnInfo(name = "content")
    private String content;

    //create a column that store the image
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    //create to column
    @ColumnInfo(name = "to")
    private String to;

    //create subject column
    @ColumnInfo(name = "previousMessage")
    private int previousMessageID;

    //if farmer deleted the message on his view its gonna be false;
    @ColumnInfo(name = "farmerBool")
    private boolean farmerBool = true;

    //if agronomist deleted the message on his view its gonna be false;
    @ColumnInfo(name = "agronomistBool")
    private boolean agronomistBool = true;


//messageID
@ColumnInfo(name = "messageID")
private String messageID;

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageID() {
        return messageID;
    }
//create getters and setters


    public void setId(int id) {
        this.id = id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public byte[] getImage() {
        return image;
    }

    public String getTo() {
        return to;
    }

    public int getPreviousMessageID() {
        return previousMessageID;
    }

    public void setPreviousMessageID(int previousMessageID) {
        this.previousMessageID = previousMessageID;
    }

    public boolean isFarmerBool() {
        return farmerBool;
    }

    public boolean isAgronomistBool() {
        return agronomistBool;
    }
    public void setFarmerBool(boolean farmerBool) {
        this.farmerBool = farmerBool;
    }

    public void setAgronomistBool(boolean agronomistBool) {
        this.agronomistBool = agronomistBool;
    }
}
