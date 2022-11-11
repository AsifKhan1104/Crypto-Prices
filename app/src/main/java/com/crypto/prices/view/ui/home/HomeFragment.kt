package com.crypto.prices.view.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentHomeBinding
import com.crypto.prices.utils.CoinManagement
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.ui.market.MarketViewPagerAdapter

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("HomeFragent", "OnCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HomeFragent", "OnViewCreated")
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
                    0 -> Utility.logAnalyitcsEvent("Earn")
                    1 -> Utility.logAnalyitcsEvent("Withdraw")
                    2 -> Utility.logAnalyitcsEvent("Refer")
                    //3 -> logScreenEvent("Market")
                    else -> Utility.logAnalyitcsEvent("Earn")
                }
            }
        })
        showRewardDialogs()

        // set default values from remote config
        val remoteConfig = Firebase.remoteConfig
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
            }
    }

    private fun showRewardDialogs() {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("HomeFragent", "OnDestroyView")
        _binding = null
    }
}