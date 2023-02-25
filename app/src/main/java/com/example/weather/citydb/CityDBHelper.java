package com.example.weather.citydb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "city.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CITY = "city"; // 表名

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
    public String queryCountyId(String countyName) {
        String countyId = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CITY, new String[]{"id"}, "name=?", new String[]{countyName}, null, null, null);
        if (cursor.moveToFirst()) {
        //    countyId = cursor.getString(cursor.getColumnIndex("id"));
            countyId=cursor.getString(cursor.getColumnIndex("id"));
        }
        cursor.close();
        db.close();
        return countyId;
    }
}
