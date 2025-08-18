-- Таблица пользователей
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  username TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  full_name TEXT NOT NULL,
  role TEXT NOT NULL CHECK(role IN ('conductor', 'employee', 'responsible')),
  is_active BOOLEAN NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted'))
);

-- Таблица вагонов
CREATE TABLE wagons (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  number TEXT UNIQUE NOT NULL,
  type TEXT NOT NULL,
  vu_9_number TEXT,
  vu_9_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted'))
);

-- Таблица групп инвентаря
CREATE TABLE inventory_groups (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted'))
);

-- Таблица наименований инвентаря
CREATE TABLE inventory_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  group_id INTEGER NOT NULL,
  name TEXT NOT NULL,
  description TEXT,
  quantity INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted')),
  FOREIGN KEY (group_id) REFERENCES inventory_groups(id)
);

-- Таблица инвентаря вагона
CREATE TABLE wagon_inventory (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  wagon_id INTEGER NOT NULL,
  item_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL DEFAULT 1,
  condition TEXT NOT NULL,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted')),
  FOREIGN KEY (wagon_id) REFERENCES wagons(id),
  FOREIGN KEY (item_id) REFERENCES inventory_items(id)
);

-- таблица истории сканирований
CREATE TABLE scan_history (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  uuid TEXT UNIQUE NOT NULL,
  wagon_uuid TEXT NOT NULL,  -- Связь через UUID для синхронизации
  wagon_number TEXT NOT NULL,  -- Для быстрого отображения
  scan_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_uuid TEXT NOT NULL,  -- Кто выполнил сканирование
  sync_status TEXT NOT NULL DEFAULT 'synced' CHECK(sync_status IN ('synced', 'modified', 'new', 'deleted')),
  FOREIGN KEY (wagon_uuid) REFERENCES wagons(uuid),
  FOREIGN KEY (user_uuid) REFERENCES users(uuid)
);

-- Таблица логов изменений
CREATE TABLE change_logs (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  table_name TEXT NOT NULL,
  record_id INTEGER NOT NULL,
  action TEXT NOT NULL CHECK(action IN ('create', 'update', 'delete')),
  old_values TEXT,
  new_values TEXT,
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);