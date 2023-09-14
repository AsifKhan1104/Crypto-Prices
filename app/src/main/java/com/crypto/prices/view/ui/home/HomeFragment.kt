package com.crypto.prices.view.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.asf.cryptoprices.databinding.FragmentHomeBinding
import com.crypto.prices.data.offline.WatchlistRepo
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.view.activity.MainActivity
import com.crypto.prices.view.ui.explore.NewsAdapter
import com.crypto.prices.view.ui.explore.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val mHomeViewModel: HomeViewModel by viewModels()
    private val mNewsViewModel: NewsViewModel by viewModels()
    private val TAG = HomeFragment.javaClass.simpleName
    private lateinit var mDatabase: WatchlistRepo
    private var mWatchlistAdapter: HomeWatchlistAdapter? = null

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
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MyAnalytics.trackScreenViews("HomeFragment", requireActivity().javaClass.simpleName)
        mDatabase = WatchlistRepo(requireContext())

        return root
    }

    private fun initWatchlist() {
        // check if any coins/nfts are watchlisted
        val watchlist = mDatabase.getAllData()
        if (watchlist != null && watchlist.isNotEmpty()) {
            binding.groupWatchlist.visibility = View.VISIBLE
            if (mWatchlistAdapter == null) {
                mWatchlistAdapter = HomeWatchlistAdapter(context, watchlist)
                binding.recyclerViewWatchlist.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerViewWatchlist.adapter = mWatchlistAdapter
            }

            // update the latest prices of crypto coins
            mHomeViewModel.fetchUpdatedPricesApi(watchlist)
        } else {
            binding.groupWatchlist.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        initWatchlist()
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
            // observe watchlist data
            mHomeViewModel.watchListLiveData.observe(viewLifecycleOwner, Observer {
                it.let {
                    //bind the data to the ui
                    mWatchlistAdapter?.updateList(it)
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    private fun loadNewsData() {
        try {
            mNewsViewModel.newsLiveData.observe(viewLifecycleOwner, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let { newsData ->
                            //bind the data to the ui
                            onLoadingFinishedN()
                            binding.recyclerViewNews.layoutManager = LinearLayoutManager(context)
                            binding.recyclerViewNews.adapter =
                                NewsAdapter(context, newsData.articles.subList(0, 3))
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