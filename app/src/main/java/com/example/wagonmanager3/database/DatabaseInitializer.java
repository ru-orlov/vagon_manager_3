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
        dbHelper.clearAllTables();

        // Добавляем тестовых пользователей
        User responsibleUser = createUser("responsible", "Ответственный", "responsible");
        User conductorUser = createUser("conductor", "Проводник Иванов", "conductor");

        String wagon1Uuid = UUID.randomUUID().toString();
        String wagon2Uuid = UUID.randomUUID().toString();
        String wagon3Uuid = UUID.randomUUID().toString();
        // Добавляем группы инвентаря
        InventoryGroup group1 = createGroup("Внутреннее оборудование", wagon1Uuid, "Оборудование внутри вагона");
        InventoryGroup group2 = createGroup("Электрооборудование", wagon2Uuid, "Электрические компоненты");
        InventoryGroup group3 = createGroup("Сантехника", wagon3Uuid, "Сантехническое оборудование");

        // Добавляем инвентарь для групп
        List<InventoryItem> group1Items = createInternalEquipmentItems(group1.getId(), wagon1Uuid);
        List<InventoryItem> group2Items = createElectricalEquipmentItems(group2.getId(), wagon2Uuid);
        List<InventoryItem> group3Items = createPlumbingEquipmentItems(group3.getId(), wagon3Uuid);


        // Создаем 3 вагона с инвентарем
        Wagon wagon1 = createWagonWithInventory("123456", wagon1Uuid, "Купе", "VU-123",
                group1Items, group2Items, group3Items);
        Wagon wagon2 = createWagonWithInventory("654321", wagon2Uuid, "Плацкарт", "VU-456",
                group1Items, group2Items, group3Items);
        Wagon wagon3 = createWagonWithInventory("987654", wagon3Uuid, "СВ", "VU-789",
                group1Items, group2Items, group3Items);

        // Добавляем по одному сканированию для каждого вагона
        createSingleScanHistory(wagon1, conductorUser);
        createSingleScanHistory(wagon2, conductorUser);
        createSingleScanHistory(wagon3, responsibleUser);
    }

    private Wagon createWagonWithInventory(String number, String vagonUuid, String type, String vu9Number,
                                           List<InventoryItem> group1Items,
                                           List<InventoryItem> group2Items,
                                           List<InventoryItem> group3Items) {
        // Создаем вагон
        Wagon wagon = createWagon(number, vagonUuid, type, vu9Number, new Date());

        // Добавляем инвентарь в вагон
        addInventoryToWagon(wagon, group1Items, group2Items, group3Items);

        return wagon;
    }

    private void createSingleScanHistory(Wagon wagon, User user) {
        ScanHistory history = new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getVagonUuid(),
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

    private Wagon createWagon(String number, String vagonUuid, String type, String vu9Number, Date vu9Date) {
        Wagon wagon = new Wagon(number, vagonUuid, type, vu9Number, vu9Date);
        dbHelper.addWagon(wagon);
        return wagon;
    }

    private InventoryGroup createGroup(String name, String vagonUuid, String description) {
        InventoryGroup group = new InventoryGroup(name, vagonUuid, description);
        long groupId = dbHelper.addInventoryGroup(group);
        group.setId(groupId);
        return group;
    }

    private List<InventoryItem> createInternalEquipmentItems(long groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Спинки поролоновые", vagonUuid, "Описание", 56));
        items.add(createItem(groupId, "Зеркала", vagonUuid, "Описание", 40));
        items.add(createItem(groupId, "Автошторы", vagonUuid, "Описание",16));
        items.add(createItem(groupId, "Поручни оконные", vagonUuid, "Описание",12));
        items.add(createItem(groupId, "Стул поворотный (откидной)", vagonUuid, "Описание",8));
        return items; // Оставим только 5 элементов для каждого типа
    }

    private List<InventoryItem> createElectricalEquipmentItems(long groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Высоковольтная магистраль",vagonUuid, "", 8));
        items.add(createItem(groupId, "Розетка высоковольтная", vagonUuid,"", 12));
        items.add(createItem(groupId, "Электрощит (комплект)", vagonUuid,"", 2));
        items.add(createItem(groupId, "Плафоны круглые", vagonUuid,"", 18));
        items.add(createItem(groupId, "Видеорегистратор", vagonUuid,"", 2));
        return items;
    }

    private List<InventoryItem> createPlumbingEquipmentItems(long groupId, String vagonUuid) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Унитаз в сборе", vagonUuid, "", 3));
        items.add(createItem(groupId, "Шланг для душа", vagonUuid, "", 1));
        items.add(createItem(groupId, "Вкладыш диванный", vagonUuid, "", 6));
        return items;
    }

    private InventoryItem createItem(long groupId, String name, String vagonUuid, String description, int quantity) {
        InventoryItem item = new InventoryItem(
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

    private void addInventoryToWagon(Wagon wagon, List<InventoryItem>... itemGroups) {
        String[] conditions = {"Отличное", "Хорошее", "Требует ремонта"};
        Random random = new Random();

        for (List<InventoryItem> group : itemGroups) {
            for (InventoryItem item : group) {
                WagonInventory wagonInventory = new WagonInventory();
                wagonInventory.setVagonUuid(wagon.getId());
                wagonInventory.setItemId(item.getId());
                wagonInventory.setQuantity(item.getQuantity());
                wagonInventory.setCondition(conditions[random.nextInt(conditions.length)]);
                wagonInventory.setNotes("Примечание для " + item.getName());

                dbHelper.addWagonInventory(wagonInventory);
            }
        }
    }
}