package com.crypto.prices.view.ui.more

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.crypto.prices.BuildConfig
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentMoreBinding
import com.crypto.prices.utils.CurrencyData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.SharedPrefsDataRetrieval
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.activity.PrivacyPolicyActivity
import com.crypto.prices.view.activity.TnCActivity
import com.crypto.prices.view.ui.explore.CurrConverterActivity
import com.google.gson.Gson

class MoreFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMoreBinding? = null
    private lateinit var mCurrencySelectViewModel: CurrencySelectViewModel
    private val TAG = MoreFragment.javaClass.simpleName

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpData()
    }

    private fun setUpViewModel() {
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, HashMap())
        mCurrencySelectViewModel =
            ViewModelProvider(this, factory).get(CurrencySelectViewModel::class.java)
    }

    private fun setUpData() {
        binding.textViewEnjoy.text = "Enjoy using ${getString(R.string.app_name)}?"
        binding.textViewRateUs.text = "Rate the ${getString(R.string.app_name)} App"
        binding.textViewShare.text = "Share the ${getString(R.string.app_name)} App"
        binding.textViewAppVersion.text =
            getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME
        binding.textViewCurrencySelected.text = Utility.getCurrencyName(requireActivity()) + " (" +
                Utility.getCurrencySymbol(requireActivity()) + ")"

        // on click listeners
        binding.relativeLayoutRateUs.setOnClickListener(this)
        binding.relativeLayoutShare.setOnClickListener(this)
        binding.relativeLayoutCS.setOnClickListener(this)
        binding.relativeLayoutCG.setOnClickListener(this)
        binding.relativeLayoutFI.setOnClickListener(this)
        binding.relativeLayoutGames.setOnClickListener(this)
        binding.relativeLayoutPP.setOnClickListener(this)
        binding.relativeLayoutTnC.setOnClickListener(this)
        binding.relativeLayoutCC.setOnClickListener(this)
        binding.relativeLayoutCalculator.setOnClickListener(this)
    }

    companion object {
        fun newInstance(): MoreFragment = MoreFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.relativeLayoutRateUs.id -> {
                Utility.openAppInPlayStore(requireContext())
            }
            binding.relativeLayoutShare.id -> {
                Utility.sendShareLink(requireContext())
            }
            binding.relativeLayoutCS.id -> {
                Utility.composeEmail(requireContext())
            }
            binding.relativeLayoutCG.id -> {
                Utility.openWebURL(requireContext(), "https://www.coingecko.com")
            }
            binding.relativeLayoutFI.id -> {
                Utility.openWebURL(requireContext(), "https://www.flaticon.com")
            }
            binding.relativeLayoutGames.id -> {
                Utility.openChromeCustomTabUrlNews(
                    requireContext(),
                    requireContext().getString(R.string.gamezop_url)
                )
            }
            binding.relativeLayoutPP.id -> {
                val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
                startActivity(intent)
            }
            binding.relativeLayoutTnC.id -> {
                val intent = Intent(requireContext(), TnCActivity::class.java)
                startActivity(intent)
            }
            binding.relativeLayoutCC.id -> {
                // get currency list from shared prefs
                var currencyList = SharedPrefsDataRetrieval().getData(requireActivity())
                if (currencyList == null) {
                    setUpViewModel()
                    loadData()
                } else {
                    // load currency list from shared prefs
                    // open bottom sheet dialog fragment
                    val fragment = CurrencySelectFragment()
                    fragment.show(requireActivity()?.supportFragmentManager, "")
                }
            }
            binding.relativeLayoutCalculator.id -> {
                val intent = Intent(requireContext(), CurrConverterActivity::class.java)
                startActivity(intent)
            }
            else -> {}
        }
    }

    fun loadData() {
        try {
            mCurrencySelectViewModel.exchangeRateLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            //onLoadingFinished()
                            val currencyList = CurrencyData().buildCurrencyList(it?.rates)
                            // now save this list in shared prefs
                            val sharedPref = requireActivity()?.getPreferences(MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString(
                                    "currencyList",
                                    Gson().toJson(currencyList)
                                )
                                apply()
                            }

                            // open bottom sheet dialog fragment
                            val fragment = CurrencySelectFragment()
                            fragment.show(requireActivity()?.supportFragmentManager, "")
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        //onError(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader, shimmer effect etc
                        //onLoading()
                    }
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }
}