package com.crypto.prices.view.ui.market.derivatives.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityDerivativesDetailBinding
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.model.DerivativesDetailData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.ui.search.SearchActivity

class DerivativesDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityDerivativesDetailBinding? = null
    private lateinit var mDetailViewModel: DerivativesDetailViewModel
    private val TAG = "DerivativesDetail"

    private var data: DerivativesData? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDerivativesDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        data = intent?.extras?.getParcelable<DerivativesData>("derivatives_data")!!
        setUpViewModel()
        setInfoData()
    }

    private fun setInfoData() {
        // set title
        setTitle(data?.name)

        // set coin icon
        /*Glide.with(this)
            .load(data?.image)
            .into(binding.imageViewSymbol)*/

        binding.textViewHomepage.text = data?.url
        binding.textViewYear.text = data?.year_established?.toString()
        binding.textViewFuturePairs.text = data?.number_of_futures_pairs?.toString()
        binding.textViewPerpPairs.text = data?.number_of_perpetual_pairs?.toString()
    }

    private fun setUpViewModel() {
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, HashMap())
        mDetailViewModel =
            ViewModelProvider(this, factory).get(DerivativesDetailViewModel::class.java)

        // load remote data
        loadData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    fun loadData() {
        try {
            mDetailViewModel.derivativesDetailLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setPerpetualData(it?.filter { it?.market?.equals(data?.name) })
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

    private fun setPerpetualData(derivativesList: List<DerivativesDetailData>) {
        // show list of perpetuals
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = DerivativesDetailAdapter(context, derivativesList)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            else -> {}
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