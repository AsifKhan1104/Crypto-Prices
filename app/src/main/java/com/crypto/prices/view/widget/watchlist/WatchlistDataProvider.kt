package com.crypto.prices.view.widget.watchlist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.asf.cryptoprices.R
import com.crypto.prices.data.offline.Watchlist
import com.crypto.prices.data.offline.WatchlistRepo
import java.math.BigDecimal


class WatchlistDataProvider(context: Context?, intent: Intent?) : RemoteViewsFactory {
    var watchlist: List<Watchlist> = ArrayList()
    var mContext: Context? = null

    override fun onCreate() {
        initData()
    }

    override fun onDataSetChanged() {
        initData()
    }

    override fun onDestroy() {}
    override fun getCount(): Int {
        return watchlist.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val view = RemoteViews(mContext!!.packageName, R.layout.item_widget)
        val item = watchlist[position]
        view.setImageViewUri(R.id.imageView, Uri.parse(item.image))
        view.setTextViewText(R.id.textView_symbol, item.symbol)
        view.setTextViewText(R.id.textView_name, item.name)
        view.setTextViewText(
            R.id.textView_price, item.currency +
                    item.price
        )
        try {
            // set arrow as per price
            var priceChangePerc = item?.priceChange24h!!
            var priceChangePercBD = item?.priceChange24h?.toBigDecimal()!!
            var priceArrow = R.drawable.ic_arrow_up_black_24
            if (priceChangePercBD.compareTo(BigDecimal.ZERO) < 0) {
                priceArrow = R.drawable.ic_arrow_down_black_24
                priceChangePerc = priceChangePerc.substring(1, priceChangePerc.length)
            }
            view.setImageViewResource(R.id.imageViewArrow, priceArrow)
            view.setTextViewText(
                R.id.textView_24hp,
                String.format("%.2f", priceChangePerc.toFloat()) + "%"
            )

            // set icon
            val appWidgetTarget = AppWidgetTarget(
                mContext!!,
                R.id.imageView,
                view,
                ComponentName(mContext!!, this::class.java)
            )

            Glide.with(mContext!!)
                .asBitmap()
                .load(item.image)
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
        // check if any coins/nfts are watchlisted
        var database = WatchlistRepo(mContext!!)
        watchlist = database.getAllData()
    }

    init {
        mContext = context
    }
}