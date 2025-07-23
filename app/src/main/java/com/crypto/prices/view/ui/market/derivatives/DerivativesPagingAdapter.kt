package com.crypto.prices.view.ui.market.derivatives

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemDerivativesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.view.ui.market.derivatives.detail.DerivativesDetailActivity

class DerivativesPagingAdapter(private val context: Context?) :
    PagingDataAdapter<DerivativesData, DerivativesPagingAdapter.MyViewHolder>(MyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDerivativesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let { ctx ->
            getItem(position)?.let { data ->
                holder.bind(ctx, data)
            }
        }
    }

    class MyViewHolder(private val binding: ItemDerivativesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: DerivativesData) {
            binding.textViewExchange.text = data.name
            binding.textViewVolume.text = data.trade_volume_24h_btc

            binding.textViewOpenInterest.text =
                data.open_interest_btc?.toString() ?: "-"

            Glide.with(context)
                .load(data.image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageViewId)

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, DerivativesDetailActivity::class.java)
                intent.putExtra("derivatives_data", data)
                context.startActivity(intent)
            }
        }
    }

    object MyComparator : DiffUtil.ItemCallback<DerivativesData>() {
        override fun areItemsTheSame(oldItem: DerivativesData, newItem: DerivativesData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DerivativesData, newItem: DerivativesData): Boolean {
            return oldItem == newItem
        }
    }
}