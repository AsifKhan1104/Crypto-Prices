package com.crypto.prices.view.ui.search

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.cryptoprices.databinding.SearchViewBinding
import com.crypto.prices.model.SearchData
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: SearchViewBinding? = null
    private val mViewModel: SearchViewModel by viewModels()
    private val TAG = "SearchActivity"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mAdapterCoins: SearchCryptoAdapter

    @Inject
    lateinit var mAdapterExchanges: SearchExchangesAdapter

    @Inject
    lateinit var mAdapterNfts: SearchNftsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SearchViewBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.hide()
        setUpListeners()
        setUpRecyclerView()
        setUpViewModel()
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewCoins.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCoins.adapter = mAdapterCoins

        binding.recyclerViewNfts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNfts.adapter = mAdapterNfts

        binding.recyclerViewExchanges.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExchanges.adapter = mAdapterExchanges
    }

    private fun setUpListeners() {
        binding.searchView.requestFocusFromTouch()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.fetchData(query!!)
                MyAnalytics.trackSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    mViewModel.fetchData(it)
                }
                return true
            }
        }
        )
    }

    private fun setUpViewModel() {
        // observe data
        observeData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
        //binding.searchViewParent.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        //binding.searchViewParent.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.searchViewParent.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    private fun observeData() {
        try {
            lifecycleScope.launch {
                mViewModel.getSearchResults().collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.networkData?.let {
                                //bind the data to the ui
                                onLoadingFinished()
                                setDataInView(it)
                            }
                        }
                        is NetworkResult.Error -> {
                            //show error message
                            onError(it.networkErrorMessage.toString())
                        }

                        is NetworkResult.Loading -> {
                            //show loader
                            onLoading()
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    // set data in all recycler views
    private fun setDataInView(it: SearchData) {
        binding.textViewCoins.visibility = View.VISIBLE
        binding.textViewNfts.visibility = View.VISIBLE
        binding.textViewExchanges.visibility = View.VISIBLE

        // set coins
        val coinList = it?.coins
        if (coinList.isEmpty()) {
            binding.textViewNoDataCoins.visibility = View.VISIBLE
            binding.recyclerViewCoins.visibility = View.GONE
        } else {
            binding.textViewNoDataCoins.visibility = View.GONE
            binding.recyclerViewCoins.visibility = View.VISIBLE
            mAdapterCoins.updateList(if (coinList.size > 4) coinList.subList(0, 4) else coinList)
        }

        // set nfts
        val nftList = it?.nfts
        if (nftList.isEmpty()) {
            binding.textViewNoDataNfts.visibility = View.VISIBLE
            binding.recyclerViewNfts.visibility = View.GONE
        } else {
            binding.textViewNoDataNfts.visibility = View.GONE
            binding.recyclerViewNfts.visibility = View.VISIBLE
            mAdapterNfts.updateList(if (nftList.size > 4) nftList.subList(0, 4) else nftList)
        }

        binding.recyclerViewExchanges.layoutManager = LinearLayoutManager(this)
        // set exchanges
        val exchangeList = it?.exchanges
        if (exchangeList.isEmpty()) {
            binding.textViewNoDataExchanges.visibility = View.VISIBLE
            binding.recyclerViewExchanges.visibility = View.GONE
        } else {
            binding.textViewNoDataExchanges.visibility = View.GONE
            binding.recyclerViewExchanges.visibility = View.VISIBLE
            mAdapterExchanges.updateList(
                if (exchangeList.size > 4) exchangeList.subList(
                    0,
                    4
                ) else exchangeList
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }
}