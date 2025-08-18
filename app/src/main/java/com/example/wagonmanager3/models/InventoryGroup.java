package com.example.wagonmanager3.models;

import com.example.wagonmanager3.database.DbContract;

import java.util.Date;
import java.util.UUID;

public class InventoryGroup {
    private long id;
    private String uuid;
    private String vagonUuid;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String syncStatus;

    public InventoryGroup() {
        this.uuid = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.syncStatus = "synced";
    }

    public InventoryGroup(String name, String vagonUuid, String description) {
        this();
        this.name = name;
        this.vagonUuid = vagonUuid;
        this.description = description;
    }

    // Геттеры
    public long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = new Date();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }

    public String getVagonUuid() {
        return vagonUuid;
    }

    public void setVagonUuid(String vagonUuid) {
        this.vagonUuid = vagonUuid;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    @Override
    public String toString() {
        return name;
    }

    // Метод для преобразования в ContentValues (для работы с SQLite)
    public android.content.ContentValues toContentValues() {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DbContract.InventoryGroups.COLUMN_UUID, uuid);
        values.put(DbContract.InventoryGroups.COLUMN_UUID, uuid);
        values.put(DbContract.InventoryGroups.COLUMN_NAME, name);
        values.put(DbContract.InventoryGroups.COLUMN_DESCRIPTION, description);
        values.put(DbContract.InventoryGroups.COLUMN_CREATED_AT, createdAt.getTime());
        values.put(DbContract.InventoryGroups.COLUMN_UPDATED_AT, updatedAt.getTime());
        values.put(DbContract.InventoryGroups.COLUMN_SYNC_STATUS, syncStatus);
        return values;
    }

    // Метод для создания объекта из Cursor
    public static InventoryGroup fromCursor(android.database.Cursor cursor) {
        InventoryGroup group = new InventoryGroup();
        group.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_ID)));
        group.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_UUID)));
        group.setVagonUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_VAGON_UUID)));
        group.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_NAME)));
        group.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_DESCRIPTION)));

        long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_CREATED_AT));
        group.setCreatedAt(new Date(createdAtMillis));

        long updatedAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_UPDATED_AT));
        group.setUpdatedAt(new Date(updatedAtMillis));

        group.setSyncStatus(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryGroups.COLUMN_SYNC_STATUS)));

        return group;
    }
}