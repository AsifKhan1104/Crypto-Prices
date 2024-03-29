package com.crypto.prices.view.ui.market.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.FragmentCategoriesBinding
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.ui.market.crypto.CryptoFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCategoriesBinding? = null
    private val mCategoriesViewModel: CategoriesViewModel by viewModels()
    private val TAG = CryptoFragment.javaClass.simpleName
    private var selectedMarketCap: String = "market_cap_desc"
    private lateinit var map: MutableMap<String, String>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mCategoriesAdapter: CategoriesAdapter

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MyAnalytics.trackScreenViews("CategoriesFragment", requireActivity().javaClass.simpleName)
        setUpViewModel()
        initData()
        return root
    }

    private fun initData() {
        // on click listener
        binding.linearLayoutMC.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        map = HashMap()
        map["order"] = selectedMarketCap

        mCategoriesViewModel.fetchDataViaApi(map)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        try {
            mCategoriesViewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer { result ->
                // blank observe here
                when (result) {
                    is NetworkResult.Success -> {
                        result.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(context)
                            binding.recyclerView.adapter = mCategoriesAdapter
                            mCategoriesAdapter.updateList(it)
                        }
                    }

                    is NetworkResult.Error -> {
                        //show error message
                        onError(result.networkErrorMessage.toString())
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
        fun newInstance(): CategoriesFragment = CategoriesFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.linearLayoutMC.id -> {
                if (selectedMarketCap.equals("market_cap_desc", true)) {
                    selectedMarketCap = "market_cap_asc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up_24))
                    map["order"] = selectedMarketCap
                    mCategoriesViewModel.fetchDataViaApi(map)
                } else {
                    selectedMarketCap = "market_cap_desc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down_24))
                    map["order"] = selectedMarketCap
                    mCategoriesViewModel.fetchDataViaApi(map)
                }
            }

            else -> {
            }
        }
    }
}