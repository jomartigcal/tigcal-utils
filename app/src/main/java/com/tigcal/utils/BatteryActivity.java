package com.tigcal.utils;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BatteryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(powerUsageIntent, 0);
        if (resolveInfo != null) {
            startActivity(powerUsageIntent);
        }
        finish();
    }

}
