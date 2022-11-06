package com.crypto.prices.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ParseException
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.crypto.prices.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat


object Utility {
    const val isPro = "isPro"
    const val isProPopUpShown = "isProPopUpShown"
    const val spinWheelTime = "spinWheelTime"
    const val shibaScreenView = "shiba_screen_view"

    const val bitBnsReferLink = "https://ref.bitbns.com/35257"
    const val binanceReferLink = "https://accounts.binance.com/en/register?ref=323840147"

    fun showToast(context: Context?, msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    fun showLongToast(context: Context?, msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    fun showDialog(context: Context?, title: String?, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    fun validateEmail(emailForValidation: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(emailForValidation).matches()
    }

    fun openOtherAppInPlayStore(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.asf.cryptoprices")
                )
            )
        } catch (exception: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.asf.cryptoprices")
                )
            )
        }
    }

    // retrieve from shared prefs
    fun getMinWithdrawCoinsFromSharedPrefs(activity: Activity): Double {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val minCoins =
            sharedPref.getString(activity.getString(R.string.min_withdraw_coins), "10000.00")

        return minCoins!!.toDouble()
    }

    fun openChromeCustomTabUrl(context: Context, webUrl: String) {
        try {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val colorInt = Color.parseColor("#66bb6a")
            builder.setToolbarColor(colorInt);
            if (isAppInstalled(context, "com.android.chrome")) {
                builder.setStartAnimations(
                    context,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                builder.setExitAnimations(
                    context,
                    R.anim.slide_in_left,
                    R.anim.slide_in_right
                )
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.intent.setPackage("com.android.chrome");
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(context, Uri.parse(webUrl));
            } else {
                builder.setStartAnimations(
                    context,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                builder.setExitAnimations(
                    context,
                    R.anim.slide_in_left,
                    R.anim.slide_in_right
                )
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(context, Uri.parse(webUrl));
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun openChromeCustomTabUrlNews(context: Context, webUrl: String) {
        try {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabsIntent: CustomTabsIntent = builder.build()
            if (isAppInstalled(context, "com.android.chrome")) {
                customTabsIntent.intent.setPackage("com.android.chrome")
            }
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(context, Uri.parse(webUrl))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun sendShareLink(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Check out this amazing app which provides live " + context.getString(R.string.app_name) + ". Click to download: " +
                    "https://play.google.com/store/apps/details?id=com.miner.shib_miner"
        )

        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    // log event in firebase analytics
    fun logAnalyitcsEvent(screenName: String) {
        Firebase.analytics.logEvent(shibaScreenView) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    // convert date/time to time ago
    fun formatPublishedDateTime(value: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
        try {
            val time: Long = sdf.parse(value).getTime()
            return TimeAgo.getTimeAgo(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}

