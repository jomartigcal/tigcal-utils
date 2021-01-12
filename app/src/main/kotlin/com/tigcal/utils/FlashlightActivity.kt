package com.tigcal.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class FlashlightActivity : AppCompatActivity() {
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var flashlightOn: Boolean = false
    private var flashAvailable: Boolean = false

    private val flashlightButton by lazy {
        findViewById<TextView>(R.id.flashlight_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_Flashlight)
        setContentView(R.layout.activity_flashlight)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager?.cameraIdList?.get(0)
        } catch (e: CameraAccessException) {
            Log.d(TAG, "CameraAccessException while accessing camera:" + e.message)
        }

        flashAvailable = false
        try {
            val cameraCharacteristics = cameraManager?.getCameraCharacteristics(BACK_CAMERA)
            flashAvailable = cameraCharacteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    ?: false
        } catch (e: CameraAccessException) {
            Log.d(TAG, "CameraAccessException while accessing camera characteristics:" + e.message)
        }

        flashlightButton.setOnClickListener {
            flashlightOn = !flashlightOn
            toggleCamera2Flashlight(flashlightOn)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about -> {
                val aboutDialog: AboutDialog = AboutDialog.newInstance()
                aboutDialog.show(supportFragmentManager, getString(R.string.about_header))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_feedback -> {
                sendFeedback()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        flashlightOn = false
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            switchFlashlight(flashlightOn)
        }
    }

    override fun onStop() {
        super.onStop()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            switchFlashlight(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchFlashlight(flashlightOn)
            } else {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_flashlight))
                        .setMessage(getString(R.string.fl_camera_permission))
                        .setPositiveButton(getString(R.string.fl_okay), null)
                        .create().show()
            }
        }
    }

    private fun displayCameraError() {
        Toast.makeText(this, getString(R.string.fl_camera_error), Toast.LENGTH_SHORT).show()
    }

    private fun toggleCamera2Flashlight(checked: Boolean) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA)
            return
        }

        switchFlashlight(checked)
    }

    private fun switchFlashlight(isOn: Boolean) {
        if (cameraId == null || cameraManager == null || !flashAvailable) {
            displayCameraError()
            return
        }

        try {
            cameraId?.apply { cameraManager?.setTorchMode(this, isOn) }

            flashlightButton.setBackgroundColor(if (isOn)
                ContextCompat.getColor(this@FlashlightActivity, R.color.fl_primary)
            else
                getThemeColor(R.attr.colorSurface, Color.WHITE)
            )
            flashlightButton.text = if (isOn) getString(R.string.fl_turn_off) else getString(R.string.fl_turn_on)
            flashlightButton.setTextColor(if (isOn)
                ContextCompat.getColor(this@FlashlightActivity, R.color.primary_text)
            else
                getThemeColor(R.attr.colorOnSurface, Color.BLACK)
            )
        } catch (e: CameraAccessException) {
            displayCameraError()
            Log.d(TAG, "switchFlashlight CameraAccessException:" + e.message)
        }

    }

    private fun getThemeColor(@AttrRes themeColor: Int, defaultColor: Int): Int {
        val surfaceValue = TypedValue()
        val resolved = theme.resolveAttribute(themeColor, surfaceValue, true)
        return if (resolved) {
            ContextCompat.getColor(this, surfaceValue.resourceId)
        } else {
            defaultColor
        }
    }

    private fun sendFeedback() {
        val deviceInfo = """

            --------------------
            Device Information:
            App Version: ${getPackageVersion()}
            OS Version: ${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL})
            OS API Level: ${Build.VERSION.SDK_INT}
            Manufacturer: ${Build.MANUFACTURER}
            Model (Product): ${Build.MODEL} (${Build.PRODUCT})
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("jomar@tigcal.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_feedback_subject, getString(R.string.app_name)))
        intent.putExtra(Intent.EXTRA_TEXT, deviceInfo)
        startActivity(Intent.createChooser(intent, getString(R.string.send_feedback_header)))
    }

    private fun getPackageVersion(): String {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "package name not found")
            ""
        }
    }

    companion object {
        private val TAG = FlashlightActivity::class.java.simpleName
        private const val BACK_CAMERA = "0"

        private const val REQUEST_CAMERA = 0
    }
}
