package com.example.android.fetchdata.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WPFEntity.class}, version = 1, exportSchema = true)
public abstract class WPFDataBase extends RoomDatabase {
    public static final String DB_NAME = "WPFTable.db";
    private static volatile WPFDataBase instance;

    public static synchronized WPFDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,WPFDataBase.class,DB_NAME).build();
        }
        return instance;
    }
    public abstract WPFDao wpfdao();
}
