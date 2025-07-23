package com.crypto.prices.view.ui.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.asf.cryptoprices.databinding.ItemSearchBinding
import com.crypto.prices.model.Nft
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SearchNftsAdapter @Inject constructor(
    @ActivityContext private val context: Context?
) : RecyclerView.Adapter<SearchNftsAdapter.MyViewHolder>() {

    private var data: List<Nft>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nft = data?.get(position)
        if (context != null && nft != null) {
            holder.bind(context, nft)
        }
    }

    fun updateList(list: List<Nft>) {
        data = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, nft: Nft) {
            binding.textViewSymbol.text = nft.symbol
            binding.textViewName.text = nft.name

            Glide.with(context)
                .load(nft.thumb)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imageView)

            binding.tableLayout.setOnClickListener {
                val intent = Intent(context, NftDetailActivity::class.java).apply {
                    putExtra("id", nft.id)
                    putExtra("name", nft.name)
                }
                context.startActivity(intent)
            }
        }
    }
}