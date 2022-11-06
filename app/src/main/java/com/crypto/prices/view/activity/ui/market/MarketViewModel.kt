package com.crypto.prices.view.activity.ui.market

import androidx.lifecycle.ViewModel

class MarketViewModel : ViewModel() {
    /*val service = Service().getUsersService()
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = service.getListingsLatest()
        }
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
    }*/
}