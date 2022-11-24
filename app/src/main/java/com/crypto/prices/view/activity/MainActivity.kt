package com.crypto.prices.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityMainBinding
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.ui.explore.NewsFragment
import com.crypto.prices.view.ui.home.HomeFragment
import com.crypto.prices.view.ui.market.MarketFragment
import com.crypto.prices.view.ui.more.MoreFragment
import com.crypto.prices.view.ui.search.SearchActivity


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

        setDefaultCurrency()
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

    // set default currency in shared prefs
    private fun setDefaultCurrency() {
        val selectedCurrency = Utility.getCurrency(this)
        if (selectedCurrency == null) {
            // set default currency as usd
            Utility.setCurrency(this, "usd")
            Utility.setCurrencyName(this, "US Dollar")
            Utility.setCurrencySymbol(this, "$")
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
        activeFragment = newsFragment
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}