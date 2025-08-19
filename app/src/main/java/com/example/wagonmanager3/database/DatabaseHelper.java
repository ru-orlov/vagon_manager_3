package com.example.wagonmanager3.database;

import static com.example.wagonmanager3.database.DbContract.InventoryItems.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wagonmanager3.models.InventoryGroup;
import com.example.wagonmanager3.models.InventoryItem;
import com.example.wagonmanager3.models.ScanHistory;
import com.example.wagonmanager3.models.User;
import com.example.wagonmanager3.models.Wagon;
import com.example.wagonmanager3.models.WagonInventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        db.execSQL(DbContract.ScanHistory.CREATE_TABLE);
        db.execSQL(DbContract.ChangeLogs.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // При обновлении базы данных
        clearAllTables();
        onCreate(db);
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

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = user.toContentValues();

        long id = db.insert(DbContract.Users.TABLE_NAME, null, values);

        // Логируем создание пользователя
        if (id != -1) {
            addChangeLog(
                    DbContract.Users.TABLE_NAME,
                    id,
                    "create",
                    null,
                    values.toString()
            );
        }

        return id;
    }

    public void deleteScanHistory(ScanHistory scanHistory) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                DbContract.ScanHistory.TABLE_NAME,
                DbContract.ScanHistory.COLUMN_UUID + " = ?",
                new String[]{scanHistory.getUuid()}
        );

        // Логируем удаление
        addChangeLog(
                DbContract.ScanHistory.TABLE_NAME,
                -1, // ID неизвестен
                "delete",
                scanHistory.toString(),
                null
        );
    }

    public long addWagon(Wagon wagon) { //create
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = wagon.toContentValues();
        values.put(DbContract.Wagons.COLUMN_UUID, wagon.getUuid());
        values.put(DbContract.Wagons.COLUMN_NUMBER, wagon.getNumber());
        values.put(DbContract.Wagons.COLUMN_VAGON_UUID, wagon.getVagonUuid());
        values.put(DbContract.Wagons.COLUMN_TYPE, wagon.getType());
        values.put(DbContract.Wagons.COLUMN_CREATED_AT, System.currentTimeMillis());
        values.put(DbContract.Wagons.COLUMN_UPDATED_AT, System.currentTimeMillis());
        values.put(DbContract.Wagons.COLUMN_SYNC_STATUS, "new"); // При создании статус "new"
        return db.insert(DbContract.Wagons.TABLE_NAME, null, values);
    }

    public long addInventoryGroup(InventoryGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.InventoryGroups.COLUMN_UUID, group.getUuid());
        values.put(DbContract.InventoryGroups.COLUMN_NAME, group.getName());
        values.put(DbContract.InventoryGroups.COLUMN_VAGON_UUID, group.getVagonUuid());
        values.put(DbContract.InventoryGroups.COLUMN_DESCRIPTION, group.getDescription());
        values.put(DbContract.InventoryGroups.COLUMN_CREATED_AT, System.currentTimeMillis());
        values.put(DbContract.InventoryGroups.COLUMN_UPDATED_AT, System.currentTimeMillis());
        values.put(DbContract.InventoryGroups.COLUMN_SYNC_STATUS, "new"); // При создании статус "new"
        return db.insert(DbContract.InventoryGroups.TABLE_NAME, null, values);
    }



    public List<InventoryGroup> getAllInventoryGroups() {
        List<InventoryGroup> groups = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                DbContract.InventoryGroups.TABLE_NAME,
                null, null, null, null, null, null)) {

            while (cursor.moveToNext()) {
                groups.add(InventoryGroup.fromCursor(cursor));
            }
        }
        return groups;
    }

    public String getWagonNumberByUuid(String wagonUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String number = null;

        Cursor cursor = db.query(
                DbContract.Wagons.TABLE_NAME,
                new String[]{DbContract.Wagons.COLUMN_UUID},
                DbContract.Wagons.COLUMN_UUID + " = ?",
                new String[]{wagonUuid},
                null, null, null);

        if (cursor.moveToFirst()) {
            number = cursor.getString(0);
        }
        cursor.close();

        return number;
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

    public long insertInventoryItem(InventoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.InventoryItems.COLUMN_UUID, item.getUuid());
        values.put(DbContract.InventoryItems.COLUMN_GROUP_ID, item.getGroupId());
        values.put(DbContract.InventoryItems.COLUMN_VAGON_UUID, item.getVagonUuid());
        values.put(DbContract.InventoryItems.COLUMN_NAME, item.getName());
        values.put(DbContract.InventoryItems.COLUMN_DESCRIPTION, item.getDescription());
        values.put(DbContract.InventoryItems.COLUMN_QUANTITY, item.getQuantity());
        values.put(DbContract.InventoryItems.COLUMN_SYNC_STATUS, item.getSyncStatus());

        return db.insert(TABLE_NAME, null, values);
    }

    public List<InventoryItem> getItemsByGroup(int groupId) {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.InventoryItems.TABLE_NAME,
                null,
                DbContract.InventoryItems.COLUMN_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)},
                null, null, null);

        while (cursor.moveToNext()) {
            items.add(new InventoryItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_UUID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_GROUP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_VAGON_UUID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_QUANTITY)),
                    new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_CREATED_AT))),
                    new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_UPDATED_AT))),
                    cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_SYNC_STATUS))
            ));
        }
        cursor.close();
        return items;
    }

    private void addChangeLog(String tableName, long recordId, String action, String oldValues, String newValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DbContract.ChangeLogs.COLUMN_TABLE_NAME, tableName);
        values.put(DbContract.ChangeLogs.COLUMN_RECORD_ID, recordId);
        values.put(DbContract.ChangeLogs.COLUMN_ACTION, action);
        values.put(DbContract.ChangeLogs.COLUMN_OLD_VALUES, oldValues);
        values.put(DbContract.ChangeLogs.COLUMN_NEW_VALUES, newValues);
        values.put(DbContract.ChangeLogs.COLUMN_CHANGED_AT, System.currentTimeMillis());

        // Получаем текущего пользователя из SharedPreferences
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        long userId = prefs.getLong("current_user_id", -1);
//        values.put(DbContract.ChangeLogs.COLUMN_USER_ID, userId);

        db.insert(DbContract.ChangeLogs.TABLE_NAME, null, values);
    }

    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT ii.*, ig.name AS group_name " +
                "FROM " + DbContract.InventoryItems.TABLE_NAME + " ii " +
                "JOIN " + DbContract.InventoryGroups.TABLE_NAME + " ig " +
                "ON ii." + DbContract.InventoryItems.COLUMN_GROUP_ID + " = ig." + DbContract.InventoryGroups.COLUMN_ID;

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                InventoryItem item = new InventoryItem();
                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_QUANTITY)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
                items.add(item);
            }
        }
        return items;
    }

    public List<Wagon> getAllWagons() {
        List<Wagon> wagons = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(DbContract.Wagons.TABLE_NAME, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                Wagon wagon = new Wagon();
                wagon.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_ID)));
                //wagon.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_VAGON_UUID)));
                wagon.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_NUMBER)));
                wagons.add(wagon);
            }
        }
        return wagons;
    }

    public InventoryItem getInventoryItemById(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        InventoryItem item = null;

        String query = "SELECT ii.*, ig.name AS group_name " +
                "FROM " + DbContract.InventoryItems.TABLE_NAME + " ii " +
                "JOIN " + DbContract.InventoryGroups.TABLE_NAME + " ig " +
                "ON ii." + DbContract.InventoryItems.COLUMN_GROUP_ID + " = ig." + DbContract.InventoryGroups.COLUMN_ID + " " +
                "WHERE ii." + DbContract.InventoryItems.COLUMN_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)})) {
            if (cursor.moveToFirst()) {
                item = new InventoryItem();
                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_DESCRIPTION)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_QUANTITY)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
            }
        }
        return item;
    }


    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DbContract.Users.TABLE_NAME);
        db.execSQL("DELETE FROM " + DbContract.Wagons.TABLE_NAME);
        db.execSQL("DELETE FROM " + DbContract.InventoryGroups.TABLE_NAME);
        db.execSQL("DELETE FROM " + DbContract.InventoryItems.TABLE_NAME);
        db.execSQL("DELETE FROM " + DbContract.ScanHistory.TABLE_NAME);
        db.execSQL("DELETE FROM " + DbContract.ChangeLogs.TABLE_NAME);

        Log.d("DatabaseHelper", "All tables cleared.");
    }

    public List<InventoryItem> getInventoryItemsByWagonUuid(String wagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.InventoryItems.TABLE_NAME,
                null,
                DbContract.InventoryItems.COLUMN_VAGON_UUID + " = ?",
                new String[]{wagonUuid},
                null, null, null);

        while (cursor.moveToNext()) {
            InventoryItem item = new InventoryItem();
            item.setGroupId(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_GROUP_ID)));
            item.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_VAGON_UUID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_DESCRIPTION)));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_QUANTITY)));
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public List<InventoryGroup> getInventoryGroupByWagonUuid(String wagonUuid) {
        List<InventoryGroup> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                DbContract.InventoryGroups.TABLE_NAME,
                null,
                DbContract.InventoryGroups.COLUMN_VAGON_UUID + " = ?",
                new String[]{wagonUuid},
                null, null, null);

        while (cursor.moveToNext()) {
            InventoryGroup group = InventoryGroup.fromCursor(cursor);
            group.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_VAGON_UUID)));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_NAME)));
            group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_DESCRIPTION)));
            groups.add(group);
        }
        cursor.close();
        return groups;
    }
}