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
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentCryptoBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.PaginationListener
import com.crypto.prices.view.PaginationListener.PAGE_START
import com.crypto.prices.view.ViewModelFactory

class CryptoFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCryptoBinding? = null
    private lateinit var mCryptoViewModel: CryptoViewModel
    private val TAG = CryptoFragment.javaClass.simpleName
    private var selectedMarketCap: String = "market_cap_desc"
    private lateinit var map: MutableMap<String, String>

    var currentPage: Int = PAGE_START
    var isLastPage = false
    var totalPage = 10
    var mIsLoading = false
    var itemCount = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.shimmerLayoutCrypto.visibility = View.GONE
        binding.shimmerLayoutCrypto.stopShimmer()
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.shimmerLayoutCrypto.visibility = View.VISIBLE
        binding.shimmerLayoutCrypto.startShimmer()
    }

    private fun onLoadingFinished() {
        binding.textViewError.visibility = View.GONE
        binding.shimmerLayoutCrypto.visibility = View.GONE
        binding.shimmerLayoutCrypto.stopShimmer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpViewModel()
        initData()
        return root
    }

    private fun initData() {
        binding.textViewPrice.text = "Price (" + Utility.getCurrencySymbol(requireActivity()) + ")"
        // on click listener
        binding.linearLayoutMC.setOnClickListener(this)
        /**
         * add scroll listener while user reach in bottom load more will call
         */
        binding.recyclerViewCrypto.addOnScrollListener(Listener(LinearLayoutManager(requireContext())))
    }

    inner class Listener(manager: LinearLayoutManager): PaginationListener(manager){
        override fun loadMoreItems() {
            mIsLoading = true
            currentPage++
            loadData()
        }

        override fun isLastPage(): Boolean {
            return isLastPage
        }

        override fun isLoading(): Boolean {
            return isLoading
        }

    }

    private fun setUpViewModel() {
        map = HashMap()
        Utility.getCurrency(requireActivity())?.let { map["vs_currency"] = it }
        map["order"] = selectedMarketCap
        map["per_page"] = "250"
        map["page"] = "1"
        /*map["sparkline"] = false*/

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
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
                            binding.recyclerViewCrypto.layoutManager =
                                LinearLayoutManager(context)
                            var adapter = CryptoAdapter(context, it.toMutableList())
                            binding.recyclerViewCrypto.adapter = adapter

                            /**
                             * manage progress view
                             */
                            if (currentPage != PAGE_START) adapter.removeLoading()
                            adapter.addItems(it.toMutableList());
                            //swipeRefresh.setRefreshing(false);
                            // check weather is last page or not
                            if (currentPage < totalPage) {
                                adapter.addLoading();
                            } else {
                                isLastPage = true;
                            }
                            mIsLoading = false;
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
            binding.linearLayoutMC.id -> {
                if (selectedMarketCap.equals("market_cap_desc", true)) {
                    selectedMarketCap = "market_cap_asc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up_24))
                    map["order"] = selectedMarketCap
                    mCryptoViewModel.getCrypto(map)
                } else {
                    selectedMarketCap = "market_cap_desc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down_24))
                    map["order"] = selectedMarketCap
                    mCryptoViewModel.getCrypto(map)
                }
            }
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