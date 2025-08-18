package com.example.wagonmanager3.models;

import com.example.wagonmanager3.database.DbContract;

import java.util.Date;
import java.util.UUID;

public class Wagon {
    private long id;
    private String vagonUuid;
    private String number;
    private String type;
    private String vu9Number;
    private Date vu9Date;
    private Date createdAt;
    private Date updatedAt;
    private String syncStatus;

    // Конструкторы
    public Wagon() {
        this.vagonUuid = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.syncStatus = "synced";
    }

    public Wagon(String number, String vagonUuid, String type, String vu9Number, Date vu9Date) {
        this();
        this.number = number;
        this.vagonUuid = vagonUuid;
        this.type = type;
        this.vu9Number = vu9Number;
        this.vu9Date = vu9Date;
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

    public String getVu9Number() {
        return vu9Number;
    }

    public Date getVu9Date() {
        return vu9Date;
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

    public void setVu9Number(String vu9Number) {
        this.vu9Number = vu9Number;
        this.updatedAt = new Date();
    }

    public void setVu9Date(Date vu9Date) {
        this.vu9Date = vu9Date;
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
        values.put(DbContract.Wagons.COLUMN_UUID, vagonUuid);
        values.put(DbContract.Wagons.COLUMN_NUMBER, number);
        values.put(DbContract.Wagons.COLUMN_TYPE, type);
        values.put(DbContract.Wagons.COLUMN_VU_9_NUMBER, vu9Number);
        if (vu9Date != null) {
            values.put(DbContract.Wagons.COLUMN_VU_9_DATE, vu9Date.getTime());
        }
        values.put(DbContract.Wagons.COLUMN_CREATED_AT, createdAt.getTime());
        values.put(DbContract.Wagons.COLUMN_UPDATED_AT, updatedAt.getTime());
        values.put(DbContract.Wagons.COLUMN_SYNC_STATUS, syncStatus);
        return values;
    }

    public static Wagon fromCursor(android.database.Cursor cursor) {
        Wagon wagon = new Wagon();
        wagon.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_ID)));
        wagon.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_UUID)));
        wagon.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_NUMBER)));
        wagon.setType(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_TYPE)));
        wagon.setVu9Number(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_VU_9_NUMBER)));

        long vu9DateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_VU_9_DATE));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_VU_9_DATE))) {
            wagon.setVu9Date(new Date(vu9DateMillis));
        }

        long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_CREATED_AT));
        wagon.setCreatedAt(new Date(createdAtMillis));

        long updatedAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_UPDATED_AT));
        wagon.setUpdatedAt(new Date(updatedAtMillis));

        wagon.setSyncStatus(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Wagons.COLUMN_SYNC_STATUS)));

        return wagon;
    }
}