package com.crypto.prices.view.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.crypto.prices.R
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.adapter.MyViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mUid: String? = null
    private var mName: String? = null
    private var mEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set user data
        mUid = intent.getStringExtra("uid")
        mName = intent.getStringExtra("name")
        mEmail = intent.getStringExtra("email")

        initToolbar()
        val adapter = MyViewPagerAdapter(supportFragmentManager)

        view_pager.offscreenPageLimit = 3
        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        view_pager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (view_pager.currentItem) {
                    0 -> logScreenEvent("Earn")
                    1 -> logScreenEvent("Withdraw")
                    2 -> logScreenEvent("Refer")
                    3 -> logScreenEvent("Market")
                    else -> logScreenEvent("Earn")
                }
            }
        })
        showRewardDialogs()

        // set default values from remote config
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("MainActivity", "Config params updated: $updated")

                    val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putString(getString(R.string.min_withdraw_coins), remoteConfig.getDouble("minWithdrawCoins").toString())
                        apply()
                    }
                }
            }
    }

    // track screen event
    private fun logScreenEvent(s: String) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, s)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    private fun showRewardDialogs() {
        val referral = intent?.getBooleanExtra("referralUser", false)
        val newUser = intent?.getBooleanExtra("newUser", false)

        if (referral!! && newUser!!) {
            Utility.showDialog(
                this,
                getString(R.string.congrats_title),
                getString(
                    R.string.congrats_msg_user,
                    CoinManagement.getSignUpCoins().toString()
                ) + " & " + getString(
                    R.string.congrats_msg_invitee,
                    CoinManagement.getReferralCoins().toString()
                )
            )
        } else if (referral!!) {
            Utility.showDialog(
                this,
                getString(R.string.congrats_title),
                getString(
                    R.string.congrats_msg_invitee,
                    CoinManagement.getReferralCoins().toString()
                )
            )
        } else if (newUser!!) {
            Utility.showDialog(
                this,
                getString(R.string.congrats_title),
                getString(
                    R.string.congrats_msg_user,
                    CoinManagement.getSignUpCoins().toString()
                )
            )
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /*R.id.menu_logout -> {
                signOut()

                true
            }*/
            R.id.menu_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("email", mEmail)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }
            /*R.id.menu_withdraw_req -> {
                val intent = Intent(this@MainActivity, WithdrawHistoryActivity::class.java)
                intent.putExtra("uid", mUid)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            // move to sign in activity
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.setTitle("Alert")
        builder.setMessage(getString(R.string.signout_confirmation))
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

    }
}