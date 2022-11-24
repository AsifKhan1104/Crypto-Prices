package com.crypto.prices.view.ui.market.exchanges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.R
import com.crypto.prices.model.ExchangesData
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_exchanges.view.*
import kotlinx.android.synthetic.main.item_nft.view.*
import kotlinx.android.synthetic.main.item_nft.view.imageView_id

class ExchangesPagingAdapter(context: Context?) :
    PagingDataAdapter<ExchangesData, ExchangesPagingAdapter.MyViewHolder>(ExchangesPagingAdapter.MyComparator) {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        ExchangesPagingAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_exchanges, parent, false)
        )

    //override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: ExchangesPagingAdapter.MyViewHolder, position: Int) {
        holder.bind(context!!, position, getItem(position) as ExchangesData)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageView = view.imageView_id
        private val textViewExchange = view.textView_exchange
        private val textViewVolume = view.textView_volume
        private val textViewTrustScore = view.textView_trust_score

        fun bind(context: Context, position: Int, data: ExchangesData?) {
            textViewExchange.text = data?.name
            textViewVolume.text = data?.trade_volume_24h_btc?.toString()
            textViewTrustScore.text = data?.trust_score?.toString()

            // set icons
            Glide.with(context)
                .load(data?.image)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    /*val intent = Intent(context, NftDetailActivity::class.java)
                    intent.putExtra("id", data?.id)
                    context.startActivity(intent)*/
                }
            })
        }
    }

    object MyComparator : DiffUtil.ItemCallback<ExchangesData>() {
        override fun areItemsTheSame(oldItem: ExchangesData, newItem: ExchangesData): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExchangesData, newItem: ExchangesData): Boolean {
            return oldItem == newItem
        }
    }
}