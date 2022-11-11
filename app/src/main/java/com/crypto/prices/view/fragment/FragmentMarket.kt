package com.crypto.prices.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.R
import com.crypto.prices.remote.Service
import com.crypto.prices.view.adapter.MarketAdapter
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.coroutines.*

class FragmentMarket : Fragment()/*, View.OnClickListener*/ {
    /*val service = Service().getUsersService()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(s: String) {
        textView_error.text = s
        loadingView.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_crypto, container, false)
    }

    private fun loadData() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getListingsLatest()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    recyclerView_listings.layoutManager = LinearLayoutManager(context)
                    recyclerView_listings.adapter = MarketAdapter(context, response.body()?.data)
                    textView_error.visibility = View.GONE
                    loadingView.visibility = View.GONE
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
        *//*textView_error.text = ""
        loadingView.visibility = View.GONE*//*
    }

    *//*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadData()
    }*//*

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    companion object {
        fun newInstance(): FragmentMarket = FragmentMarket()
        *//*val domain = "https://shibminer.page.link"
        val baseUrl =
            Uri.parse("https://play.google.com/store/apps/details?id=com.miner.shib_miner")*//*
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

    *//*override fun onResume() {
        super.onResume()
        // track screen event
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Market")
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }*/
}