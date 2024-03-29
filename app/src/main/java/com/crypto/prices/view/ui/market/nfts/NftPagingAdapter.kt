package com.crypto.prices.view.ui.market.nfts

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.crypto.prices.model.NftData
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailActivity
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_crypto.view.textView_name
import kotlinx.android.synthetic.main.item_crypto.view.textView_symbol
import kotlinx.android.synthetic.main.item_nft.view.*

class NftPagingAdapter(context: Context?) :
    PagingDataAdapter<NftData, NftPagingAdapter.MyViewHolder>(NftPagingAdapter.MyComparator) {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        NftPagingAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_nft, parent, false)
        )

    //override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: NftPagingAdapter.MyViewHolder, position: Int) {
        holder.bind(context!!, position, getItem(position) as NftData)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val textViewSymbol = view.textView_symbol
        private val textViewName = view.textView_name
        private val textViewAssetPlfrm = view.textView_asset_plat

        fun bind(context: Context, position: Int, data: NftData?) {
            textViewSymbol.text = data?.symbol
            textViewName.text = data?.name
            textViewAssetPlfrm.text = data?.asset_platform_id

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(context, NftDetailActivity::class.java)
                    intent.putExtra("id", data?.id)
                    intent.putExtra("name", data?.name)
                    context.startActivity(intent)
                }
            })
        }
    }

    object MyComparator : DiffUtil.ItemCallback<NftData>() {
        override fun areItemsTheSame(oldItem: NftData, newItem: NftData): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NftData, newItem: NftData): Boolean {
            return oldItem == newItem
        }
    }
}