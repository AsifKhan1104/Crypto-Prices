package com.crypto.prices.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityMainBinding
import com.crypto.prices.view.ui.more.MoreFragment
import com.crypto.prices.view.ui.explore.NewsFragment
import com.crypto.prices.view.ui.home.HomeFragment
import com.crypto.prices.view.ui.market.MarketFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment.newInstance()
    private val marketFragment = MarketFragment.newInstance()
    private val newsFragment = NewsFragment.newInstance()
    private val moreFragment = MoreFragment.newInstance()
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // default home page
        openFragment(homeFragment)

        // add fragments
        fragmentManager.beginTransaction().apply {
            add(R.id.container, moreFragment, getString(R.string.title_more)).hide(
                newsFragment
            )
            add(R.id.container, newsFragment, getString(R.string.title_news)).hide(
                moreFragment
            )
            add(R.id.container, marketFragment, getString(R.string.title_market)).hide(
                newsFragment
            )
            add(R.id.container, homeFragment, getString(R.string.title_home)).hide(marketFragment)
        }.commitNow()

        // handle bottom navigation
        binding?.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    //supportActionBar?.title = getString(R.string.title_home)
                    openFragment(homeFragment)
                    activeFragment = homeFragment
                    true
                }
                R.id.navigation_market -> {
                    //supportActionBar?.title = getString(R.string.title_market)
                    openFragment(marketFragment)
                    activeFragment = marketFragment
                    true
                }
                R.id.navigation_explore -> {
                    //supportActionBar?.title = getString(R.string.title_explore)
                    openFragment(newsFragment)
                    activeFragment = newsFragment
                    true
                }
                R.id.navigation_more -> {
                    //supportActionBar?.title = getString(R.string.title_more)
                    openFragment(moreFragment)
                    activeFragment = moreFragment
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        //Log.d(TAG, "openFragment: ")
        /*val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // this is a helper class that replaces the container with the fragment. You can replace or add fragments.
        transaction.replace(binding.container.id, fragment)
        // if you add fragments it will be added to the backStack. If you replace the fragment it will
        // add only the last fragment
        //transaction.addToBackStack(null)
        transaction.commitNow()*/
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment)
            .commitNow()
    }

    // handling show more button from home page
    fun clickExploreMenu() {
        binding?.bottomNavigationView?.menu.getItem(2).setChecked(true)
        openFragment(newsFragment)
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            *//*R.id.menu_logout -> {
                signOut()

                true
            }*//*
            R.id.menu_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("email", mEmail)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }
            *//*R.id.menu_withdraw_req -> {
                val intent = Intent(this@MainActivity2, WithdrawHistoryActivity::class.java)
                intent.putExtra("uid", mUid)
                intent.putExtra("name", mName)
                startActivity(intent)

                true
            }*//*
            else -> super.onOptionsItemSelected(item)
        }
    }*/
}