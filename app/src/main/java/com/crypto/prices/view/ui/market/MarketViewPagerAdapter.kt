package com.crypto.prices.view.ui.market

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crypto.prices.view.ui.market.categories.CategoriesFragment
import com.crypto.prices.view.ui.market.crypto.CryptoFragment
import com.crypto.prices.view.ui.market.derivatives.DerivativesFragment
import com.crypto.prices.view.ui.market.exchanges.ExchangesFragment
import com.crypto.prices.view.ui.market.nfts.NftFragment

class MarketViewPagerAdapter(fm: FragmentManager, lf: Lifecycle) : FragmentStateAdapter(fm, lf) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CryptoFragment.newInstance()
        1 -> CategoriesFragment.newInstance()
        2 -> NftFragment.newInstance()
        3 -> ExchangesFragment.newInstance()
        4 -> DerivativesFragment.newInstance()
        else -> CryptoFragment.newInstance()
    }
}