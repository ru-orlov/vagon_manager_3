package com.example.wagonmanager3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wagonmanager3.adapters.ScanHistoryAdapter;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.database.DatabaseInitializer;
import com.example.wagonmanager3.models.ScanHistory;
import com.example.wagonmanager3.models.User;
import com.example.wagonmanager3.models.Wagon;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ScanHistoryAdapter.OnItemClickListener {
    private RecyclerView rvScanHistory;
    private ScanHistoryAdapter adapter;
    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String vagonUuid = result.getContents();
                    Intent intent = new Intent(this, WagonInventoryActivity.class);
                    intent.putExtra("WagonUuid", vagonUuid);
                    logScanHistrory(intent);
                    startActivity(intent);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Инициализация тестовых данных
        initializeTestDataOnce();

        setupToolbar();
        setupRecyclerView();
        loadScanHistory();

        ImageButton btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Наведите камеру на QR-код вагона");
            options.setOrientationLocked(false);
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            barcodeLauncher.launch(options);
        });
    }

    private void logScanHistrory(Intent intent) {
        String wagonUuid = intent.getStringExtra("WagonUuid");
        Wagon wagon = dbHelper.getWagonByUuid(wagonUuid);
        User user = dbHelper.getRandomUser();
        if (wagon != null && user != null) {
            ScanHistory scan = new ScanHistory(
                    UUID.randomUUID().toString(),
                    wagon.getUuid(),
                    wagon.getNumber(),
                    user.getUuid(),
                    new Date()
            );
            dbHelper.addScanHistory(scan);
        }
    }
    private void initializeTestDataOnce() {
        boolean isInitialized = getDatabasePath(dbHelper.getDatabaseName()).exists();
        if (!isInitialized) {
            DatabaseInitializer dbInit = new DatabaseInitializer(this);
            dbInit.initializeTestData();
        }
    }

    private void setupToolbar() {
        // Явно указываем тип Toolbar из AppCompat
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Устанавливаем заголовок
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Журнал ВУ-9");
        }

//        ImageButton btnScan = findViewById(R.id.btn_scan);
//        btnScan.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
//            startActivity(intent);
//        });
    }
    private void showWagonNotFoundAndReturn() {
        // Можно использовать Toast, Snackbar или AlertDialog — вот пример с Toast
        Toast.makeText(this, "Вагон не найден", Toast.LENGTH_LONG).show();
        // Если вы уже на главном экране — можно ничего не делать, иначе:
        // Если вы хотите всегда возвращаться на главный экран:
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
        Intent intent = new Intent(this, WagonInventoryActivity.class);
        intent.putExtra("WagonUuid", scanHistory.getWagonUuid());
        startActivity(intent);
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