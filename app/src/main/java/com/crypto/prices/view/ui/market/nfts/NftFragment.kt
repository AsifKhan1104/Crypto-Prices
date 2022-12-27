package com.crypto.prices.view.ui.market.nfts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentNftBinding
import com.crypto.prices.utils.Constants
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.TrailLoadStateAdapter
import com.crypto.prices.view.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NftFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentNftBinding? = null
    private lateinit var mViewModel: NftViewModel
    private val TAG = NftFragment.javaClass.simpleName
    private var selectedMarketCap: String = "market_cap_usd_desc"
    private lateinit var map: MutableMap<String, String>
    private lateinit var myAdapter: NftPagingAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.shimmerLayout.visibility = View.GONE
        binding.shimmerLayout.stopShimmer()
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()
    }

    private fun onLoadingFinished() {
        binding.textViewError.visibility = View.GONE
        binding.shimmerLayout.visibility = View.GONE
        binding.shimmerLayout.stopShimmer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNftBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MyAnalytics.trackScreenViews(javaClass.simpleName, requireActivity().javaClass.simpleName)
        setUpViewModel()
        initData()
        return root
    }

    private fun initData() {
        // on click listener
        //binding.linearLayoutMC.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        map = HashMap()
        /*Utility.getCurrency(requireActivity())?.let { map["vs_currency"] = it }*/
        map["order"] = selectedMarketCap
        map["per_page"] = Constants.itemsPerPage

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mViewModel = ViewModelProvider(this, factory).get(NftViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // observe internet connection
        if (!mViewModel.hasInternet) {
            onError(getString(R.string.no_internet_msg))
            return
        }

        myAdapter = NftPagingAdapter(requireContext())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = myAdapter

            //bind the LoadStateAdapter with the movieAdapter
            adapter = myAdapter.withLoadStateFooter(
                footer = TrailLoadStateAdapter { myAdapter.retry() }
            )
        }

        requireActivity().lifecycleScope.launch {
            mViewModel.nftList.collectLatest {
                myAdapter.submitData(it)
            }
        }

        myAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                onLoading()
            } else {
                onLoadingFinished()

                // getting the error
                val error = when {
                    loadState.prepend is LoadState.Error -> {
                        loadState.prepend as LoadState.Error
                        onError(getString(R.string.error_msg))
                    }
                    loadState.append is LoadState.Error -> {
                        loadState.append as LoadState.Error

                    }
                    loadState.refresh is LoadState.Error -> {
                        loadState.refresh as LoadState.Error
                        onError(getString(R.string.error_msg))
                    }

                    else -> null
                }
            }
        }
    }

    companion object {
        fun newInstance(): NftFragment = NftFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /*binding.linearLayoutMC.id -> {
                if (selectedMarketCap.equals("market_cap_desc", true)) {
                    selectedMarketCap = "market_cap_asc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up_24))
                    map["order"] = selectedMarketCap
                    //mViewModel.getCrypto(map)
                    myAdapter.refresh()
                } else {
                    selectedMarketCap = "market_cap_desc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down_24))
                    map["order"] = selectedMarketCap
                    //mViewModel.getCrypto(map)
                    myAdapter.refresh()
                }
            }*/
            else -> {
            }
        }
    }
}