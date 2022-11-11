package com.crypto.prices.view.activity.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.R
import com.crypto.prices.databinding.FragmentCryptoBinding
import com.crypto.prices.databinding.FragmentMarketBinding
import com.crypto.prices.remote.Service
import com.crypto.prices.view.adapter.MarketAdapter
import kotlinx.coroutines.*

class CryptoFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCryptoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val service = Service().getUsersService()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(s: String) {
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonLoadData.visibility = View.GONE
        loadData()
    }

    private fun loadData() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getListingsLatest()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    binding.recyclerViewListings.layoutManager = LinearLayoutManager(context)
                    binding.recyclerViewListings.adapter = MarketAdapter(context, response.body()?.data)
                    binding.textViewError.visibility = View.GONE
                    binding.loadingView.visibility = View.GONE
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
        /*textView_error.text = ""
        loadingView.visibility = View.GONE*/
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoadData.setOnClickListener(View.OnClickListener {
            binding.buttonLoadData.visibility = View.GONE
            loadData()
        })
    }

    companion object {
        fun newInstance(): CryptoFragment = CryptoFragment()
        /*val domain = "https://shibminer.page.link"
        val baseUrl =
            Uri.parse("https://play.google.com/store/apps/details?id=com.miner.shib_miner")*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonInvite -> {
                //createLink()
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    /*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Market")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/
}