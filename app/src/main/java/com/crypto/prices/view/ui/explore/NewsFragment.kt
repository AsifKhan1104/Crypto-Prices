package com.crypto.prices.view.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.R
import com.crypto.prices.remote.Service
import com.crypto.prices.view.adapter.NewsAdapter
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.*

class NewsFragment : Fragment(), View.OnClickListener {
    val service = Service().getNewsService()
    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(s: String) {
        textView_error.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.fragment_news, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_loadData.visibility = View.GONE
        loadData()
    }

    private fun loadData() {
        val params: MutableMap<String, String> = HashMap()
        params["q"] = "Crypto"
        params["apiKey"] = "0bfddc1dafb24e8b883576fba4b58235"
        params["sortBy"] = "publishedAt"

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getAllNews(params)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    recyclerView_news.layoutManager = LinearLayoutManager(context)
                    recyclerView_news.adapter = NewsAdapter(context, response.body()?.articles)
                    textView_error.visibility = View.GONE
                    loadingView.visibility = View.GONE
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

        button_loadData.setOnClickListener(View.OnClickListener {
            button_loadData.visibility = View.GONE
            loadData()
        })
    }

    companion object {
        fun newInstance(): MoreFragment = MoreFragment()
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