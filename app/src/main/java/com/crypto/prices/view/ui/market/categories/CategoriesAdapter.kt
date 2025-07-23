package com.crypto.prices.view.ui.market.categories

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemCategoriesBinding
import com.bumptech.glide.Glide
import com.crypto.prices.model.CategoriesData
import com.crypto.prices.view.ui.market.categories.CategoriesCoinListActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CategoriesAdapter @Inject constructor(@ActivityContext private val context: Context?) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    private var data: List<CategoriesData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { item ->
                holder.bind(ctx, item)
            }
        }
    }

    fun updateList(list: List<CategoriesData>) {
        data = list
        notifyDataSetChanged()
    }

    class CategoriesViewHolder(private val binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: CategoriesData) {
            try {
                binding.textViewCategory.text = data.name
                binding.textView24hp.text = "${String.format("%.1f", data.market_cap_change_24h)}%"
                binding.textViewMarketCap.text = data.market_cap?.toString()

                val coins = data.top_3_coins
                if (!coins.isNullOrEmpty()) {
                    if (coins.size > 0) Glide.with(context).load(coins[0]).into(binding.imageViewTopCoin1)
                    if (coins.size > 1) Glide.with(context).load(coins[1]).into(binding.imageViewTopCoin2)
                    if (coins.size > 2) Glide.with(context).load(coins[2]).into(binding.imageViewTopCoin3)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, CategoriesCoinListActivity::class.java)
                intent.putExtra("categoryId", data.id)
                intent.putExtra("categoryName", data.name)
                context.startActivity(intent)
            }
        }
    }
}