package com.vibedev.app.links

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent

/** Opens links and copies text. The app does nothing else with the outside world. */
class Links(private val context: Context) {

    /** In-app browser tab: closing it returns to the guide. */
    fun openInTab(url: String) = attempt {
        CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(context, Uri.parse(url))
    }

    /** Let Android pick the app, so claude.ai links go to the Claude app. */
    fun openInApp(url: String) = attempt { context.startActivity(view(url)) }

    /** Full Chrome, which handles file downloads; falls back to any browser. */
    fun openInChrome(url: String) = attempt {
        try {
            context.startActivity(view(url).setPackage("com.android.chrome"))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(view(url))
        }
    }

    fun isAppInstalled(packageName: String): Boolean = try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    /** The Play Store app's page, or the Play website if the store app is missing. */
    fun openPlayStore(packageName: String) = attempt {
        try {
            context.startActivity(view("market://details?id=$packageName"))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(view("https://play.google.com/store/apps/details?id=$packageName"))
        }
    }

    fun copy(text: String) {
        val clipboard = context.getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText("Message for Claude", text))
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun view(url: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun attempt(open: () -> Unit) {
        try {
            open()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app on this phone can open that link.", Toast.LENGTH_LONG).show()
        }
    }
}
