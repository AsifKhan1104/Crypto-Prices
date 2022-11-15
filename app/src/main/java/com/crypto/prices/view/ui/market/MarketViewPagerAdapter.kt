package com.crypto.prices.view.ui.market

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MarketViewPagerAdapter(fm: FragmentManager, lf: Lifecycle) : FragmentStateAdapter(fm, lf) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CryptoFragment.newInstance()
        1 -> CryptoFragment.newInstance()
        else -> CryptoFragment.newInstance()
    }
}