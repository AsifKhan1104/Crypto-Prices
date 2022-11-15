package com.crypto.prices.view.ui.home

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
import com.crypto.prices.databinding.FragmentHomeBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.activity.MainActivity
import com.crypto.prices.view.ui.explore.NewsAdapter
import com.crypto.prices.view.ui.explore.NewsViewModel

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mNewsViewModel: NewsViewModel
    private val TAG = HomeFragment.javaClass.simpleName

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun onErrorT(s: String) {
        binding.textViewErrorT.text = s
        binding.textViewErrorT.visibility = View.VISIBLE
        binding.shimmerLayoutTrending.visibility = View.GONE
        binding.shimmerLayoutTrending.stopShimmer()
    }

    private fun onLoadingT() {
        binding.textViewErrorT.visibility = View.GONE
        binding.shimmerLayoutTrending.visibility = View.VISIBLE
        binding.shimmerLayoutTrending.startShimmer()
    }

    private fun onLoadingFinishedT() {
        binding.textViewErrorT.visibility = View.GONE
        binding.shimmerLayoutTrending.visibility = View.GONE
        binding.shimmerLayoutTrending.stopShimmer()
    }

    private fun onErrorN(s: String) {
        binding.textViewErrorN.text = s
        binding.textViewErrorN.visibility = View.VISIBLE
        binding.shimmerLayoutNews.visibility = View.GONE
        binding.shimmerLayoutNews.stopShimmer()
    }

    private fun onLoadingN() {
        binding.textViewErrorN.visibility = View.GONE
        binding.shimmerLayoutNews.visibility = View.VISIBLE
        binding.shimmerLayoutNews.startShimmer()
    }

    private fun onLoadingFinishedN() {
        binding.textViewErrorN.visibility = View.GONE
        binding.shimmerLayoutNews.visibility = View.GONE
        binding.shimmerLayoutNews.stopShimmer()
        binding.textViewNewsMore.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpViewModel()
        return root
    }

    private fun setUpViewModel() {
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, HashMap())
        mHomeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        mNewsViewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewNewsMore.setOnClickListener(this)
        loadData()
        loadNewsData()
    }

    fun loadData() {
        try {
            mHomeViewModel.trendingLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinishedT()
                            binding.recyclerViewTrending.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            binding.recyclerViewTrending.adapter =
                                HomeTrendingAdapter(context, it.coins)
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        onErrorT(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader, shimmer effect etc
                        onLoadingT()
                    }
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    fun loadNewsData() {
        try {
            mNewsViewModel.newsLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinishedN()
                            binding.recyclerViewNews.layoutManager = LinearLayoutManager(context)
                            binding.recyclerViewNews.adapter =
                                NewsAdapter(context, it.articles.subList(0, 3))
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        onErrorN(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader, shimmer effect etc
                        onLoadingN()
                    }
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.textViewNewsMore.id -> (activity as MainActivity?)?.clickExploreMenu()
            else -> {}
        }

    }

}