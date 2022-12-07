package com.crypto.prices.view.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.crypto.prices.R
import com.crypto.prices.model.CryptoData
import com.crypto.prices.utils.Utility.getCurrencyGlobal
import com.crypto.prices.utils.Utility.getCurrencySymbolGlobal
import com.crypto.prices.view.AppRepository
import com.crypto.prices.view.AppRepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataProvider(context: Context?, intent: Intent?) : RemoteViewsFactory {
    var cryptoList: List<CryptoData> = ArrayList()
    var mContext: Context? = null
    var currencySymbol: String? = null

    override fun onCreate() {
        initData()
    }

    override fun onDataSetChanged() {
        initData()
    }

    override fun onDestroy() {}
    override fun getCount(): Int {
        return cryptoList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(mContext!!.packageName, R.layout.item_crypto_widget)
        val cryptoItem = cryptoList[position]
        view.setImageViewUri(R.id.imageView, Uri.parse(cryptoItem.image))
        view.setTextViewText(R.id.textView_symbol, cryptoItem.symbol)
        view.setTextViewText(R.id.textView_name, cryptoItem.name)
        view.setTextViewText(
            R.id.textView_price, getCurrencySymbolGlobal(mContext!!) +
                    cryptoItem.current_price.toString()
        )
        view.setTextViewText(R.id.textView_24hp, cryptoItem.price_change_percentage_24h.toString())
        return view
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(mContext!!.packageName, R.layout.item_widget_loading)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun initData() {
        Log.v("CryptoWidget", "initData(): ")
        val map = HashMap<String, String?>()
        map["vs_currency"] = getCurrencyGlobal(mContext!!)
        map["order"] = "market_cap_desc"
        map["per_page"] = "20"

        GlobalScope.launch {
            val appRepository: AppRepository = AppRepositoryImpl()
            val response = appRepository.getCryptoPrices(map as MutableMap<String, String>)
            // check if request is successful
            if (response.isSuccessful) {
                cryptoList = response.body()!!
                Log.v("CryptoWidget", "response: " + response.toString())
                Log.v("CryptoWidget", "response body: " + response.body().toString())
            }
        }
    }

    init {
        mContext = context
    }
}