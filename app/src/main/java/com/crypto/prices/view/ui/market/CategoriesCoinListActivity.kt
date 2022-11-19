package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentCryptoBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory

class CategoriesCoinListActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: FragmentCryptoBinding? = null
    private lateinit var mCryptoViewModel: CryptoViewModel
    private val TAG = CryptoFragment.javaClass.simpleName
    private var selectedMarketCap: String = "market_cap_desc"
    private var selectedCategory: String = ""
    private lateinit var map: MutableMap<String, String>

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

    private fun initData() {
        // on click listener
        binding.linearLayoutMC.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        map = HashMap()
        map["vs_currency"] = "usd"
        map["order"] = selectedMarketCap
        map["per_page"] = "250"
        map["page"] = "1"
        map["category"] = selectedCategory
        /*map["sparkline"] = false*/

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mCryptoViewModel = ViewModelProvider(this, factory).get(CryptoViewModel::class.java)

        // observe data
        loadData()
    }

    fun loadData() {
        try {
            mCryptoViewModel.cryptoLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            binding.recyclerViewCrypto.layoutManager =
                                LinearLayoutManager(this)
                            binding.recyclerViewCrypto.adapter = CryptoAdapter(this, it)
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
        fun newInstance(): CategoriesCoinListActivity = CategoriesCoinListActivity()
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
}