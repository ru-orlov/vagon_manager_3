package com.example.wagonmanager3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.adapters.InventorySectionAdapter;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.InventoryGroup;
import com.example.wagonmanager3.models.InventoryItem;
import com.example.wagonmanager3.models.Wagon;

import java.util.List;

public class WagonInventoryActivity extends AppCompatActivity implements InventorySectionAdapter.OnItemActionListener {
    private RecyclerView inventoryRecyclerView;
    private String wagonUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wagon_inventory);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        wagonUuid = getIntent().getStringExtra("WagonUuid");

        getSupportActionBar().setTitle("Инвентарь вагона " + getWagonNumber(wagonUuid));

        setupRecyclerView();
        loadData();
    }

    private void setupRecyclerView() {
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadData() {
        List<InventoryItem> inventoryItems = loadInventoryList(wagonUuid);
        List<InventoryGroup> inventoryGroups = loadInventoryGroupList(wagonUuid);

        InventorySectionAdapter adapter = new InventorySectionAdapter(inventoryGroups, inventoryItems, this);
        inventoryRecyclerView.setAdapter(adapter);
    }

    private String getWagonNumber(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Wagon wagon = dbHelper.getWagonByUuid(wagonUuid);
        return wagon.getNumber();
    }

    private List<InventoryItem> loadInventoryList(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getInventoryItemsByWagonUuid(wagonUuid);
    }

    private List<InventoryGroup> loadInventoryGroupList(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getInventoryGroupByWagonUuid(wagonUuid);
    }

    @Override
    public void onAddItem(InventoryItem item) {
        // Увеличить количество на 1
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        item.setQuantity(item.getQuantity() + 1);
        // Обновить в базе данных
        // dbHelper.updateInventoryItem(item);
        Toast.makeText(this, "Количество увеличено", Toast.LENGTH_SHORT).show();
        loadData(); // Перезагрузить данные
    }

    @Override
    public void onEditItem(InventoryItem item) {
        Intent intent = new Intent(this, InventoryEditActivity.class);
        intent.putExtra("wagon_uuid", wagonUuid);
        intent.putExtra("item_uuid", item.getUuid());
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDeleteItem(InventoryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить позицию \"" + item.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    DatabaseHelper dbHelper = new DatabaseHelper(this);
                    // dbHelper.deleteInventoryItem(item);
                    Toast.makeText(this, "Позиция удалена", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}