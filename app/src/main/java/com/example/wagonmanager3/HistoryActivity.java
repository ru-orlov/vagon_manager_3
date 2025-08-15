package com.example.wagonmanager3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.wagonmanager3.adapters.HistoryAdapter;
import com.example.wagonmanager3.database.DatabaseHelper;
import com.example.wagonmanager3.database.DbContract;
import com.example.wagonmanager3.models.ScanHistory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {
    private ListView historyListView;
    private HistoryAdapter adapter;
    private List<ScanHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.history_list);
        loadHistoryData();

        // Инициализация адаптера с правильными параметрами
        adapter = new HistoryAdapter(this, historyList, historyItem -> {
            showWagonQR(historyItem.getWagonUuid());
        });

        historyListView.setAdapter(adapter);
    }

    private void loadHistoryData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                DbContract.ScanHistory.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                DbContract.ScanHistory.COLUMN_SCAN_TIME + " DESC")) {

            historyList.clear();
            while (cursor.moveToNext()) {
                ScanHistory item = new ScanHistory(
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_WAGON_UUID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_WAGON_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_USER_UUID)),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.ScanHistory.COLUMN_SCAN_TIME)))
                );
                historyList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showWagonQR(String wagonUuid) {
        // 1. Создаем диалоговое окно
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR-код вагона");

        // 2. Создаем ImageView для QR-кода
        ImageView imageView = new ImageView(this);
        imageView.setPadding(32, 32, 32, 32);

        try {
            // 3. Генерируем QR-код
            Bitmap qrCode = generateQRCode(wagonUuid, 400, 400);
            imageView.setImageBitmap(qrCode);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка генерации QR-кода", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Настройка диалога
        builder.setView(imageView)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Поделиться", (dialog, which) -> shareQRCode(wagonUuid));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Генерация QR-кода
    private Bitmap generateQRCode(String content, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                Collections.singletonMap(EncodeHintType.MARGIN, 1)
        );

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    // Поделиться QR-кодом
    private void shareQRCode(String wagonUuid) {
        try {
            Bitmap qrCode = generateQRCode(wagonUuid, 400, 400);
            File file = saveBitmapToCache(qrCode);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            Uri uri = FileProvider.getUriForFile(
                    this,
                    "ru.orlov.myapplication.fileprovider",
                    file
            );
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Поделиться QR-кодом"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при отправке QR-кода", Toast.LENGTH_SHORT).show();
        }
    }

    // Сохранение во временный файл
    private File saveBitmapToCache(Bitmap bitmap) throws IOException {
        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs();
        File file = File.createTempFile("qr_code", ".png", cachePath);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        return file;
    }
}