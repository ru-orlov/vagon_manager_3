package com.example.wagonmanager3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.InventoryGroup;
import com.example.wagonmanager3.models.InventoryItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InventoryEditActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText etItemName, etDescription, etQuantity;
    private AutoCompleteTextView actvGroup, actvCondition;
    private ImageView ivPhoto;
    private ProgressBar progressBar;
    private Button btnSave, btnCancel;
    private String currentPhotoPath;
    private long inventoryId = -1;
    private String wagonUuid;
    private String originalName = "";
    private String originalDescription = "";
    private String originalQuantity = "";
    private String originalGroup = "";
    private String originalCondition = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);
        inventoryId = getIntent().getLongExtra("ITEM_ID", -1);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave.setOnClickListener(v -> saveInventory());

        btnSave.setOnClickListener(v -> saveInventory());

        // Set up cancel button click listener
        btnCancel.setOnClickListener(v -> handleCancel());

        initViews();
        loadInventoryData(); // This will get wagonUuid from intent
        setupDropdowns(); // This needs wagonUuid to be available
    }

    private void initViews() {
        etItemName = findViewById(R.id.et_item_name);
        etDescription = findViewById(R.id.et_description);
        etQuantity = findViewById(R.id.et_quantity);
        actvGroup = findViewById(R.id.actv_group);
        actvCondition = findViewById(R.id.actv_condition);
        ivPhoto = findViewById(R.id.iv_photo);
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.btn_take_photo).setOnClickListener(v -> dispatchTakePictureIntent());
        findViewById(R.id.btn_remove_photo).setOnClickListener(v -> removePhoto());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveInventoryItem());
    }

    private void saveInventoryItem() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // Валидация → Сохранение в БД → setResult(RESULT_OK) → finish()
        String itemName = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String group = actvGroup.getText().toString().trim();
        String condition = actvCondition.getText().toString().trim();
        inventoryId = getIntent().getLongExtra("inventory_id", -1);

        // Проверка названия (обязательно)
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Введите название предмета", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка количества (должно быть положительным числом)
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректное количество", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            inventoryId = getIntent().getLongExtra("inventory_id", -1);
            String inventoryGroupUuid = getIntent().getStringExtra("group_id");
            InventoryItem item = new InventoryItem();
            item.setName(itemName);
            item.setDescription(description);
            item.setQuantity(quantity);
            item.setGroupId(inventoryGroupUuid);
            item.setId(inventoryId);

            int success = dbHelper.updateInventoryItem(item);
            if (success != -1) {
                Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDropdowns() {
        // Группы оборудования - загружаем из базы данных
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<InventoryGroup> groupsList = null;

        // Получаем wagonUuid из Intent если он еще не загружен
        if (wagonUuid == null) {
            wagonUuid = getIntent().getStringExtra("wagon_uuid");
        }

        if (wagonUuid != null) {
            groupsList = dbHelper.getInventoryGroupByWagonUuid(wagonUuid);
        }

        List<String> groupNames = new ArrayList<>();
        if (groupsList != null && !groupsList.isEmpty()) {
            for (InventoryGroup group : groupsList) {
                groupNames.add(group.getName());
            }
        } else {
            // Fallback к статическому массиву если нет групп в БД
            try {
                String[] staticGroups = getResources().getStringArray(R.array.inventory_groups);
                if (staticGroups.length > 0) {
                    groupNames.addAll(Arrays.asList(staticGroups));
                }
            } catch (Exception e) {
                // If resource not found, add default groups
                groupNames.add("Внутреннее оборудование");
                groupNames.add("Электрооборудование");
            }
        }

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, groupNames);
        actvGroup.setAdapter(groupAdapter);

        // Состояния оборудования
        List<String> conditionNames = new ArrayList<>();
        try {
            String[] conditions = getResources().getStringArray(R.array.inventory_conditions);
            if (conditions.length > 0) {
                conditionNames.addAll(Arrays.asList(conditions));
            }
        } catch (Exception e) {
            // If resource not found, add default conditions
            conditionNames.add("Исправен");
            conditionNames.add("Требует ремонта");
            conditionNames.add("Неисправен");
            conditionNames.add("Отсутствует");
        }

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, conditionNames);
        actvCondition.setAdapter(conditionAdapter);
    }

    private void loadInventoryData() {
        inventoryId = getIntent().getLongExtra("inventory_id", -1);
        wagonUuid = getIntent().getStringExtra("wagon_uuid");

        if (inventoryId != -1) {
            // Show loading indicator
            progressBar.setVisibility(View.VISIBLE);
            // Редактирование существующего элемента
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            InventoryItem item = dbHelper.getInventoryItemById(inventoryId);
            progressBar.setVisibility(View.GONE);

            if (item != null) {
                // Загружаем данные элемента в поля формы
                etItemName.setText(item.getName() != null ? item.getName() : "");
                etDescription.setText(item.getDescription() != null ? item.getDescription() : "");
                etQuantity.setText(String.valueOf(item.getQuantity()));

                // Set group dropdown value by finding the group name
                String groupName = dbHelper.getInventoryGroupNameByUuid(item.getGroupId());

                if (groupName != null) {
                    actvGroup.setText(groupName, false);
                } else {
                    Toast.makeText(this, "Группа не найдена для этого элемента (UUID: " + item.getGroupId() + ")", Toast.LENGTH_SHORT).show();
                    if (actvGroup.getAdapter() != null && actvGroup.getAdapter().getCount() > 0) {
                        String firstGroup = (String) actvGroup.getAdapter().getItem(0);
                        actvGroup.setText(firstGroup, false);
                    }
                }

                // Set default condition since it's not stored in the model currently
                try {
                    String[] conditions = getResources().getStringArray(R.array.inventory_conditions);
                    if (conditions.length > 0) {
                        actvCondition.setText(conditions[0], false); // Set first condition as default
                    }
                } catch (Exception e) {
                    actvCondition.setText("Исправен", false); // Fallback default
                }

                setTitle("Редактировать элемент");
            } else {
                // Элемент не найден в базе данных
                Toast.makeText(this, "Элемент не найден в базе данных (ID: " + inventoryId + ")", Toast.LENGTH_LONG).show();
                finish(); // Закрываем активность, так как редактировать нечего
            }
        } else {
            // Создание нового элемента
            setTitle("Добавить элемент");
            // Set default values for new item
            try {
                String[] conditions = getResources().getStringArray(R.array.inventory_conditions);
                if (conditions.length > 0) {
                    actvCondition.setText(conditions[0], false);
                }
            } catch (Exception e) {
                actvCondition.setText("Исправен", false); // Fallback default
            }
        }
    }

    private boolean hasUnsavedChanges() {
        String currentName = etItemName.getText().toString();
        String currentDescription = etDescription.getText().toString();
        String currentQuantity = etQuantity.getText().toString();
        String currentGroup = actvGroup.getText().toString();
        String currentCondition = actvCondition.getText().toString();

        return !originalName.equals(currentName) ||
                !originalDescription.equals(currentDescription) ||
                !originalQuantity.equals(currentQuantity) ||
                !originalGroup.equals(currentGroup) ||
                !originalCondition.equals(currentCondition);
    }

    /**
     * Handle cancel button click
     */
    private void handleCancel() {
        if (hasUnsavedChanges()) {
            showCancelConfirmationDialog();
        } else {
            cancelAndFinish();
        }
    }

    /**
     * Show confirmation dialog when user has unsaved changes
     */
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Отменить изменения?")
                .setMessage("У вас есть несохраненные изменения. Вы действительно хотите отменить редактирование?")
                .setPositiveButton("Да, отменить", (dialog, which) -> {
                    Toast.makeText(this, "Изменения не сохранены", Toast.LENGTH_SHORT).show();
                    cancelAndFinish();
                })
                .setNegativeButton("Продолжить редактирование", null)
                .show();
    }

    /**
     * Cancel editing and finish activity
     */
    private void cancelAndFinish() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Override back button to handle unsaved changes
     */
    @Override
    public void onBackPressed() {
        handleCancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveInventory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInventory() {
        String name = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String group = actvGroup.getText().toString().trim();
        String condition = actvCondition.getText().toString().trim();

        if (name.isEmpty() || quantityStr.isEmpty() || group.isEmpty() || condition.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                Toast.makeText(this, "Количество должно быть положительным числом", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректное количество", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        try {
            if (inventoryId == -1) {
                // Создание нового элемента
                String uuid = java.util.UUID.randomUUID().toString();

                // Find group UUID by name (we need to map the selected group name to UUID)
                String groupUuid = findGroupUuidByName(group);
                if (groupUuid == null) {
                    Toast.makeText(this, "Группа не найдена", Toast.LENGTH_SHORT).show();
                    return;
                }

                InventoryItem newItem = new InventoryItem();
                newItem.setUuid(uuid);
                newItem.setGroupId(groupUuid);
                newItem.setVagonUuid(wagonUuid);
                newItem.setName(name);
                newItem.setDescription(description);
                newItem.setQuantity(quantity);
                newItem.setSyncStatus("new");

                long result = dbHelper.insertInventoryItem(newItem);

                if (result > 0) {
                    Toast.makeText(this, "Элемент добавлен", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Ошибка при добавлении элемента", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Обновление существующего элемента
                InventoryItem existingItem = dbHelper.getInventoryItemById(inventoryId);
                if (existingItem != null) {
                    // Find group UUID by name
                    String groupUuid = findGroupUuidByName(group);
                    if (groupUuid == null) {
                        Toast.makeText(this, "Группа не найдена", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    existingItem.setName(name);
                    existingItem.setDescription(description);
                    existingItem.setQuantity(quantity);
                    existingItem.setGroupId(groupUuid);

                    int result = dbHelper.updateInventoryItem(existingItem);

                    if (result > 0) {
                        Toast.makeText(this, "Элемент обновлен", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Ошибка при обновлении элемента", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Элемент не найден", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String findGroupUuidByName(String groupName) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<InventoryGroup> groups = dbHelper.getInventoryGroupByWagonUuid(wagonUuid);

        for (InventoryGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group.getUuid();
            }
        }
        return null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ru.orlov.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        try {
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void removePhoto() {
        currentPhotoPath = null;
        ivPhoto.setImageResource(android.R.color.transparent);
        findViewById(R.id.btn_remove_photo).setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ivPhoto.setImageURI(Uri.parse(currentPhotoPath));
            findViewById(R.id.btn_remove_photo).setVisibility(View.VISIBLE);
        }
    }
}