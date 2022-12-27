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
import com.crypto.prices.database.Watchlist
import com.crypto.prices.database.WatchlistRepo
import com.crypto.prices.databinding.FragmentHomeBinding
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.activity.MainActivity
import com.crypto.prices.view.ui.explore.NewsAdapter
import com.crypto.prices.view.ui.explore.NewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mNewsViewModel: NewsViewModel
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
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        MyAnalytics.trackScreenViews(javaClass.simpleName, requireActivity().javaClass.simpleName)
        mDatabase = WatchlistRepo(requireContext())
        //initWatchlist()
        setUpViewModel()
        return root
    }

    private fun initWatchlist() {
        // check if any coins/nfts are watchlisted
        val watchlist = mDatabase.getAllData()
        if (watchlist != null && watchlist.size != 0) {
            binding.groupWatchlist.visibility = View.VISIBLE
            if (mWatchlistAdapter == null) {
                mWatchlistAdapter = HomeWatchlistAdapter(context, watchlist)
                binding.recyclerViewWatchlist.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerViewWatchlist.adapter = mWatchlistAdapter

                // update the latest prices of crypto coins
                getUpdatedPricesApi(watchlist)
            } else {
                // update the latest prices of crypto coins
                getUpdatedPricesApi(watchlist)
            }
        } else {
            binding.groupWatchlist.visibility = View.GONE
        }
    }

    private fun getUpdatedPricesApi(watchlist: List<Watchlist>) {
        val cryptoIds = StringBuilder()
        for (i in watchlist) {
            if (!i.type.equals("") && i.type.equals("crypto"))
                cryptoIds.append(i.id + ",")
        }

        // now request these id's data
        val map = HashMap<String, String?>()
        map["vs_currency"] = Utility.getCurrencyGlobal(requireContext())
        map["order"] = "market_cap_desc"
        //map["per_page"] = "20"
        map["ids"] = cryptoIds.toString()

        GlobalScope.launch {
            val appRepository: AppRepository = AppRepositoryImpl()
            val response = appRepository.getCryptoPrices(map as MutableMap<String, String>)
            // check if request is successful
            if (response.isSuccessful) {
                val cryptoList = response.body()!!
                // set updated price & price_change_24%
                for ((i, value1) in cryptoList.withIndex()) {
                    for ((j, value2) in watchlist.withIndex()) {
                        if (value1.id.equals(value2.id)) {
                            watchlist[j].price = cryptoList[i].current_price.toString()
                            watchlist[j].priceChange24h =
                                cryptoList[i].price_change_percentage_24h.toString()
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    // update recycler view
                    mWatchlistAdapter?.updateList(watchlist)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initWatchlist()
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