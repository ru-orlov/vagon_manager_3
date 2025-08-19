package com.example.wagonmanager3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.adapters.InventorySectionAdapter;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.InventoryGroup;
import com.example.wagonmanager3.models.InventoryItem;
import com.example.wagonmanager3.utils.QRCodeUtils;

import java.util.List;


public class WagonInventoryActivity extends AppCompatActivity {
    private RecyclerView inventoryRecyclerView;
    private String wagonUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wagon_inventory);

        // Добавьте эти строки:
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        wagonUuid = getIntent().getStringExtra("WagonUuid");

        // Теперь вызов getSupportActionBar() безопасен:
        getSupportActionBar().setTitle("Инвентарь вагона " + wagonUuid);

        List<InventoryItem> inventoryItems = loadInventoryList();
        List<InventoryGroup> inventoryGroups = loadInventoryGroupList();
        InventorySectionAdapter adapter = new InventorySectionAdapter(inventoryGroups, inventoryItems);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecyclerView.setAdapter(adapter);

    }

    private String getWagonNumber(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getWagonNumberByUuid(wagonUuid);
    }
    private void setupRecyclerView() {
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
    private List<InventoryItem> loadInventoryList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getInventoryItemsByWagonUuid(wagonUuid);
    }

    private List<InventoryGroup> loadInventoryGroupList() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getInventoryGroupByWagonUuid(wagonUuid);
    }
    private void showEmptyState() {
        // Показать состояние "пусто"
//        TextView emptyText = findViewById(R.string.empty_text);
//        emptyText.setVisibility(View.VISIBLE);
//        inventoryRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_wagon_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_add_item) {
//            openAddInventoryItem();
//            return true;
//        } else if (id == R.id.action_share_qr) {
//            shareWagonQr();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void openAddInventoryItem() {
        Intent intent = new Intent(this, InventoryEditActivity.class);
        intent.putExtra("wagon_uuid", wagonUuid);
        startActivity(intent);
    }

    private void shareWagonQr() {
        QRCodeUtils.shareWagonQR(this, wagonUuid);
    }
}