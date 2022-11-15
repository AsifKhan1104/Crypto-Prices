package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.crypto.prices.CryptoApplication
import com.crypto.prices.databinding.ActivityCryptoDetailBinding
import com.crypto.prices.model.CryptoData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory

class CryptoDetailActivity : AppCompatActivity() {
    private var _binding: ActivityCryptoDetailBinding? = null
    private lateinit var mCryptoDetailViewModel: CryptoDetailViewModel
    private val TAG = "CryptoDetailActivity"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCryptoDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        var data = intent.extras?.getParcelable<CryptoData>("crypto_data")
        setUpViewModel(data)

        // set data
        setTitle(data?.name)
        // set coin icon
        Glide.with(this)
            .load(data?.image)
            .into(binding.imageViewSymbol)

        binding.textViewSymbol.text = data?.symbol
        binding.textViewPrice.text = "$" + String.format("%.8f", data?.current_price)
        binding.textView24hp.text =
            String.format("%.1f", data?.price_change_percentage_24h) + "%"
        binding.textViewMcr.text = data?.market_cap_rank?.toString()
        binding.textViewMc.text = data?.market_cap?.toString() + "$"
        binding.textViewFdmc.text = data?.fully_diluted_valuation?.toString() + "$"
        binding.textViewTotalVol.text = data?.total_volume.toString()
        binding.textViewMaxSupply.text = data?.max_supply?.toString()
        binding.textViewCircSupply.text = data?.circulating_supply?.toString()
        binding.textViewTotalSupply.text = data?.total_supply?.toString()
        binding.textView24h.text = data?.high_24h?.toString() + "$"
        binding.textView24l.text = data?.low_24h?.toString() + "$"
        binding.textViewAth.text = data?.ath?.toString() + "$"
        binding.textViewAtl.text = data?.atl?.toString() + "$"

    }

    private fun setUpViewModel(data: CryptoData?) {
        // create map of params needed for api
        val map: MutableMap<String, String> = HashMap()
        map["symbol"] = data?.id.toString()
        map["currency"] = "usd"
        val days = 14
        map["days"] = days.toString()

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mCryptoDetailViewModel =
            ViewModelProvider(this, factory).get(CryptoDetailViewModel::class.java)

        // load remote data
        loadData()
    }

    fun loadData() {
        try {
            mCryptoDetailViewModel.cryptoChartLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            /*onLoadingFinished()
                            binding.recyclerViewCrypto.layoutManager =
                                LinearLayoutManager(context)
                            binding.recyclerViewCrypto.adapter = CryptoAdapter(context, it)*/
                            Log.v("chatData", it.toString())
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        //onError(it.networkErrorMessage.toString())
                        Log.v("chatData", it.networkErrorMessage.toString())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}