package com.tigcal.utils

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class AboutDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_about, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setTitle(context?.getString(R.string.about_header))

        val appVersion = "\n${context?.getString(R.string.app_name)} ${getAppVersion()}\n"
        val versionText: TextView = view.findViewById(R.id.textview_app_version)
        versionText.text = appVersion

        val linkMovementMethod = LinkMovementMethod.getInstance()
        val developerText: TextView = view.findViewById(R.id.textview_app_developer)
        developerText.movementMethod = linkMovementMethod
        val contactText: TextView = view.findViewById(R.id.textview_app_contact)
        contactText.movementMethod = linkMovementMethod
    }

    private fun getAppVersion(): String {
        return try {
            context?.packageManager?.getPackageInfo(context?.packageName ?: "", 0)?.versionName
                    ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(DIALOG_TAG, "package name not found")
            ""
        }
    }

    companion object {
        private val DIALOG_TAG = AboutDialog::class.java.simpleName
        fun newInstance(): AboutDialog {
            return AboutDialog()
        }
    }
}