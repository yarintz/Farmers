package com.example.ex1_205790488_315680397;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CropsTable.class, SeedsTable.class, MessagesTable.class, WareTable.class, PlotTable.class, SupplierTable.class, UserTypeTable.class, AllSuppliersItemsTable.class},version =13, exportSchema = false)


public abstract class RoomDB extends RoomDatabase {
    //create databse instance
    private static RoomDB database;
    //Define datavase name
    private static String DATABASE_NAME = "database";

    public synchronized static RoomDB getInstance(Context context) {
        //check condition
        if (database == null) {
            //When database is null
            //Initialize database
            database = Room.databaseBuilder(context.getApplicationContext()
                    , RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract MainDao mainDao();
}