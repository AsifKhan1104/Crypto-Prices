package com.crypto.prices.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.*
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.crypto.prices.BuildConfig
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.view.activity.MainActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

object Utility {
    const val isPro = "isPro"
    const val spinWheelTime = "spinWheelTime"

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
            // initializing object for custom chrome tabs.
            val customIntent = CustomTabsIntent.Builder()

            // below line is setting toolbar color
            // for our custom chrome tab.
            /*customIntent.setToolbarColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )*/
            val customTabsIntent = customIntent.build()

            // package name is the default package
            // for our custom chrome tab
            val packageName = "com.android.chrome"
            if (packageName != null) {

                // we are checking if the package name is not null
                // if package name is not null then we are calling
                // that custom chrome tab with intent by passing its
                // package name.
                customTabsIntent.intent.setPackage(packageName)
            }
            // in that custom tab intent we are passing
            // our url which we have to browse.
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
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        )

        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    // log event in firebase analytics
    fun logAnalyitcsEvent(screenName: String) {
        /*Firebase.analytics.logEvent(shibaScreenView) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }*/
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

    fun isInternetAvailable(): Boolean {
        val conMgr =
            CryptoApplication.instance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network: Network? = conMgr.activeNetwork
            val networkCapabilities = conMgr.getNetworkCapabilities(network)
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        } else {
            // below API Level 23
            (conMgr.activeNetworkInfo != null && conMgr.activeNetworkInfo!!.isAvailable
                    && conMgr.activeNetworkInfo!!.isConnected)
        }
    }

    fun openAppInPlayStore(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        } catch (exception: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    fun composeEmail(context: Context) {
        // compose subject
        var subject: String = context.getString(R.string.app_name) + " Issue"
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data =
            Uri.parse("mailto:indianapps47@gmail.com") // only email apps should handle this

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            selector = selectorIntent
        }
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(emailIntent)
        }
    }

    fun openWebURL(context: Context, url: String) {
        var url = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        context.startActivity(browserIntent)
    }

    // save currency in shared prefs
    fun setCurrency(activity: Activity, currency: String?) {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_currency", currency)
            apply()
        }
    }

    fun setCurrencyName(activity: Activity, currency: String?) {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_currency_name", currency)
            apply()
        }
    }

    fun setCurrencySymbol(activity: Activity, currency: String?) {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_currency_symbol", currency)
            apply()
        }
    }

    // get currency from shared prefs
    fun getCurrency(activity: Activity): String? {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val selectedCurrency = sharedPref.getString("selected_currency", null)
        return selectedCurrency
    }

    fun getCurrencyName(activity: Activity): String? {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val selectedCurrency = sharedPref.getString("selected_currency_name", null)
        return selectedCurrency
    }

    fun getCurrencySymbol(activity: Activity): String? {
        val sharedPref = activity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val selectedCurrency = sharedPref.getString("selected_currency_symbol", null)
        return selectedCurrency
    }

    // restart app method
    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finishAffinity()
    }
}

