package com.tigcal.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BatteryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val powerUsageIntent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        val resolveInfo = getResolveInfo(powerUsageIntent)
        if (resolveInfo != null) startActivity(powerUsageIntent)
        finish()
    }

    private fun getResolveInfo(intent: Intent): ResolveInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            packageManager.resolveActivity(intent, 0)
        }
    }
}
