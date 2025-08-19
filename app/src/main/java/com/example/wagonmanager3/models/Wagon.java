package com.example.wagonmanager3.models;

import com.example.wagonmanager3.database.DbContract;

import java.util.Date;
import java.util.UUID;

public class Wagon {
    private long id;
    private String uuid;
    private String vagonUuid;
    private String number;
    private String type;
    private Date createdAt;
    private Date updatedAt;
    private String syncStatus;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    // Конструкторы
    public Wagon() {
        this.vagonUuid = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.syncStatus = "synced";
    }

    public Wagon(String number, String vagonUuid, String type) {
        this();
        this.number = number;
        this.vagonUuid = vagonUuid;
        this.type = type;
    }

    // Геттеры
    public long getId() {
        return id;
    }

    public String getVagonUuid() {
        return vagonUuid;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

     public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    // Сеттеры
    public void setId(long id) {
        this.id = id;
    }

    public void setVagonUuid(String vagonUuid) {
        this.vagonUuid = vagonUuid;
    }

    public void setNumber(String number) {
        this.number = number;
        this.updatedAt = new Date();
    }

    public void setType(String type) {
        this.type = type;
        this.updatedAt = new Date();
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSyncStatus(String syncStatus) {
        if (!syncStatus.equals("synced") && !syncStatus.equals("modified")
                && !syncStatus.equals("new") && !syncStatus.equals("deleted")) {
            throw new IllegalArgumentException("Invalid sync status");
        }
        this.syncStatus = syncStatus;
    }

    @Override
    public String toString() {
        return "Вагон №" + number + " (" + type + ")";
    }

    // Методы для работы с базой данных
    public android.content.ContentValues toContentValues() {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DbContract.Wagons.COLUMN_UUID, id == 0 ? vagonUuid : String.valueOf(id));
        values.put(DbContract.Wagons.COLUMN_NUMBER, number);
        values.put(DbContract.Wagons.COLUMN_TYPE, type);
        values.put(DbContract.Wagons.COLUMN_CREATED_AT, createdAt.getTime());
        values.put(DbContract.Wagons.COLUMN_UPDATED_AT, updatedAt.getTime());
        values.put(DbContract.Wagons.COLUMN_SYNC_STATUS, syncStatus);
        return values;
    }

    public static Wagon fromCursor(android.database.Cursor cursor) {
        Wagon wagon = new Wagon();
        wagon.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_ID)));
        wagon.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_VAGON_UUID)));
        wagon.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_NUMBER)));
        wagon.setType(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_TYPE)));

        long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_CREATED_AT));
        wagon.setCreatedAt(new Date(createdAtMillis));

        long updatedAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_UPDATED_AT));
        wagon.setUpdatedAt(new Date(updatedAtMillis));

        wagon.setSyncStatus(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_SYNC_STATUS)));

        return wagon;
    }
}