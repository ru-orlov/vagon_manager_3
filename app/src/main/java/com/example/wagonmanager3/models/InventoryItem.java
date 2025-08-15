package com.example.wagonmanager3.models;

import com.example.wagonmanager3.database.DbContract;

import java.util.Date;
import java.util.UUID;

public class InventoryItem {
    private long id;
    private String uuid;
    private long groupId;
    private String name;
    private String description;
    private int quantity;
    private Date createdAt;
    private Date updatedAt;
    private String syncStatus;

    public InventoryItem() {
        this.uuid = UUID.randomUUID().toString();
        this.quantity = 1;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.syncStatus = "synced";
    }

    public InventoryItem(long groupId, String name, String description, int quantity) {
        this();
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    // Геттеры
    public long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
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

    public void setGroupId(long groupId) {
        this.groupId = groupId;
        this.updatedAt = new Date();
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = new Date();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Date();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = new Date();
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
        return name + " (Кол-во: " + quantity + ")";
    }

    // Преобразование в ContentValues для работы с SQLite
    public android.content.ContentValues toContentValues() {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DbContract.InventoryItems.COLUMN_UUID, uuid);
        values.put(DbContract.InventoryItems.COLUMN_GROUP_ID, groupId);
        values.put(DbContract.InventoryItems.COLUMN_NAME, name);
        values.put(DbContract.InventoryItems.COLUMN_DESCRIPTION, description);
        values.put(DbContract.InventoryItems.COLUMN_QUANTITY, quantity);
        values.put(DbContract.InventoryItems.COLUMN_CREATED_AT, createdAt.getTime());
        values.put(DbContract.InventoryItems.COLUMN_UPDATED_AT, updatedAt.getTime());
        values.put(DbContract.InventoryItems.COLUMN_SYNC_STATUS, syncStatus);
        return values;
    }

    // Создание объекта из Cursor
    public static InventoryItem fromCursor(android.database.Cursor cursor) {
        InventoryItem item = new InventoryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_ID)));
        item.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_UUID)));
        item.setGroupId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_GROUP_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_NAME)));
        item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_DESCRIPTION)));
        item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_QUANTITY)));

        long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_CREATED_AT));
        item.setCreatedAt(new Date(createdAtMillis));

        long updatedAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_UPDATED_AT));
        item.setUpdatedAt(new Date(updatedAtMillis));

        item.setSyncStatus(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.InventoryItems.COLUMN_SYNC_STATUS)));

        return item;
    }
}