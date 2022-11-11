package com.crypto.prices.view.activity.ui.market

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MarketViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> CryptoFragment.newInstance()
        //1 -> FragmentWithdraw.newInstance()
        //2 -> FragmentRefer.newInstance()
        //3 -> FragmentLottery.newInstance()
        else -> CryptoFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Cryptocurrency"
        //1 -> "Withdraw"
        //2 -> "Refer & Spin"
        //3 -> "Lottery"
        else -> "Cryptocurrency"
    }

    override fun getCount(): Int = 1
}