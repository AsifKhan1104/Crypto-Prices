package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.CryptoApplication
import com.crypto.prices.databinding.FragmentCryptoBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.adapter.MarketAdapter

class CryptoFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCryptoBinding? = null
    private lateinit var mCryptoViewModel: CryptoViewModel
    private val TAG = CryptoFragment.javaClass.simpleName

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpViewModel()
        return root
    }

    private fun setUpViewModel() {
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository)
        mCryptoViewModel = ViewModelProvider(this, factory).get(CryptoViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    fun loadData() {
        try {
            mCryptoViewModel.cryptoLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            binding.recyclerViewListings.layoutManager = LinearLayoutManager(context)
                            binding.recyclerViewListings.adapter = MarketAdapter(context, it.data)
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        onError(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader, shimmer effect etc
                        onLoading()
                    }
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    companion object {
        fun newInstance(): CryptoFragment = CryptoFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            else -> {
            }
        }
    }

    /*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Market")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/
}