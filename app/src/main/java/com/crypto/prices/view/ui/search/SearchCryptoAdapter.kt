package com.crypto.prices.view.ui.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.asf.cryptoprices.databinding.ItemSearchBinding
import com.crypto.prices.model.Coin
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SearchCryptoAdapter @Inject constructor(
    @ActivityContext private val context: Context?
) : RecyclerView.Adapter<SearchCryptoAdapter.MyViewHolder>() {

    private var data: List<Coin>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val coin = data?.get(position)
        context?.let { ctx ->
            coin?.let {
                holder.bind(ctx, it)
            }
        }
    }

    fun updateList(list: List<Coin>) {
        data = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, coin: Coin) {
            binding.textViewSymbol.text = coin.symbol
            binding.textViewName.text = coin.name

            // Load icon with Glide and circle crop
            Glide.with(context)
                .load(coin.large)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageView)

            // Click listener to open detail activity
            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, CryptoDetailSearchActivity::class.java).apply {
                    putExtra("id", coin.id)
                }
                context.startActivity(intent)
            }
        }
    }
}