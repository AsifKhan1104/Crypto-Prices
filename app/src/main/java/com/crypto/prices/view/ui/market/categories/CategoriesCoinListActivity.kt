package com.crypto.prices.view.ui.market.categories

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentCryptoBinding
import com.crypto.prices.utils.Constants
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.TrailLoadStateAdapter
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.ui.market.crypto.CryptoPagingAdapter
import com.crypto.prices.view.ui.market.crypto.CryptoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoriesCoinListActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: FragmentCryptoBinding? = null
    private lateinit var mCryptoViewModel: CryptoViewModel
    private val TAG = "CategoriesCoinListActivity"
    private var selectedMarketCap: String = "market_cap_desc"
    private var selectedCategory: String = ""
    private lateinit var map: MutableMap<String, String>
    private lateinit var myAdapter: CryptoPagingAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentCryptoBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        // get data from intent
        selectedCategory = intent?.getStringExtra("categoryId")!!
        var categoryName = intent?.getStringExtra("categoryName")!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(categoryName)
        // get data from intent
        setUpViewModel()
        initData()
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

    private fun initData() {
        binding.textViewPrice.text = "Price (" + Utility.getCurrencySymbol(this) + ")"
        // on click listener
        binding.linearLayoutMC.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        map = HashMap()
        map = HashMap()
        Utility.getCurrency(this)?.let { map["vs_currency"] = it }
        map["order"] = selectedMarketCap
        map["per_page"] = Constants.itemsPerPage
        map["category"] = selectedCategory

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mCryptoViewModel = ViewModelProvider(this, factory).get(CryptoViewModel::class.java)

        loadData()
    }

    fun loadData() {
        // observe internet connection
        if (!mCryptoViewModel.hasInternet) {
            onError(getString(R.string.no_internet_msg))
            return
        }

        myAdapter = CryptoPagingAdapter(this)
        binding.recyclerViewCrypto.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = myAdapter

            //bind the LoadStateAdapter with the movieAdapter
            adapter = myAdapter.withLoadStateFooter(
                footer = TrailLoadStateAdapter { myAdapter.retry() }
            )
        }

        lifecycleScope.launch {
            mCryptoViewModel.cryptoList.collectLatest {
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

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.linearLayoutMC.id -> {
                if (selectedMarketCap.equals("market_cap_desc", true)) {
                    selectedMarketCap = "market_cap_asc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up_24))
                    map["order"] = selectedMarketCap
                    //mCryptoViewModel.getCrypto(map)
                    myAdapter.refresh()
                } else {
                    selectedMarketCap = "market_cap_desc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down_24))
                    map["order"] = selectedMarketCap
                    //mCryptoViewModel.getCrypto(map)
                    myAdapter.refresh()
                }
            }
            else -> {
            }
        }
    }
}