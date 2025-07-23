package com.crypto.prices.view.ui.market.exchanges

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemExchangesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.ExchangesData
import com.crypto.prices.view.ui.market.exchanges.detail.ExchangesDetailActivity

class ExchangesPagingAdapter(private val context: Context?) :
    PagingDataAdapter<ExchangesData, ExchangesPagingAdapter.MyViewHolder>(MyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemExchangesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let {
            getItem(position)?.let { data ->
                holder.bind(it, data)
            }
        }
    }

    class MyViewHolder(private val binding: ItemExchangesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: ExchangesData) {
            binding.textViewExchange.text = data.name
            binding.textViewVolume.text = data.trade_volume_24h_btc?.toString()
            binding.textViewTrustScore.text = data.trust_score?.toString()

            // Load image
            Glide.with(context)
                .load(data.image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageViewId)

            // Click listener
            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, ExchangesDetailActivity::class.java)
                intent.putExtra("exchanges_data", data)
                context.startActivity(intent)
            }
        }
    }

    object MyComparator : DiffUtil.ItemCallback<ExchangesData>() {
        override fun areItemsTheSame(oldItem: ExchangesData, newItem: ExchangesData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExchangesData, newItem: ExchangesData): Boolean {
            return oldItem == newItem
        }
    }
}