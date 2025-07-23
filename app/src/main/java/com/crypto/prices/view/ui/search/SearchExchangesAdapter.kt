package com.crypto.prices.view.ui.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.asf.cryptoprices.databinding.ItemSearchBinding
import com.crypto.prices.model.Exchange
import com.crypto.prices.view.ui.market.exchanges.detail.ExchangesDetailSearchActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SearchExchangesAdapter @Inject constructor(
    @ActivityContext private val context: Context?
) : RecyclerView.Adapter<SearchExchangesAdapter.MyViewHolder>() {

    private var data: List<Exchange>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val exchange = data?.get(position)
        if (context != null && exchange != null) {
            holder.bind(context, exchange)
        }
    }

    fun updateList(list: List<Exchange>) {
        data = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, exchange: Exchange) {
            binding.textViewSymbol.text = exchange.market_type
            binding.textViewName.text = exchange.name

            Glide.with(context)
                .load(exchange.large)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageView)

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, ExchangesDetailSearchActivity::class.java).apply {
                    putExtra("id", exchange.id)
                }
                context.startActivity(intent)
            }
        }
    }
}