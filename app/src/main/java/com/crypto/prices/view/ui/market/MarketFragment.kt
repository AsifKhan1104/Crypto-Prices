package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.crypto.prices.databinding.FragmentMarketBinding
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.ui.explore.NewsFragment
import com.google.android.material.tabs.TabLayout

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val TAG = "MarketFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "OnCreateView")
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpAdapter()
        return root
    }

    fun setUpAdapter() {
        val adapter = MarketViewPagerAdapter(requireActivity().supportFragmentManager)

        //view_pager.offscreenPageLimit = 3
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (binding.viewPager.currentItem) {
                    0 -> Utility.logAnalyitcsEvent("Crypto")
                    //1 -> Utility.logAnalyitcsEvent("Withdraw")
                    //2 -> Utility.logAnalyitcsEvent("Refer")
                    //3 -> logScreenEvent("Market")
                    else -> Utility.logAnalyitcsEvent("Crypto")
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "OnViewCreated")
        // showRewardDialogs()

        // set default values from remote config
        /*val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("MainActivity", "Config params updated: $updated")

                    val sharedPref = requireActivity()?.getPreferences(Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString(
                            getString(R.string.min_withdraw_coins),
                            remoteConfig.getDouble("minWithdrawCoins").toString()
                        )
                        apply()
                    }
                }
            }*/
    }

    /*private fun showRewardDialogs() {
        val referral = requireActivity().intent?.getBooleanExtra("referralUser", false)
        val newUser = requireActivity().intent?.getBooleanExtra("newUser", false)

        if (referral!! && newUser!!) {
            Utility.showDialog(
                requireActivity(),
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
                requireActivity(),
                getString(R.string.congrats_title),
                getString(
                    R.string.congrats_msg_invitee,
                    CoinManagement.getReferralCoins().toString()
                )
            )
        } else if (newUser!!) {
            Utility.showDialog(
                requireActivity(),
                getString(R.string.congrats_title),
                getString(
                    R.string.congrats_msg_user,
                    CoinManagement.getSignUpCoins().toString()
                )
            )
        }
    }*/

    companion object {
        fun newInstance(): MarketFragment = MarketFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "OnDestroyView")
        _binding = null
    }
}