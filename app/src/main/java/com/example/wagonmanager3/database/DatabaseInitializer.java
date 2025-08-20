package com.example.wagonmanager3.database;

import android.content.Context;

import com.example.wagonmanager3.models.*;

import java.util.*;

public class DatabaseInitializer {
    private final DatabaseHelper dbHelper;

    public DatabaseInitializer(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void initializeTestData() {
        // Очищаем базу перед добавлением тестовых данных
        String wagon1Uuid = UUID.randomUUID().toString();
        String wagon2Uuid = UUID.randomUUID().toString();
        String wagon3Uuid = UUID.randomUUID().toString();
        // Добавляем группы инвентаря
        InventoryGroup group1 = createGroup(UUID.randomUUID().toString(),"Внутреннее оборудование", wagon1Uuid, "Оборудование внутри вагона");
        InventoryGroup group11 = createGroup(UUID.randomUUID().toString(),"Внутреннее оборудование", wagon2Uuid, "Оборудование внутри вагона");
        InventoryGroup group12 = createGroup(UUID.randomUUID().toString(),"Внутреннее оборудование", wagon3Uuid, "Оборудование внутри вагона");

        InventoryGroup group2 = createGroup(UUID.randomUUID().toString(),"Электрооборудование", wagon1Uuid, "Электрические компоненты");
        InventoryGroup group21 = createGroup(UUID.randomUUID().toString(),"Электрооборудование", wagon2Uuid, "Электрические компоненты");
        InventoryGroup group22 = createGroup(UUID.randomUUID().toString(),"Электрооборудование", wagon3Uuid, "Электрические компоненты");

        InventoryGroup group3 = createGroup(UUID.randomUUID().toString(),"Сантехника", wagon1Uuid, "Сантехническое оборудование");
        InventoryGroup group31 = createGroup(UUID.randomUUID().toString(),"Сантехника", wagon2Uuid, "Сантехническое оборудование");
        InventoryGroup group32 = createGroup(UUID.randomUUID().toString(),"Сантехника", wagon3Uuid, "Сантехническое оборудование");

        // Добавляем инвентарь для групп
        createInternalEquipmentItems(group1.getUuid(), wagon1Uuid);
        createElectricalEquipmentItems(group2.getUuid(), wagon2Uuid);
        createPlumbingEquipmentItems(group3.getUuid(), wagon3Uuid);

        createPlumbingEquipmentItems(group11.getUuid(), wagon2Uuid);
        createPlumbingEquipmentItems(group12.getUuid(), wagon3Uuid);
        createPlumbingEquipmentItems(group21.getUuid(), wagon2Uuid);
        createPlumbingEquipmentItems(group22.getUuid(), wagon3Uuid);
        createPlumbingEquipmentItems(group31.getUuid(), wagon2Uuid);
        createPlumbingEquipmentItems(group32.getUuid(), wagon3Uuid);


        User responsibleUser = createUser("responsible", "Ответственный", "responsible");
        User conductorUser = createUser("conductor", "Проводник Иванов", "conductor");

        Wagon wagon1 = createWagon("Вагон 1321", wagon1Uuid, "Пассажирский");
        Wagon wagon2 = createWagon("Вагон 2495", wagon2Uuid, "Пассажирский");
        Wagon wagon3 = createWagon("Вагон 3753", wagon3Uuid, "Грузовой");

//        // Добавляем по одному сканированию для каждого вагона
        createSingleScanHistory(wagon1, conductorUser);
        createSingleScanHistory(wagon2, conductorUser);
        createSingleScanHistory(wagon3, responsibleUser);
    }

    private void createSingleScanHistory(Wagon wagon, User user) {
        ScanHistory history = new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getUuid(),
                wagon.getNumber(),
                user.getUuid(),
                new Date() // Текущая дата
        );
        dbHelper.addScanHistory(history);
    }

    // Остальные методы остаются без изменений
    private User createUser(String username, String fullName, String role) {
        User user = new User(
                UUID.randomUUID().toString(),
                username,
                "hashed_password_" + username,
                fullName,
                role,
                true
        );
        dbHelper.addUser(user);
        return user;
    }

    private Wagon createWagon(String number, String vagonUuid, String type) {
        Wagon wagon = new Wagon(number, vagonUuid, type);
        dbHelper.addWagon(wagon);
        return wagon;
    }

    private InventoryGroup createGroup(String uuid, String name, String vagonUuid, String description) {
        InventoryGroup group = new InventoryGroup(uuid, name, vagonUuid, description);
        dbHelper.addInventoryGroup(group);
        return group;
    }

    private List<InventoryItem> createInternalEquipmentItems(String groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Спинки поролоновые", vagonUuid, "Описание", 56));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Зеркала", vagonUuid, "Описание", 40));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Автошторы", vagonUuid, "Описание",16));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Поручни оконные", vagonUuid, "Описание",12));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Стул поворотный (откидной)", vagonUuid, "Описание",8));
        return items; // Оставим только 5 элементов для каждого типа
    }

    private List<InventoryItem> createElectricalEquipmentItems(String groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Высоковольтная магистраль",vagonUuid, "Описание", 8));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Розетка высоковольтная", vagonUuid,"Описание", 12));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Электрощит (комплект)", vagonUuid,"Описание", 2));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Плафоны круглые", vagonUuid,"Описание", 18));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Видеорегистратор", vagonUuid,"Описание", 2));
        return items;
    }

    private List<InventoryItem> createPlumbingEquipmentItems(String groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Унитаз в сборе", vagonUuid, "Описание", 3));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Шланг для душа", vagonUuid, "Описание", 1));
        items.add(createItem(UUID.randomUUID().toString(), groupId, "Вкладыш диванный", vagonUuid, "Описание", 6));
        return items;
    }

    private InventoryItem createItem(String uuid, String groupId, String name, String vagonUuid, String description, int quantity) {
        InventoryItem item = new InventoryItem(
                uuid,
                vagonUuid,
                groupId,
                name,
                description,
                quantity,
                "synced"
        );
        long itemId = dbHelper.insertInventoryItem(item);
        item.setId(itemId);
        return item;
    }

}