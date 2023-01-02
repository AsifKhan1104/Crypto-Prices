package com.crypto.prices.view.ui.market.exchanges

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
import com.crypto.prices.databinding.FragmentExchangesBinding
import com.crypto.prices.utils.Constants
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.TrailLoadStateAdapter
import com.crypto.prices.view.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExchangesFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentExchangesBinding? = null
    private lateinit var mViewModel: ExchangesViewModel
    private val TAG = ExchangesFragment.javaClass.simpleName
    private lateinit var map: MutableMap<String, String>
    private lateinit var myAdapter: ExchangesPagingAdapter

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
        _binding = FragmentExchangesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MyAnalytics.trackScreenViews("ExchangesFragment", requireActivity().javaClass.simpleName)
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
        map["per_page"] = Constants.itemsPerPage

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mViewModel = ViewModelProvider(this, factory).get(ExchangesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // observe internet connection
        if (!mViewModel.hasInternet) {
            onError(getString(R.string.no_internet_msg))
            return
        }

        myAdapter = ExchangesPagingAdapter(requireContext())
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
            mViewModel.exchangesList.collectLatest {
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
        fun newInstance(): ExchangesFragment = ExchangesFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            else -> {
            }
        }
    }
}