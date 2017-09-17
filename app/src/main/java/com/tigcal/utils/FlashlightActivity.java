package com.tigcal.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FlashlightActivity extends AppCompatActivity {
    private static final String TAG = "FlashlightActivity";
    private static final String BACK_CAMERA = "0";

    private static final int REQUEST_CAMERA = 0;

    private TextView toggleView;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean flashlightOn;
    private boolean flashAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggleView = findViewById(R.id.flashlight_button);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Log.d(TAG, "CameraAccessException while accessing camera:" + e.getMessage());
        }

        flashAvailable = false;
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(BACK_CAMERA);
            flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        } catch (CameraAccessException e) {
            Log.d(TAG, "CameraAccessException while accessing camera characteristics:" + e.getMessage());
        }

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashlightOn = !flashlightOn;
                toggleCamera2Flashlight(flashlightOn);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        flashlightOn = false;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            switchFlashlight(flashlightOn);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            switchFlashlight(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switchFlashlight(flashlightOn);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.app_flashlight))
                            .setMessage(getString(R.string.fl_camera_permission))
                            .setPositiveButton(getString(R.string.fl_okay), null)
                            .create().show();
                }
                break;
        }
    }

    private void displayCameraError() {
        Toast.makeText(this, getString(R.string.fl_camera_error), Toast.LENGTH_SHORT).show();
    }

    private void toggleCamera2Flashlight(boolean checked) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        switchFlashlight(checked);
    }

    private void switchFlashlight(boolean onOrOff) {
        if (cameraId == null || !flashAvailable) {
            displayCameraError();
            return;
        }

        try {
            cameraManager.setTorchMode(cameraId, onOrOff);
            toggleView.setBackgroundColor(onOrOff ?
                    ContextCompat.getColor(FlashlightActivity.this, R.color.fl_primary) :
                    Color.WHITE
            );
            toggleView.setText(onOrOff ? getString(R.string.fl_turn_off) : getString(R.string.fl_turn_on));
        } catch (CameraAccessException e) {
            displayCameraError();
            Log.d(TAG, "toggleCamera2Flashlight CameraAccessException:" + e.getMessage());
        }
    }
}
