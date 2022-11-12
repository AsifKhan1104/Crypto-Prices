package com.crypto.prices.view.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.prices.R
import com.crypto.prices.model.CoinX
import kotlinx.android.synthetic.main.item_trending.view.*

class HomeTrendingAdapter(context: Context?, var data: List<CoinX>?) :
    RecyclerView.Adapter<HomeTrendingAdapter.TrendingViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = TrendingViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class TrendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val cardView = view.cardView
        private val imageViewId = view.imageViewId
        private val textViewName = view.textViewName
        private val textViewPrice = view.textViewPrice
        private val textViewChange = view.textViewRank

        fun bind(context: Context, position: Int, data: CoinX) {
            textViewName.text = data.item.name + " ( " + data.item.symbol + " )"
            textViewPrice.text = String.format("%.9f", data.item.price_btc) + " btc"
            textViewChange.text = data.item.market_cap_rank.toString()

            // set icons
            Glide.with(context)
                .load(data.item.small)
                .into(imageViewId)

            // on click listener
            /*cardView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    *//*val intent = Intent(context, MarketDetailActivity::class.java)
                    intent.putExtra("market_listings_data", data)
                    context.startActivity(intent)*//*
                }
            })*/
        }
    }
}