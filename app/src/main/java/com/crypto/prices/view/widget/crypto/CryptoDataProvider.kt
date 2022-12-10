package com.crypto.prices.view.widget.crypto

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.crypto.prices.R
import com.crypto.prices.model.CryptoData
import com.crypto.prices.utils.Utility.getCurrencyGlobal
import com.crypto.prices.utils.Utility.getCurrencySymbolGlobal
import com.crypto.prices.view.AppRepository
import com.crypto.prices.view.AppRepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal


class CryptoDataProvider(context: Context?, intent: Intent?) : RemoteViewsFactory {
    var cryptoList: List<CryptoData> = ArrayList()
    var mContext: Context? = null

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
        try {
            // set arrow as per price
            var priceChangePerc = cryptoItem.price_change_percentage_24h.toString()
            var priceChangePercBD = cryptoItem.price_change_percentage_24h
            var priceArrow = R.drawable.ic_arrow_up_black_24
            if (priceChangePercBD!!.compareTo(BigDecimal.ZERO) < 0) {
                priceArrow = R.drawable.ic_arrow_down_black_24
                priceChangePerc = priceChangePerc.substring(1, priceChangePerc.length)
            }
            view.setImageViewResource(R.id.imageViewArrow, priceArrow)
            view.setTextViewText(
                R.id.textView_24hp,
                String.format("%.2f", priceChangePerc.toFloat()) + "%"
            )

            // set icon
            /*GlobalScope.launch {
                var myImage: Bitmap? = null
                try {
                    val `in`: InputStream = URL(cryptoItem.image).openStream()
                    myImage = BitmapFactory.decodeStream(`in`)

                    // now show bitmap in remoteview
                    withContext(Dispatchers.Main) {
                        view.setImageViewBitmap(R.id.imageView, myImage)
                    }
                } catch (e: Exception) {
                    Log.e("ImageLoadingError", e.message!!)
                    e.printStackTrace()
                }
            }*/

            val appWidgetTarget = AppWidgetTarget(
                mContext!!,
                R.id.imageView,
                view,
                ComponentName(mContext!!, this::class.java)
            )

            Glide.with(mContext!!)
                .asBitmap()
                .load(cryptoItem.image)
                .into(appWidgetTarget)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

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
            }
        }
    }

    init {
        mContext = context
    }

    private fun getActiveWidgetIds(context: Context): IntArray {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, this::class.java)

        // return ID of all active widgets within this AppWidgetProvider
        return appWidgetManager.getAppWidgetIds(componentName)
    }
}