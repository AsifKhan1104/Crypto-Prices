package com.crypto.prices.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.crypto.prices.view.fragment.FragmentEarn
import com.crypto.prices.view.fragment.FragmentRefer
import com.crypto.prices.view.fragment.FragmentWithdraw

class MyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> FragmentEarn.newInstance()
        1 -> FragmentWithdraw.newInstance()
        2 -> FragmentRefer.newInstance()
        //3 -> FragmentLottery.newInstance()
        else -> FragmentEarn.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Earn"
        1 -> "Withdraw"
        2 -> "Refer & Spin"
        //3 -> "Lottery"
        else -> "Earn"
    }

    override fun getCount(): Int = 3
}