package com.crypto.prices.view.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.R
import com.crypto.prices.database.Watchlist
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailActivity
import kotlinx.android.synthetic.main.item_trending.view.*
import java.math.BigDecimal

class HomeWatchlistAdapter(context: Context?, var data: List<Watchlist>?) :
    RecyclerView.Adapter<HomeWatchlistAdapter.MyViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
    )

    override fun getItemCount() = if (data != null) data!!.size else 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context!!, position, data?.let { it.get(position) })
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view.cardView
        private val imageViewId = view.imageViewId
        private val textViewName = view.textViewName
        private val textViewPrice = view.textViewPrice
        private val textViewPriceChange = view.textViewRank
        private val textViewRankText = view.textViewRankText

        fun bind(context: Context, position: Int, data: Watchlist?) {
            textViewName.text = data?.name
            textViewPrice.text =
                Utility.getCurrencySymbol(context as Activity) + data?.price
            textViewRankText.text = "Price"

            // set arrow as per price
            var priceChangePerc = data?.priceChange24h!!
            var priceChangePercBD = data?.priceChange24h?.toBigDecimal()!!
            var priceArrow = R.drawable.ic_arrow_up_black_24
            if (priceChangePercBD.compareTo(BigDecimal.ZERO) < 0) {
                priceArrow = R.drawable.ic_arrow_down_black_24
                priceChangePerc = priceChangePerc.substring(1, priceChangePerc.length)
            }
            textViewPriceChange.setCompoundDrawablesWithIntrinsicBounds(priceArrow, 0, 0, 0)
            textViewPriceChange.text = priceChangePerc + "%"

            // set icons
            Glide.with(context)
                .load(data?.image)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewId)

            // on click listener
            cardView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    var intent: Intent
                    if (data?.type.equals("crypto")) {
                        intent = Intent(context, CryptoDetailSearchActivity::class.java)
                    } else {
                        intent = Intent(context, NftDetailActivity::class.java)
                    }
                    intent.putExtra("id", data?.id)
                    context.startActivity(intent)
                }
            })
        }
    }
}