package com.crypto.prices.view.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemTrendingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.CoinX
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class HomeTrendingAdapter @Inject constructor(@ActivityContext private val context: Context?) :
    RecyclerView.Adapter<HomeTrendingAdapter.TrendingViewHolder>() {

    private var data: List<CoinX>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrendingViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { coin ->
                holder.bind(ctx, coin)
            }
        }
    }

    class TrendingViewHolder(private val binding: ItemTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: CoinX) {
            binding.textViewName.text = "${data.item.name} (${data.item.symbol})"
            binding.textViewPrice.text = String.format("%.9f", data.item.price_btc) + " btc"
            binding.textViewRank.text = data.item.market_cap_rank.toString()

            Glide.with(context)
                .load(data.item.small)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageViewId)

            binding.cardView.setOnClickListener {
                val intent = Intent(context, CryptoDetailSearchActivity::class.java)
                intent.putExtra("id", data.item.id)
                context.startActivity(intent)
            }
        }
    }

    fun updateList(list: List<CoinX>) {
        data = list
        notifyDataSetChanged()
    }
}