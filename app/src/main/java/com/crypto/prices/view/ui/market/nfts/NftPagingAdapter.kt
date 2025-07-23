package com.crypto.prices.view.ui.market.nfts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemNftBinding
import com.crypto.prices.model.NftData
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailActivity

class NftPagingAdapter(private val context: Context?) :
    PagingDataAdapter<NftData, NftPagingAdapter.MyViewHolder>(MyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let {
            getItem(position)?.let { data ->
                holder.bind(it, data)
            }
        }
    }

    class MyViewHolder(private val binding: ItemNftBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, data: NftData) {
            binding.textViewSymbol.text = data.symbol
            binding.textViewName.text = data.name
            binding.textViewAssetPlat.text = data.asset_platform_id

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, NftDetailActivity::class.java).apply {
                    putExtra("id", data.id)
                    putExtra("name", data.name)
                }
                context.startActivity(intent)
            }
        }
    }

    object MyComparator : DiffUtil.ItemCallback<NftData>() {
        override fun areItemsTheSame(oldItem: NftData, newItem: NftData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NftData, newItem: NftData): Boolean {
            return oldItem == newItem
        }
    }
}