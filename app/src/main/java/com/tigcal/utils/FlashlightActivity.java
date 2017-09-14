package com.tigcal.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FlashlightActivity extends AppCompatActivity {
    private static final String TAG = "FlashlightActivity";

    private static final int REQUEST_CAMERA = 0;

    private TextView toggleView;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean flashlightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggleView = (TextView) findViewById(R.id.flashlight_button);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        flashlightOn = false;
        toggleCamera2Flashlight(flashlightOn);
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleCamera2Flashlight(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO
                } else {
                    Toast.makeText(this, getString(R.string.fl_camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void toggleCamera2Flashlight(boolean checked) {
        try {
            cameraManager.setTorchMode(cameraId, checked);
            toggleView.setBackgroundColor(checked ?
                    ContextCompat.getColor(FlashlightActivity.this, R.color.fl_primary) :
                    Color.WHITE
            );
            toggleView.setText(checked ? getString(R.string.fl_turn_off) : getString(R.string.fl_turn_on));
        } catch (CameraAccessException e) {
            Log.d(TAG, "toggleCamera2Flashlight CameraAccessException:" + e.getMessage());
        }
    }
}
