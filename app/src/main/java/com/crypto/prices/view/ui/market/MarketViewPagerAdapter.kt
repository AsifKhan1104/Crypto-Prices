package com.crypto.prices.view.ui.market

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crypto.prices.view.ui.market.categories.CategoriesFragment
import com.crypto.prices.view.ui.market.crypto.CryptoFragment

class MarketViewPagerAdapter(fm: FragmentManager, lf: Lifecycle) : FragmentStateAdapter(fm, lf) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CryptoFragment.newInstance()
        1 -> CategoriesFragment.newInstance()
        2 -> NftFragment.newInstance()
        else -> CryptoFragment.newInstance()
    }
}