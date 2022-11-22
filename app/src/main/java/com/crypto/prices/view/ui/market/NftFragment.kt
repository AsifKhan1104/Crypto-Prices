package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.databinding.FragmentNftBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory

class NftFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentNftBinding? = null
    private lateinit var mViewModel: NftViewModel
    private val TAG = NftFragment.javaClass.simpleName
    private var selectedMarketCap: String = "market_cap_usd_desc"

    //private var selectedMarketCap: String = "market_cap_desc"
    private lateinit var map: MutableMap<String, String>

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
        _binding = FragmentNftBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpViewModel()
        initData()
        return root
    }

    private fun initData() {
        //binding.textViewPrice.text = "Price (" + Utility.getCurrencySymbol(requireActivity()) + ")"
        // on click listener
        //binding.linearLayoutMC.setOnClickListener(this)
    }

    private fun setUpViewModel() {
        map = HashMap()
        /*Utility.getCurrency(requireActivity())?.let { map["vs_currency"] = it }*/
        map["order"] = selectedMarketCap
        map["per_page"] = "250"
        /*map["page"] = "1"
        map["sparkline"] = "false"*/

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mViewModel = ViewModelProvider(this, factory).get(NftViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    fun loadData() {
        try {
            mViewModel.nftLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(context)
                            binding.recyclerView.adapter = NftAdapter(context, it)
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
        fun newInstance(): NftFragment = NftFragment()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /*binding.linearLayoutMC.id -> {
                if (selectedMarketCap.equals("market_cap_desc", true)) {
                    selectedMarketCap = "market_cap_asc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up_24))
                    map["order"] = selectedMarketCap
                    mViewModel.getCrypto(map)
                } else {
                    selectedMarketCap = "market_cap_desc"
                    binding.imageViewMcArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down_24))
                    map["order"] = selectedMarketCap
                    mViewModel.getCrypto(map)
                }
            }*/
            else -> {
            }
        }
    }
}