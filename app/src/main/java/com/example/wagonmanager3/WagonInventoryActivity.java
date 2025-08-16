package com.example.wagonmanager3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.database.DbContract;
import com.example.wagonmanager3.adapters.InventoryAdapter;
import com.example.wagonmanager3.models.WagonInventory;
import com.example.wagonmanager3.utils.QRCodeUtils;
import com.example.wagonmanager3.models.User;

import java.util.List;


public class WagonInventoryActivity extends AppCompatActivity {
    private RecyclerView inventoryList;
    private String wagonUuid;
    private RecyclerView inventoryRecyclerView;
    private InventoryAdapter adapter;
    private String wagonNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wagon_inventory);

        // Получаем данные вагона
        wagonUuid = getIntent().getStringExtra("wagon_uuid");
        wagonNumber = getWagonNumber(wagonUuid);
        Toolbar toolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Инвентарь вагона " + wagonNumber);
        inventoryRecyclerView = findViewById(R.id.inventory_list);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadInventoryData();
    }

    private String getWagonNumber(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getWagonNumberByUuid(wagonUuid);
    }
    private void setupRecyclerView() {
        inventoryList.setLayoutManager(new LinearLayoutManager(this));
        inventoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadInventoryData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<WagonInventory> inventory = dbHelper.getWagonInventory(wagonUuid);

        if (inventory.isEmpty()) {
            showEmptyState();
        } else {
           // adapter = new InventoryAdapter(inventory);
            inventoryRecyclerView.setAdapter(adapter);
        }
    }
    private void showEmptyState() {
        // Показать состояние "пусто"
        TextView emptyText = findViewById(R.id.empty_text);
        emptyText.setVisibility(View.VISIBLE);
        inventoryRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wagon_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_item) {
            openAddInventoryItem();
            return true;
        } else if (id == R.id.action_share_qr) {
            shareWagonQr();
            return true;
        }

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