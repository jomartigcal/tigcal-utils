package com.tigcal.utils

import android.app.Application
import androidx.preference.PreferenceManager
import com.tigcal.utils.util.ThemeHelper

class TigcalUtilsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = prefs.getString(getString(R.string.settings_theme_preference), ThemeHelper.DEFAULT_MODE)
        ThemeHelper.applyTheme(themePref)
    }
}
