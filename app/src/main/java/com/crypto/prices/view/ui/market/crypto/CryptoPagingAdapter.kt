package com.crypto.prices.view.ui.market.crypto

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemCryptoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.CryptoData
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailActivity

class CryptoPagingAdapter(private val context: Context?) :
    PagingDataAdapter<CryptoData, CryptoPagingAdapter.CryptoViewHolder>(MyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val binding = ItemCryptoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CryptoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        context?.let { ctx ->
            getItem(position)?.let { cryptoData ->
                holder.bind(ctx, cryptoData)
            }
        }
    }

    class CryptoViewHolder(private val binding: ItemCryptoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: CryptoData) {
            binding.textViewSymbol.text = data.symbol
            binding.textViewName.text = data.name
            binding.textViewPrice.text = data.current_price?.toString()

            try {
                binding.textView24hp.text = String.format("%.1f", data.price_change_percentage_24h) + "%"
                binding.textViewMarketCap.text = data.market_cap?.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            Glide.with(context)
                .load(data.image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageViewId)

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, CryptoDetailActivity::class.java)
                intent.putExtra("crypto_data", data)
                context.startActivity(intent)
            }
        }
    }

    object MyComparator : DiffUtil.ItemCallback<CryptoData>() {
        override fun areItemsTheSame(oldItem: CryptoData, newItem: CryptoData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CryptoData, newItem: CryptoData): Boolean {
            return oldItem == newItem
        }
    }
}