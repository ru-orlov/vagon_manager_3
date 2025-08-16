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

        // Добавляем тестовые вагоны
        Wagon wagon1 = createWagon("123456", "Купе", "VU-123", new Date());
        Wagon wagon2 = createWagon("654321", "Плацкарт", "VU-456", new Date());
        Wagon wagon3 = createWagon("987654", "СВ", "VU-789", new Date());

        // Добавляем группы инвентаря
        InventoryGroup group1 = createGroup("Внутреннее оборудование", "Оборудование внутри вагона");
        InventoryGroup group2 = createGroup("Электрооборудование", "Электрические компоненты");
        InventoryGroup group3 = createGroup("Сантехника", "Сантехническое оборудование");

        // Добавляем инвентарь для групп (как в VU-9_min.pdf)
        List<InventoryItem> group1Items = createInternalEquipmentItems(group1.getId());
        List<InventoryItem> group2Items = createElectricalEquipmentItems(group2.getId());
        List<InventoryItem> group3Items = createPlumbingEquipmentItems(group3.getId());

        // Добавляем инвентарь в вагоны
        addInventoryToWagon(wagon1, group1Items, group2Items, group3Items);
        addInventoryToWagon(wagon2, group1Items, group2Items, group3Items);
        addInventoryToWagon(wagon3, group1Items, group2Items, group3Items);

        // Добавляем историю сканирований
        createScanHistory(wagon1, conductorUser, 5);
        createScanHistory(wagon2, conductorUser, 3);
        createScanHistory(wagon3, responsibleUser, 2);
        addPreScannedWagonWithHistory();
    }


    private void addPreScannedWagonWithHistory() {
        // Создаем специальный вагон, который уже сканировали
        Wagon preScannedWagon = createWagon("999999", "Купе-Люкс", "VU-999", new Date());

        // Получаем существующие группы инвентаря
        List<InventoryGroup> groups = dbHelper.getAllInventoryGroups();
        InventoryGroup internalGroup = groups.get(0);
        InventoryGroup electroGroup = groups.get(1);
        InventoryGroup plumbingGroup = groups.size() > 2 ? groups.get(2) : null;

        // Получаем существующие items из базы
        List<InventoryItem> internalItems = dbHelper.getItemsByGroup((int) internalGroup.getId());
        List<InventoryItem> electroItems = dbHelper.getItemsByGroup((int) electroGroup.getId());
        List<InventoryItem> plumbingItems = plumbingGroup != null ?
                dbHelper.getItemsByGroup((int) plumbingGroup.getId()) : new ArrayList<>();

        // Добавляем инвентарь в вагон с особыми условиями (имитация проблем)
        addInventoryWithIssues(preScannedWagon, internalItems, electroItems, plumbingItems);

        // Создаем расширенную историю сканирований для этого вагона
        createDetailedScanHistory(preScannedWagon);
    }

    private void addInventoryWithIssues(Wagon wagon,
                                        List<InventoryItem> internalItems,
                                        List<InventoryItem> electroItems,
                                        List<InventoryItem> plumbingItems) {
        // Для внутреннего оборудования - некоторые позиции отсутствуют
        for (int i = 0; i < internalItems.size(); i++) {
            InventoryItem item = internalItems.get(i);
            WagonInventory wagonInventory = new WagonInventory();
            wagonInventory.setWagonId(wagon.getId());
            wagonInventory.setItemId(item.getId());

            // Каждый 5-й элемент отсутствует
            if (i % 5 == 0) {
                wagonInventory.setQuantity(0);
                wagonInventory.setCondition("Отсутствует");
                wagonInventory.setNotes("Отсутствует с 15.01.2023");
            } else {
                wagonInventory.setQuantity(item.getQuantity());
                wagonInventory.setCondition(i % 3 == 0 ? "Требует ремонта" : "Хорошее");
                wagonInventory.setNotes("Стандартное состояние");
            }

            dbHelper.addWagonInventory(wagonInventory);
        }

        // Для электрооборудования - некоторые позиции неисправны
        for (int i = 0; i < electroItems.size(); i++) {
            InventoryItem item = electroItems.get(i);
            WagonInventory wagonInventory = new WagonInventory();
            wagonInventory.setWagonId(wagon.getId());
            wagonInventory.setItemId(item.getId());

            // Каждый 3-й элемент неисправен
            if (i % 3 == 0) {
                wagonInventory.setQuantity(item.getQuantity());
                wagonInventory.setCondition("Неисправно");
                wagonInventory.setNotes("Требуется замена. Последняя проверка: 20.02.2023");
            } else {
                wagonInventory.setQuantity(item.getQuantity());
                wagonInventory.setCondition("Рабочее");
                wagonInventory.setNotes("В норме");
            }

            dbHelper.addWagonInventory(wagonInventory);
        }

        // Для сантехники - все в порядке
        for (InventoryItem item : plumbingItems) {
            WagonInventory wagonInventory = new WagonInventory();
            wagonInventory.setWagonId(wagon.getId());
            wagonInventory.setItemId(item.getId());
            wagonInventory.setQuantity(item.getQuantity());
            wagonInventory.setCondition("Отличное");
            wagonInventory.setNotes("Новое оборудование");

            dbHelper.addWagonInventory(wagonInventory);
        }
    }

    private void createDetailedScanHistory(Wagon wagon) {
        User responsibleUser = dbHelper.getUserByUsername("responsible");
        User conductorUser = dbHelper.getUserByUsername("conductor");

        // Создаем календарь для генерации дат
        Calendar calendar = Calendar.getInstance();

        // Последнее сканирование - сегодня ответственным лицом
        dbHelper.addScanHistory(new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getUuid(),
                wagon.getNumber(),
                responsibleUser.getUuid(),
                calendar.getTime()
        ));

        // Предыдущее сканирование - неделю назад проводником
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        dbHelper.addScanHistory(new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getUuid(),
                wagon.getNumber(),
                conductorUser.getUuid(),
                calendar.getTime()
        ));

        // Еще одно сканирование - месяц назад ответственным лицом
        calendar.add(Calendar.DAY_OF_YEAR, -24); // всего 31 день назад
        dbHelper.addScanHistory(new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getUuid(),
                wagon.getNumber(),
                responsibleUser.getUuid(),
                calendar.getTime()
        ));

        // Первое сканирование - 3 месяца назад
        calendar.add(Calendar.MONTH, -2); // всего 3 месяца назад
        dbHelper.addScanHistory(new ScanHistory(
                UUID.randomUUID().toString(),
                wagon.getUuid(),
                wagon.getNumber(),
                conductorUser.getUuid(),
                calendar.getTime()
        ));
    }
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
        items.add(createItem(groupId, "Флюгарка", "", 2));
        items.add(createItem(groupId, "Лестница-стремянка", "", 2));
        items.add(createItem(groupId, "Стенки газетницы", "", 12));
        items.add(createItem(groupId, "Стенки багажные", "", 15));
        items.add(createItem(groupId, "Карман для рекламы", "", 8));
        items.add(createItem(groupId, "Рамка для объявлений", "", 23));

        return items;
    }

    private List<InventoryItem> createElectricalEquipmentItems(long groupId) {
        List<InventoryItem> items = new ArrayList<>();

        items.add(createItem(groupId, "Высоковольтная магистраль", "", 8));
        items.add(createItem(groupId, "Розетка высоковольтная", "", 12));
        items.add(createItem(groupId, "Электрощит (комплект)", "", 2));
        items.add(createItem(groupId, "Плафоны круглые", "", 18));
        items.add(createItem(groupId, "Видеорегистратор", "", 2));
        items.add(createItem(groupId, "Камера видеонаблюдения", "", 8));
        items.add(createItem(groupId, "Блок питания", "", 10));
        items.add(createItem(groupId, "Преобразователь напряжения", "", 6));
        items.add(createItem(groupId, "Пульт управления ЭЧТК", "", 1));

        return items;
    }

    private List<InventoryItem> createPlumbingEquipmentItems(long groupId) {
        List<InventoryItem> items = new ArrayList<>();

        items.add(createItem(groupId, "Унитаз в сборе", "", 3));
        items.add(createItem(groupId, "Шланг для душа", "", 1));
        items.add(createItem(groupId, "Вкладыш диванный", "", 6));
        items.add(createItem(groupId, "Рукоятки для подъема инвалидов", "", 1));

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
        // Для каждого вагона добавляем инвентарь с разным состоянием
        String[] conditions = {"Отличное", "Хорошее", "Требует ремонта", "Неисправно"};
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

    private void createScanHistory(Wagon wagon, User user, int count) {
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < count; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -i); // Разные даты для истории

            ScanHistory history = new ScanHistory(
                    UUID.randomUUID().toString(),
                    wagon.getUuid(),
                    wagon.getNumber(),
                    user.getUuid(),
                    calendar.getTime()
            );

            dbHelper.addScanHistory(history);
        }
    }

}