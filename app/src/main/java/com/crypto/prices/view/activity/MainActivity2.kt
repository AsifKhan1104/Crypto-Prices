package com.crypto.prices.view.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityMain2Binding
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.activity.ui.home.HomeFragment
import com.crypto.prices.view.activity.ui.market.FragmentMarket
import com.crypto.prices.view.activity.ui.news.FragmentNews
import kotlin.system.exitProcess

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private var mUid: String? = null
    private var mName: String? = null
    private var mEmail: String? = null

    private val homeFragment = HomeFragment()
    private val marketFragment = FragmentMarket()
    private val newsFragment = FragmentNews()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if pro pop up shown
        if (!MySharedPrefs.getInstance(this).getBoolean(Utility.isProPopUpShown)) {
            showUpgradeTourDialog()
            MySharedPrefs.getInstance(this).saveBoolean(Utility.isProPopUpShown, true)
        }

        // add fragments
        fragmentManager.beginTransaction().apply {
            add(R.id.container, newsFragment, getString(R.string.title_news)).hide(
                newsFragment
            )
            add(R.id.container, marketFragment, getString(R.string.title_market)).hide(
                marketFragment
            )
            add(R.id.container, homeFragment, getString(R.string.title_faucet))
        }.commit()

        // handle bottom navigation
        binding.navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_faucet -> {
                    supportActionBar?.title = getString(R.string.title_faucet)
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment)
                        .commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.navigation_market -> {
                    supportActionBar?.title = getString(R.string.title_market)
                    fragmentManager.beginTransaction().hide(activeFragment).show(marketFragment)
                        .commit()
                    activeFragment = marketFragment
                    true
                }
                R.id.navigation_news -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(newsFragment).commit()
                    activeFragment = newsFragment
                    true
                }
                else -> false
            }
        }

        // set user data
        mUid = intent.getStringExtra("uid")
        mName = intent.getStringExtra("name")
        mEmail = intent.getStringExtra("email")

        val navView: BottomNavigationView = binding.navView

        /*val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_faucet, R.id.navigation_market, R.id.navigation_fun
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/

        showRewardDialogs()

        // set default values from remote config
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("MainActivity", "Config params updated: $updated")

                    val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString(
                            getString(R.string.min_withdraw_coins),
                            remoteConfig.getDouble("minWithdrawCoins").toString()
                        )
                        apply()
                    }

                    if (!remoteConfig.getBoolean("appServerUp")) {
                        showDialogExit(
                            this,
                            getString(R.string.alert),
                            remoteConfig.getString("serverDownMsg")
                        )
                    }
                }
            }

        // check & save pro user status
        try {
            Firebase.database.reference.child(TableManagement.PRO_USERS_LIST)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val proUserId = ds.key
                            if (mUid!!.equals(proUserId)) {
                                MySharedPrefs.getInstance(this@MainActivity2)
                                    .saveBoolean(Utility.isPro, true)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("firebase", "Error getting data for isPro " + databaseError.message)
                    }
                })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showDialogExit(context: Context?, title: String?, message: String?) {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.apply {
            setIcon(R.drawable.alert)
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            setPositiveButton("OK") { _, _ ->
                finishAffinity()
                exitProcess(0)
            }
        }.create().show()
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
                val intent = Intent(this@MainActivity2, SettingsActivity::class.java)
                intent.putExtra("email", mEmail)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }
            /*R.id.menu_withdraw_req -> {
                val intent = Intent(this@MainActivity2, WithdrawHistoryActivity::class.java)
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
            MySharedPrefs.getInstance(this@MainActivity2)
                .saveBoolean(Utility.isPro, false)
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

    fun showUpgradeTourDialog() {
        val builder = Dialog(this)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.getWindow()?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        builder.setOnDismissListener(DialogInterface.OnDismissListener {
            //nothing;
        })
        val imageView = ImageView(this)
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.upgrade_welcome_tour))
        builder.addContentView(
            imageView, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        builder.setCancelable(true)
        builder.show()
    }
}