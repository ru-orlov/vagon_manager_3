package com.example.wagonmanager3.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import java.io.IOException;

public class CameraSourcePreview extends SurfaceView implements SurfaceHolder.Callback {
    private CameraSource cameraSource;

    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    public void start(CameraSource cameraSource) throws IOException {
        this.cameraSource = cameraSource;
        startIfReady();
    }

    private void startIfReady() throws IOException {
        if (cameraSource != null) {
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start(getHolder());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            startIfReady();
        } catch (IOException e) {
            // Обработка ошибки
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }
}