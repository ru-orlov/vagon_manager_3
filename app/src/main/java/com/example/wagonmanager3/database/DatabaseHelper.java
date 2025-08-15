package com.example.wagonmanager3.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wagonmanager3.models.ScanHistory;
import com.example.wagonmanager3.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vu9journal.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблиц согласно предоставленному скрипту
        db.execSQL(DbContract.Users.CREATE_TABLE);
        db.execSQL(DbContract.Wagons.CREATE_TABLE);
        db.execSQL(DbContract.InventoryGroups.CREATE_TABLE);
        db.execSQL(DbContract.InventoryItems.CREATE_TABLE);
        db.execSQL(DbContract.WagonInventory.CREATE_TABLE);
        db.execSQL(DbContract.ScanHistory.CREATE_TABLE);
        db.execSQL(DbContract.ChangeLogs.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // При обновлении базы данных
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.ChangeLogs.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.ScanHistory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.WagonInventory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.InventoryItems.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.InventoryGroups.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Wagons.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Users.TABLE_NAME);
        onCreate(db);
    }

    public void addScanHistory(ScanHistory historyItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.ScanHistory.COLUMN_UUID, historyItem.getUuid());
        values.put(DbContract.ScanHistory.COLUMN_WAGON_UUID, historyItem.getWagonUuid());
        values.put(DbContract.ScanHistory.COLUMN_WAGON_NUMBER, historyItem.getWagonNumber());
        values.put(DbContract.ScanHistory.COLUMN_USER_UUID, historyItem.getUserUuid());
        values.put(DbContract.ScanHistory.COLUMN_SCAN_TIME, historyItem.getScanTime().getTime());

        db.insert(DbContract.ScanHistory.TABLE_NAME, null, values);
        db.close();
    }

    public String getWagonNumberByUuid(String wagonUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String number = null;

        Cursor cursor = db.query(
                DbContract.Wagons.TABLE_NAME,
                new String[]{DbContract.Wagons.COLUMN_NUMBER},
                DbContract.Wagons.COLUMN_UUID + " = ?",
                new String[]{wagonUuid},
                null, null, null);

        if (cursor.moveToFirst()) {
            number = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return number;
    }

    public List<ScanHistory> getAllScanHistory() {
        List<ScanHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.ScanHistory.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                DbContract.ScanHistory.COLUMN_SCAN_TIME + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                ScanHistory history = new ScanHistory(
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_WAGON_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_WAGON_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_USER_UUID)),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_SCAN_TIME)))
                );
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(
                DbContract.Users.TABLE_NAME,
                null,
                DbContract.Users.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_UUID)),
                    username,
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_PASSWORD_HASH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_ROLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_IS_ACTIVE)) == 1
            );
        }
        cursor.close();
        return user;
    }
}