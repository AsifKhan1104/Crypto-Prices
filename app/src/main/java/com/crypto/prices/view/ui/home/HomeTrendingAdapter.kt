package com.crypto.prices.view.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.asf.cryptoprices.R
import com.crypto.prices.model.CoinX
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.item_trending.view.*
import javax.inject.Inject

class HomeTrendingAdapter @Inject constructor(@ActivityContext val context: Context?) :
    RecyclerView.Adapter<HomeTrendingAdapter.TrendingViewHolder>() {
    private var data: List<CoinX>? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = TrendingViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class TrendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view.cardView
        private val imageViewId = view.imageViewId
        private val textViewName = view.textViewName
        private val textViewPrice = view.textViewPrice
        private val textViewChange = view.textViewRank

        fun bind(context: Context, position: Int, data: CoinX) {
            textViewName.text = data.item.name + " (" + data.item.symbol + ")"
            textViewPrice.text = String.format("%.9f", data.item.price_btc) + " btc"
            textViewChange.text = data.item.market_cap_rank.toString()

            // set icons
            Glide.with(context)
                .load(data.item.small)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewId)

            // on click listener
            cardView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(context, CryptoDetailSearchActivity::class.java)
                    intent.putExtra("id", data?.item?.id)
                    context.startActivity(intent)
                }
            })
        }
    }

    fun updateList(list: List<CoinX>) {
        data = list
        notifyDataSetChanged()
    }
}