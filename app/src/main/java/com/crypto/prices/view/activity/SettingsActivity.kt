package com.crypto.prices.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crypto.prices.R
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.Utility


class SettingsActivity : AppCompatActivity(){/*, View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_more)

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

        // attribution text link
        val htmlText =
            "<a style='text-decoration:underline' href=\"https://www.flaticon.com\">Icons created by Freepik - Flaticon</a>"
        textViewAttribution.setClickable(true);
        textViewAttribution.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= 24) {
            textViewAttribution.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY))
        } else {
            textViewAttribution.setText(Html.fromHtml(htmlText))
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}