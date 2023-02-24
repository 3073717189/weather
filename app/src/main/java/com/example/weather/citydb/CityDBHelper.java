package com.example.weather.citydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "city.db";
    private static final int DATABASE_VERSION = 1;

    public CityDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS city (id INTEGER PRIMARY KEY, name TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS city";
        db.execSQL(sql);
        onCreate(db);
    }
}
