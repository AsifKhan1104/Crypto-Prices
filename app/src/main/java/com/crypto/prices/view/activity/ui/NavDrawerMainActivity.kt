package com.crypto.prices.view.activity.ui

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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityMainNavDrawerBinding
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.activity.SettingsActivity
import com.crypto.prices.view.activity.SignInActivity
import com.crypto.prices.view.activity.ui.market.FragmentMarket
import com.crypto.prices.view.activity.ui.explore.FragmentMore
import kotlin.system.exitProcess

class NavDrawerMainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainNavDrawerBinding

    private var mUid: String? = null
    private var mName: String? = null
    private var mEmail: String? = null

    //private val homeFragment = HomeFragment()
    private val marketFragment = FragmentMarket()
    private val newsFragment = FragmentMore()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = marketFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavDrawerMain.toolbar)

        binding.appBarNavDrawerMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val toolbar: Toolbar = binding.appBarNavDrawerMain.toolbar
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        /*val navController = findNavController(R.id.nav_host_fragment_content_nav_drawer_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_faucet, R.id.navigation_market*//*, R.id.nav_slideshow*//*
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/

        // check if pro pop up shown
        /*if (!MySharedPrefs.getInstance(this).getBoolean(Utility.isProPopUpShown)) {
            showUpgradeTourDialog()
            MySharedPrefs.getInstance(this).saveBoolean(Utility.isProPopUpShown, true)
        }*/

        // add fragments
        fragmentManager.beginTransaction().apply {
            add(
                R.id.nav_host_fragment_content_nav_drawer_main,
                newsFragment,
                getString(R.string.title_news)
            ).hide(
                newsFragment
            )
            add(
                R.id.nav_host_fragment_content_nav_drawer_main,
                marketFragment,
                getString(R.string.title_market)
            )/*.hide(
                marketFragment
            )
            add(
                R.id.nav_host_fragment_content_nav_drawer_main,
                homeFragment,
                getString(R.string.title_faucet)
            )*/
        }.commit()

        // handle side navigation
        navView.setNavigationItemSelectedListener { menuItem ->
            closeDrawer()
            when (menuItem.itemId) {
                /*R.id.navigation_faucet -> {
                    Utility.logAnalyitcsEvent(getString(R.string.title_faucet))
                    supportActionBar?.title = getString(R.string.title_faucet)
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment)
                        .commit()
                    activeFragment = homeFragment
                    true
                }*/
                R.id.navigation_market -> {
                    Utility.logAnalyitcsEvent(getString(R.string.title_market))
                    supportActionBar?.title = getString(R.string.title_market)
                    fragmentManager.beginTransaction().hide(activeFragment).show(marketFragment)
                        .commit()
                    activeFragment = marketFragment
                    true
                }
                R.id.navigation_explore -> {
                    Utility.logAnalyitcsEvent(getString(R.string.title_news))
                    supportActionBar?.title = getString(R.string.title_news)
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(newsFragment).commit()
                    activeFragment = newsFragment
                    true
                }
                R.id.navigation_games -> {
                    Utility.logAnalyitcsEvent(getString(R.string.title_games))
                    Utility.openChromeCustomTabUrl(this, getString(R.string.gamezop_url))
                    true
                }
                R.id.navigation_settings -> {
                    Utility.logAnalyitcsEvent(getString(R.string.settings))
                    val intent = Intent(this@NavDrawerMainActivity, SettingsActivity::class.java)
                    intent.putExtra("email", mEmail)
                    intent.putExtra("name", mName)
                    startActivity(intent)
                    true
                }
                R.id.navigation_share -> {
                    Utility.logAnalyitcsEvent(getString(R.string.share))
                    Utility.sendShareLink(this)
                    true
                }
                else -> false
            }
        }

        // set user data
        //mUid = intent.getStringExtra("uid")
        //mName = intent.getStringExtra("name")
        //mEmail = intent.getStringExtra("email")
        // set data in nav drawer header
        //binding.navView.getHeaderView(0).textView_name.text = mName
        //binding.navView.getHeaderView(0).textView_email.text = mEmail

        //showRewardDialogs()

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
                                MySharedPrefs.getInstance(this@NavDrawerMainActivity)
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
                val intent = Intent(this@NavDrawerMainActivity, SettingsActivity::class.java)
                intent.putExtra("email", mEmail)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }
            /*R.id.menu_withdraw_req -> {
                val intent = Intent(this@NavDrawerMainActivity, WithdrawHistoryActivity::class.java)
                intent.putExtra("uid", mUid)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nav_drawer_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes") { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            MySharedPrefs.getInstance(this@NavDrawerMainActivity)
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

    private fun closeDrawer() {
        val drawer = binding.drawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
    }
}