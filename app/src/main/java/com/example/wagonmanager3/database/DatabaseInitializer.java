package com.example.wagonmanager3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

        // Добавляем группы инвентаря
        InventoryGroup group1 = createGroup("Внутреннее оборудование", "Оборудование внутри вагона");
        InventoryGroup group2 = createGroup("Электрооборудование", "Электрические компоненты");
        InventoryGroup group3 = createGroup("Сантехника", "Сантехническое оборудование");

        // Добавляем инвентарь для групп
        List<InventoryItem> group1Items = createInternalEquipmentItems(group1.getId());
        List<InventoryItem> group2Items = createElectricalEquipmentItems(group2.getId());
        List<InventoryItem> group3Items = createPlumbingEquipmentItems(group3.getId());

        // Создаем 3 вагона с инвентарем
        Wagon wagon1 = createWagonWithInventory("123456", "Купе", "VU-123",
                group1Items, group2Items, group3Items);
        Wagon wagon2 = createWagonWithInventory("654321", "Плацкарт", "VU-456",
                group1Items, group2Items, group3Items);
        Wagon wagon3 = createWagonWithInventory("987654", "СВ", "VU-789",
                group1Items, group2Items, group3Items);

        // Добавляем по одному сканированию для каждого вагона
        createSingleScanHistory(wagon1, conductorUser);
        createSingleScanHistory(wagon2, conductorUser);
        createSingleScanHistory(wagon3, responsibleUser);
    }

    private Wagon createWagonWithInventory(String number, String type, String vu9Number,
                                           List<InventoryItem> group1Items,
                                           List<InventoryItem> group2Items,
                                           List<InventoryItem> group3Items) {
        // Создаем вагон
        Wagon wagon = createWagon(number, type, vu9Number, new Date());

        // Добавляем инвентарь в вагон
        addInventoryToWagon(wagon, group1Items, group2Items, group3Items);

        return wagon;
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

    private Wagon createWagon(String number, String type, String vu9Number, Date vu9Date) {
        Wagon wagon = new Wagon(number, type, vu9Number, vu9Date);
        dbHelper.addWagon(wagon);
        return wagon;
    }

    private InventoryGroup createGroup(String name, String description) {
        InventoryGroup group = new InventoryGroup(name, description);
        long groupId = dbHelper.addInventoryGroup(group);
        group.setId(groupId);
        return group;
    }

    private List<InventoryItem> createInternalEquipmentItems(long groupId) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Спинки поролоновые", "", 56));
        items.add(createItem(groupId, "Зеркала", "", 40));
        items.add(createItem(groupId, "Автошторы", "", 16));
        items.add(createItem(groupId, "Поручни оконные", "", 12));
        items.add(createItem(groupId, "Стул поворотный (откидной)", "", 8));
        return items; // Оставим только 5 элементов для каждого типа
    }

    private List<InventoryItem> createElectricalEquipmentItems(long groupId) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Высоковольтная магистраль", "", 8));
        items.add(createItem(groupId, "Розетка высоковольтная", "", 12));
        items.add(createItem(groupId, "Электрощит (комплект)", "", 2));
        items.add(createItem(groupId, "Плафоны круглые", "", 18));
        items.add(createItem(groupId, "Видеорегистратор", "", 2));
        return items;
    }

    private List<InventoryItem> createPlumbingEquipmentItems(long groupId) {
        List<InventoryItem> items = new ArrayList<>();
        items.add(createItem(groupId, "Унитаз в сборе", "", 3));
        items.add(createItem(groupId, "Шланг для душа", "", 1));
        items.add(createItem(groupId, "Вкладыш диванный", "", 6));
        return items;
    }

    private InventoryItem createItem(long groupId, String name, String description, int quantity) {
        InventoryItem item = new InventoryItem(
                UUID.randomUUID().toString(),
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
                wagonInventory.setWagonId(wagon.getId());
                wagonInventory.setItemId(item.getId());
                wagonInventory.setQuantity(item.getQuantity());
                wagonInventory.setCondition(conditions[random.nextInt(conditions.length)]);
                wagonInventory.setNotes("Примечание для " + item.getName());

                dbHelper.addWagonInventory(wagonInventory);
            }
        }
    }
}