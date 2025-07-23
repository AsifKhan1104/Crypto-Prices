package com.crypto.prices.view.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ItemTrendingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.data.offline.Watchlist
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailActivity
import dagger.hilt.android.qualifiers.ActivityContext
import java.math.BigDecimal
import javax.inject.Inject

class HomeWatchlistAdapter @Inject constructor(@ActivityContext private val context: Context?) :
    RecyclerView.Adapter<HomeWatchlistAdapter.MyViewHolder>() {

    private var data: List<Watchlist>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { watchlistItem ->
                holder.bind(ctx, watchlistItem)
            }
        }
    }

    fun updateList(newList: List<Watchlist>?) {
        data = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: ItemTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: Watchlist) {
            binding.textViewName.text = data.name
            binding.textViewPrice.text = "${data.currency}${data.price}"
            binding.textViewRankText.text = "Price"

            // Arrow and percentage formatting
            val priceChange = data.priceChange24h ?: "0"
            val priceChangeBD = BigDecimal(priceChange)
            val isNegative = priceChangeBD < BigDecimal.ZERO

            val iconRes = if (isNegative) {
                R.drawable.ic_arrow_down_black_24
            } else {
                R.drawable.ic_arrow_up_black_24
            }

            val cleanPercentage = if (isNegative) priceChange.substring(1) else priceChange

            binding.textViewRank.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
            binding.textViewRank.text = String.format("%.6f", cleanPercentage.toFloat()) + "%"

            Glide.with(context)
                .load(data.image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageViewId)

            binding.cardView.setOnClickListener {
                val intent = if (data.type == "crypto") {
                    Intent(context, CryptoDetailSearchActivity::class.java)
                } else {
                    Intent(context, NftDetailActivity::class.java)
                }
                intent.putExtra("id", data.id)
                context.startActivity(intent)
            }
        }
    }
}