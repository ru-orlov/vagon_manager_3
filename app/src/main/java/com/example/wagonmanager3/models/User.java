package com.example.wagonmanager3.models;

import com.example.wagonmanager3.database.DbContract;

import java.util.Date;
import java.util.UUID;

public class User {
    private long id;
    private String uuid;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role; // "conductor", "employee", "responsible"
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private String syncStatus; // "synced", "modified", "new", "deleted"

    // Конструкторы
    public User() {
        this.uuid = UUID.randomUUID().toString();
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.syncStatus = "synced";
    }

    public User(String uuid, String username, String passwordHash,
                String fullName, String role, boolean isActive) {
        this();
        this.uuid = uuid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
    }

    // Геттеры
    public long getId() { return id; }
    public String getUuid() { return uuid; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public String getSyncStatus() { return syncStatus; }

    // Сеттеры с автоматическим обновлением даты
    public void setId(long id) { this.id = id; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = new Date();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = new Date();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = new Date();
    }

    public void setRole(String role) {
        if (!role.equals("conductor") && !role.equals("employee") && !role.equals("responsible")) {
            throw new IllegalArgumentException("Invalid user role");
        }
        this.role = role;
        this.updatedAt = new Date();
    }

    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = new Date();
    }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public void setSyncStatus(String syncStatus) {
        if (!syncStatus.equals("synced") && !syncStatus.equals("modified")
                && !syncStatus.equals("new") && !syncStatus.equals("deleted")) {
            throw new IllegalArgumentException("Invalid sync status");
        }
        this.syncStatus = syncStatus;
    }

    // Проверка прав пользователя
    public boolean canEditInventory() {
        return role.equals("responsible");
    }

    public boolean canViewInventory() {
        return role.equals("conductor") || role.equals("employee") || role.equals("responsible");
    }

    // Для работы с базой данных
    public android.content.ContentValues toContentValues() {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DbContract.Users.COLUMN_UUID, uuid);
        values.put(DbContract.Users.COLUMN_USERNAME, username);
        values.put(DbContract.Users.COLUMN_PASSWORD_HASH, passwordHash);
        values.put(DbContract.Users.COLUMN_FULL_NAME, fullName);
        values.put(DbContract.Users.COLUMN_ROLE, role);
        values.put(DbContract.Users.COLUMN_IS_ACTIVE, isActive ? 1 : 0);
        values.put(DbContract.Users.COLUMN_CREATED_AT, createdAt.getTime());
        values.put(DbContract.Users.COLUMN_UPDATED_AT, updatedAt.getTime());
        values.put(DbContract.Users.COLUMN_SYNC_STATUS, syncStatus);
        return values;
    }

    public static User fromCursor(android.database.Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_ID)));
        user.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_UUID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_USERNAME)));
        user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_PASSWORD_HASH)));
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_FULL_NAME)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_ROLE)));
        user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_IS_ACTIVE)) == 1);

        long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_CREATED_AT));
        user.setCreatedAt(new Date(createdAtMillis));

        long updatedAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_UPDATED_AT));
        user.setUpdatedAt(new Date(updatedAtMillis));

        user.setSyncStatus(cursor.getString(cursor.getColumnIndexOrThrow(DbContract.Users.COLUMN_SYNC_STATUS)));

        return user;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ") - " + role;
    }
}