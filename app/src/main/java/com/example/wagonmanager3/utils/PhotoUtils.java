package com.example.wagonmanager3.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoUtils {
    private static final String FILE_PROVIDER_AUTHORITY = "ru.orlov.myapplication.fileprovider";

    // Создание файла для фотографии
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "INV_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    // Получение URI для камеры
    public static Uri getCameraUri(Context context, File photoFile) {
        return FileProvider.getUriForFile(
                context,
                FILE_PROVIDER_AUTHORITY,
                photoFile
        );
    }

    // Сохранение Bitmap в файл
    public static String saveBitmapToFile(Context context, Bitmap bitmap) {
        File photoFile;
        try {
            photoFile = createImageFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return photoFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Получение Bitmap из файла с оптимизацией памяти
    public static Bitmap getOptimizedBitmap(String photoPath, int reqWidth, int reqHeight) {
        // Чтение размеров изображения
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Расчет коэффициента масштабирования
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Чтение изображения с новыми параметрами
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    // Удаление фотографии
    public static boolean deletePhotoFile(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) {
            return false;
        }

        File photoFile = new File(photoPath);
        return photoFile.exists() && photoFile.delete();
    }

    // Получение Bitmap из галереи
    public static Bitmap getBitmapFromGallery(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}