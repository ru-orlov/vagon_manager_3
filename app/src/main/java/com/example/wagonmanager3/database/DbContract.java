package com.example.wagonmanager3.database;

public class DbContract {
    private DbContract() {}

    // Таблица пользователей
    public static class Users {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_FULL_NAME = "full_name";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT NOT NULL,"
                + COLUMN_USERNAME + " TEXT NOT NULL,"
                + COLUMN_PASSWORD_HASH + " TEXT NOT NULL,"
                + COLUMN_FULL_NAME + " TEXT NOT NULL,"
                + COLUMN_ROLE + " TEXT NOT NULL CHECK(" + COLUMN_ROLE + " IN ('conductor', 'employee', 'responsible')),"
                + COLUMN_IS_ACTIVE + " BOOLEAN NOT NULL DEFAULT 1,"
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SYNC_STATUS + " TEXT NOT NULL DEFAULT 'synced' CHECK(" + COLUMN_SYNC_STATUS + " IN ('synced', 'modified', 'new', 'deleted'))"
                + ")";
    }

    // Таблица вагонов
    public static class Wagons {
        public static final String TABLE_NAME = "wagons";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT NOT NULL,"
                + COLUMN_NUMBER + " TEXT NOT NULL,"
                + COLUMN_TYPE + " TEXT NOT NULL,"
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SYNC_STATUS + " TEXT NOT NULL DEFAULT 'synced' CHECK(" + COLUMN_SYNC_STATUS + " IN ('synced', 'modified', 'new', 'deleted'))"
                + ")";
    }

    // Таблица групп инвентаря
    public static class InventoryGroups {
        public static final String TABLE_NAME = "inventory_groups";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_VAGON_UUID = "vagonUuid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_VAGON_UUID + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SYNC_STATUS + " TEXT NOT NULL DEFAULT 'synced' CHECK(" + COLUMN_SYNC_STATUS + " IN ('synced', 'modified', 'new', 'deleted'))"
                + ")";
    }

    // Таблица наименований инвентаря
    public static class InventoryItems {
        public static final String TABLE_NAME = "inventory_items";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_GROUP_ID = "group_id";
        public static final String COLUMN_VAGON_UUID = "vagon_uuid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT,"
                + COLUMN_GROUP_ID + " INTEGER,"
                + COLUMN_VAGON_UUID + " INTEGER,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SYNC_STATUS + " TEXT NOT NULL DEFAULT 'synced' CHECK(" + COLUMN_SYNC_STATUS + " IN ('synced', 'modified', 'new', 'deleted'))"
                + ")";

        // Индексы для ускорения запросов
        public static final String INDEX_GROUP_ID = "CREATE INDEX idx_inventory_items_group_id "
                + "ON " + TABLE_NAME + "(" + COLUMN_GROUP_ID + ")";

        public static final String INDEX_SYNC_STATUS = "CREATE INDEX idx_inventory_items_sync_status "
                + "ON " + TABLE_NAME + "(" + COLUMN_SYNC_STATUS + ")";
    }

    // Таблица истории сканирований
    public static class ScanHistory {
        public static final String TABLE_NAME = "scan_history";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_WAGON_UUID = "wagon_uuid";
        public static final String COLUMN_WAGON_NUMBER = "wagon_number";
        public static final String COLUMN_SCAN_TIME = "scan_time";
        public static final String COLUMN_USER_UUID = "user_uuid";
        public static final String COLUMN_SYNC_STATUS = "sync_status";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UUID + " TEXT NOT NULL,"
                + COLUMN_WAGON_UUID + " TEXT NOT NULL,"
                + COLUMN_WAGON_NUMBER + " TEXT NOT NULL,"
                + COLUMN_SCAN_TIME + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_USER_UUID + " TEXT NOT NULL,"
                + COLUMN_SYNC_STATUS + " TEXT NOT NULL DEFAULT 'synced' CHECK(" + COLUMN_SYNC_STATUS + " IN ('synced', 'modified', 'new', 'deleted'))"
                + ")";
    }

    // Таблица логов изменений
    public static class ChangeLogs {
        public static final String TABLE_NAME = "change_logs";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_RECORD_ID = "record_id";
        public static final String COLUMN_ACTION = "action";
        public static final String COLUMN_OLD_VALUES = "old_values";
        public static final String COLUMN_NEW_VALUES = "new_values";
        public static final String COLUMN_CHANGED_AT = "changed_at";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_TABLE_NAME + " TEXT NOT NULL,"
                + COLUMN_RECORD_ID + " INTEGER NOT NULL,"
                + COLUMN_ACTION + " TEXT NOT NULL CHECK(" + COLUMN_ACTION + " IN ('create', 'update', 'delete')),"
                + COLUMN_OLD_VALUES + " TEXT,"
                + COLUMN_NEW_VALUES + " TEXT,"
                + COLUMN_CHANGED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    }
}