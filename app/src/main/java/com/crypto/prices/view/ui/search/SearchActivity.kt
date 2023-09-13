package com.crypto.prices.view.ui.search

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.cryptoprices.databinding.SearchViewBinding
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.SearchData
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: SearchViewBinding? = null
    private lateinit var mViewModel: SearchViewModel
    private val TAG = "SearchActivity"

    private lateinit var mAdapterCoins: SearchCryptoAdapter
    private lateinit var mAdapterNfts: SearchNftsAdapter
    private lateinit var mAdapterExchanges: SearchExchangesAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        mAdapterCoins = SearchCryptoAdapter(this, ArrayList())
        binding.recyclerViewCoins.adapter = mAdapterCoins

        binding.recyclerViewNfts.layoutManager = LinearLayoutManager(this)
        mAdapterNfts = SearchNftsAdapter(this, ArrayList())
        binding.recyclerViewNfts.adapter = mAdapterNfts

        binding.recyclerViewExchanges.layoutManager = LinearLayoutManager(this)
        mAdapterExchanges = SearchExchangesAdapter(this, ArrayList())
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
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, HashMap())
        mViewModel =
            ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        // load remote data
        loadData()
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

    fun loadData() {
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
        if (coinList.size == 0) {
            binding.textViewNoDataCoins.visibility = View.VISIBLE
            binding.recyclerViewCoins.visibility = View.GONE
        } else {
            binding.textViewNoDataCoins.visibility = View.GONE
            binding.recyclerViewCoins.visibility = View.VISIBLE
            mAdapterCoins.data = if (coinList.size > 4) coinList.subList(0, 4) else coinList
            mAdapterCoins.notifyDataSetChanged()
        }

        // set nfts
        val nftList = it?.nfts
        if (nftList.size == 0) {
            binding.textViewNoDataNfts.visibility = View.VISIBLE
            binding.recyclerViewNfts.visibility = View.GONE
        } else {
            binding.textViewNoDataNfts.visibility = View.GONE
            binding.recyclerViewNfts.visibility = View.VISIBLE
            mAdapterNfts.data = if (nftList.size > 4) nftList.subList(0, 4) else nftList
            mAdapterNfts.notifyDataSetChanged()
        }

        binding.recyclerViewExchanges.layoutManager = LinearLayoutManager(this)
        // set exchanges
        val exchangeList = it?.exchanges
        if (exchangeList.size == 0) {
            binding.textViewNoDataExchanges.visibility = View.VISIBLE
            binding.recyclerViewExchanges.visibility = View.GONE
        } else {
            binding.textViewNoDataExchanges.visibility = View.GONE
            binding.recyclerViewExchanges.visibility = View.VISIBLE
            mAdapterExchanges.data =
                if (exchangeList.size > 4) exchangeList.subList(0, 4) else exchangeList
            mAdapterExchanges.notifyDataSetChanged()
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