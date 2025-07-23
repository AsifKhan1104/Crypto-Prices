package com.crypto.prices.view.ui.market.derivatives.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ActivityDerivativesDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.model.DerivativesDetailData
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DerivativesDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityDerivativesDetailBinding? = null
    private val mDetailViewModel: DerivativesDetailViewModel by viewModels()
    private val TAG = "DerivativesDetail"

    private var data: DerivativesData? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mDerivativesDetailAdapter: DerivativesDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDerivativesDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        MyAnalytics.trackScreenViews("DerivativesDetailActivity", javaClass.simpleName)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // get data from intent
        data = intent?.extras?.getParcelable<DerivativesData>("derivatives_data")!!
        // set icon on action bar
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        Glide.with(this).asDrawable().apply(RequestOptions.circleCropTransform()).load(data?.image)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    @Nullable transition: Transition<in Drawable?>?
                ) {
                    supportActionBar?.setLogo(resource)
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })

        setUpViewModel()
        setInfoData()
    }

    private fun setInfoData() {
        // set title
        setTitle("  " + data?.name)

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
        // observe remote data
        observeData()
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

    fun observeData() {
        try {
            mDetailViewModel.derivativesDetailLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setPerpetualData(it.filter { it.market.equals(data?.name) })
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
        if (derivativesList != null && derivativesList.size > 0) {
            // show list of perpetuals
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mDerivativesDetailAdapter
                mDerivativesDetailAdapter.updateList(derivativesList)
            }
        } else {
            onError("No Data Found !!")
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