package com.example.wagonmanager3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.adapters.ScanHistoryAdapter;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.database.DatabaseInitializer;
import com.example.wagonmanager3.models.ScanHistory;
import androidx.appcompat.widget.Toolbar;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ScanHistoryAdapter.OnItemClickListener {
    private RecyclerView rvScanHistory;
    private ScanHistoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Инициализация тестовых данных
        new DatabaseInitializer(this).initializeTestData();
        System.out.println(">>> onCreate");
        setupToolbar();
        setupRecyclerView();
        loadScanHistory();
    }

    private void setupToolbar() {
        // Явно указываем тип Toolbar из AppCompat
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Устанавливаем заголовок
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Журнал ВУ-9");
        }

        ImageButton btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        });
    }



    private void setupRecyclerView() {
        rvScanHistory = findViewById(R.id.rv_scan_history);
        rvScanHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScanHistoryAdapter(new ArrayList<>(), this);
        rvScanHistory.setAdapter(adapter);

        // Добавляем разделитель между элементами
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvScanHistory.getContext(),
                DividerItemDecoration.VERTICAL
        );
        rvScanHistory.addItemDecoration(dividerItemDecoration);
    }

    private void loadScanHistory() {
        List<ScanHistory> historyList = dbHelper.getAllScanHistory();
        adapter.updateData(historyList);
    }

    @Override
    public void onItemClick(ScanHistory scanHistory) {
        showEditDialog(scanHistory);
    }

    @Override
    public void onItemLongClick(ScanHistory scanHistory) {
        showDeleteDialog(scanHistory);
    }

    private void showEditDialog(ScanHistory scanHistory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактирование записи");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_scan, null);
        EditText etNotes = view.findViewById(R.id.et_notes);

        builder.setView(view);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            // Здесь можно добавить логику сохранения изменений
            Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    private void showDeleteDialog(ScanHistory scanHistory) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    dbHelper.deleteScanHistory(scanHistory);
                    loadScanHistory(); // Обновляем список
                    Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScanHistory(); // Обновляем историю при возвращении на экран
    }
}