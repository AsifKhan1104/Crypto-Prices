package com.crypto.prices.view.ui.market.nfts.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityNftDetailBinding
import com.crypto.prices.model.NftDetailData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.ui.search.SearchActivity
import java.math.BigDecimal

class NftDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityNftDetailBinding? = null
    private lateinit var mViewModel: NftDetailViewModel
    private val TAG = "NftDetailActivity"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNftDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        val id = intent?.getStringExtra("id")
        setUpViewModel(id)
    }

    private fun setMarketStatsData(detailData: NftDetailData?) {
        // set title
        setTitle(detailData?.name)
        // set coin icon
        Glide.with(this)
            .load(detailData?.image?.small)
            .into(binding.imageViewSymbol)

        binding.textViewName.text = detailData?.name
        binding.textViewPrice.text = "$" + detailData?.floor_price?.usd?.toString()
        binding.textViewAssetPlat.text = detailData?.asset_platform_id
        binding.textViewContractAddress.text = detailData?.contract_address
        binding.textViewMc.text = "$" + detailData?.market_cap?.usd?.toString()
        binding.textViewVolume24h.text = "$" + detailData?.volume_24h?.usd?.toString()
        binding.textViewTotalSupply.text = detailData?.total_supply?.toString()
        binding.textViewUniqueAddress.text = detailData?.number_of_unique_addresses?.toString()

        // set arrow %
        var priceChangePerc: String? =
            detailData?.floor_price_in_usd_24h_percentage_change?.toString()
        var uniqueAddrChangePerc: String? =
            detailData?.number_of_unique_addresses_24h_percentage_change?.toString()
        var priceArrow = R.drawable.ic_arrow_up_24
        if (detailData?.floor_price_in_usd_24h_percentage_change!!.compareTo(BigDecimal.ZERO) < 0) {
            priceArrow = R.drawable.ic_arrow_down_24
            priceChangePerc = detailData?.floor_price_in_usd_24h_percentage_change.toString()
                .substring(
                    1,
                    detailData?.floor_price_in_usd_24h_percentage_change?.toString()!!.length
                )
        }
        binding.textViewFloorPrice24hPerc.setCompoundDrawablesWithIntrinsicBounds(
            priceArrow,
            0,
            0,
            0
        )
        binding.textViewFloorPrice24hPerc.text = priceChangePerc

        var uniqueAddressArrow = R.drawable.ic_arrow_up_24
        if (detailData?.number_of_unique_addresses_24h_percentage_change!!.compareTo(BigDecimal.ZERO) < 0) {
            uniqueAddressArrow = R.drawable.ic_arrow_down_24
            uniqueAddrChangePerc =
                detailData?.number_of_unique_addresses_24h_percentage_change.toString()
                    .substring(
                        1,
                        detailData?.number_of_unique_addresses_24h_percentage_change?.toString()!!.length
                    )
        }
        binding.textViewUniqueAddressPerc.setCompoundDrawablesWithIntrinsicBounds(
            uniqueAddressArrow,
            0,
            0,
            0
        )
        binding.textViewUniqueAddressPerc.text = uniqueAddrChangePerc

        binding.textViewAbout.text = detailData?.description
    }

    private fun setUpViewModel(id: String?) {
        // create map of params needed for api
        val map: MutableMap<String, String> = HashMap()
        map["id"] = id.toString()

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mViewModel =
            ViewModelProvider(this, factory).get(NftDetailViewModel::class.java)

        // load remote data
        loadData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
        binding.scrollView.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.scrollView.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.scrollView.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    fun loadData() {
        try {
            mViewModel.nftDetailLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setMarketStatsData(it)
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
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}