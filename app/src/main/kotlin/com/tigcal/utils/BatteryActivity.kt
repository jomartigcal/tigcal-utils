package com.tigcal.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BatteryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val powerUsageIntent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        val resolveInfo = packageManager.resolveActivity(powerUsageIntent, 0)
        if (resolveInfo != null) startActivity(powerUsageIntent)
        finish()
    }

}
