package com.example.wagonmanager3.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.wagonmanager3.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class QRCodeUtils {
    public static void shareWagonQR(Context context, String wagonUuid) {
        try {
            Bitmap qrCode = generateQRCode(wagonUuid, 500, 500);
            File imageFile = saveBitmapToCache(context, qrCode);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    imageFile
            );

            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.share_qr, wagonUuid)
            ));
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка при создании QR-кода", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap generateQRCode(String text, int width, int height) throws WriterException {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Текст для QR-кода не может быть пустым");
        }

        // Настройки кодирования QR-кода
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Минимальные отступы

        try {
            // Создаем битовую матрицу QR-кода
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            int[] pixels = new int[width * height];
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = bitMatrix.get(i % width, i / width) ? Color.BLACK : Color.WHITE;
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            throw new WriterException("Ошибка генерации QR-кода: " + e.getMessage());
        }
    }

    private static File saveBitmapToCache(Context context, Bitmap bitmap) throws IOException {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs();
        File file = new File(cachePath, "wagon_qr.png");

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }

        return file;
    }
}