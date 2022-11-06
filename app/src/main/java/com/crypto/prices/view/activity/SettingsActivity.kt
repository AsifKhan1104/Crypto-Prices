package com.crypto.prices.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crypto.prices.BuildConfig
import com.crypto.prices.R
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        initData()
    }

    private fun initData() {
        setProfileData()

        // on click listeners
        relativeLayoutRateUs.setOnClickListener(this)
        relativeLayoutShare.setOnClickListener(this)
        relativeLayoutCS.setOnClickListener(this)
        relativeLayoutPP.setOnClickListener(this)
        relativeLayoutTnC.setOnClickListener(this)
        relativeLayoutMoreApps.setOnClickListener(this)
    }

    private fun setProfileData() {
        var name:String? = ""
        if ((intent.getStringExtra("name")).toString().isBlank()) {
            name = "User"
        } else {
            name = intent.getStringExtra("name")
        }

        // check if pro user
        if(MySharedPrefs.getInstance(this).getBoolean(Utility.isPro)) {
            imageViewAvatar.background = resources.getDrawable(R.drawable.pro_user)
        }
        textViewName.text = name
        textViewEmail.text = intent.getStringExtra("email")
        textViewVersion.text = "v" + BuildConfig.VERSION_NAME
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.relativeLayoutRateUs -> {
                openAppInPlayStore()
            }
            R.id.relativeLayoutShare -> {
                Utility.sendShareLink(this)
            }
            R.id.relativeLayoutCS -> {
                composeEmail()
            }
            R.id.relativeLayoutPP -> {
                val intent = Intent(this@SettingsActivity, PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }
            R.id.relativeLayoutTnC -> {
                val intent = Intent(this@SettingsActivity, TnCActivity::class.java)
                startActivity(intent)
            }
            R.id.relativeLayoutMoreApps -> {
                Utility.openOtherAppInPlayStore(this)
            }
        }
    }

    fun openAppInPlayStore() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        } catch (exception: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    fun composeEmail() {
        // compose subject
        var subject:String
        if(MySharedPrefs.getInstance(this).getBoolean(Utility.isPro)) {
            subject = getString(R.string.app_name) + " (PRO USER)"
        } else {
            subject = getString(R.string.app_name)
        }

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data =
            Uri.parse("mailto:elanvagueapps@gmail.com") // only email apps should handle this

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
            selector = selectorIntent
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}