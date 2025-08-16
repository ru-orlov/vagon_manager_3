package com.example.wagonmanager3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.models.ScanHistory;
import com.example.wagonmanager3.models.User;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class ScanActivity extends AppCompatActivity {
    private CameraSource cameraSource;
    private User currentUser;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        currentUser = getCurrentUser();
        setupBarcodeDetector();
    }

    private void setupBarcodeDetector() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Не удалось инициализировать сканер QR-кодов", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0 && isScanning) {
                    isScanning = false; // Предотвращаем множественное срабатывание
                    String scannedData = barcodes.valueAt(0).displayValue;
                    runOnUiThread(() -> processScannedData(scannedData));
                }
            }
        });

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1024, 768)
                .setAutoFocusEnabled(true)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка запуска камеры", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void processScannedData(String scannedData) {
        if (isValidWagonUuid(scannedData)) {
            saveScanHistory(scannedData);
            navigateToWagonInventory(scannedData);
        } else {
            Toast.makeText(this, "Неверный QR-код вагона", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isValidWagonUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void saveScanHistory(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ScanHistory historyItem = new ScanHistory(
                UUID.randomUUID().toString(),
                wagonUuid,
                getWagonNumberByUuid(wagonUuid),
                currentUser.getUuid(),
                new Date()
        );
        dbHelper.addScanHistory(historyItem);
    }

    private String getWagonNumberByUuid(String wagonUuid) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getWagonNumberByUuid(wagonUuid);
    }

    private void navigateToWagonInventory(String wagonUuid) {
        Intent intent = new Intent(this, WagonInventoryActivity.class);
        intent.putExtra("wagon_uuid", wagonUuid);
        startActivity(intent);
        finish();
    }

    private User getCurrentUser() {
        // Реализация получения текущего пользователя
        return new User(
                UUID.randomUUID().toString(),
                "test_user",
                "hashed_password",
                "Иван Иванов",
                "responsible",
                true
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}