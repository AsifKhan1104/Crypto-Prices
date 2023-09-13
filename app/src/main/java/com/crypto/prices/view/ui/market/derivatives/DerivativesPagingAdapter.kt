package com.crypto.prices.view.ui.market.derivatives

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.view.ui.market.derivatives.detail.DerivativesDetailActivity
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_derivatives.view.textView_open_interest
import kotlinx.android.synthetic.main.item_exchanges.view.textView_exchange
import kotlinx.android.synthetic.main.item_exchanges.view.textView_volume
import kotlinx.android.synthetic.main.item_nft.view.imageView_id

class DerivativesPagingAdapter(context: Context?) :
    PagingDataAdapter<DerivativesData, DerivativesPagingAdapter.MyViewHolder>(
        DerivativesPagingAdapter.MyComparator
    ) {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        DerivativesPagingAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_derivatives, parent, false)
        )

    //override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: DerivativesPagingAdapter.MyViewHolder, position: Int) {
        holder.bind(context!!, position, getItem(position) as DerivativesData)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageView = view.imageView_id
        private val textViewExchange = view.textView_exchange
        private val textViewVolume = view.textView_volume
        private val textViewOpenInterest = view.textView_open_interest

        fun bind(context: Context, position: Int, data: DerivativesData?) {
            textViewExchange.text = data?.name
            textViewVolume.text = data?.trade_volume_24h_btc
            if (data?.open_interest_btc != null)
                textViewOpenInterest.text = data?.open_interest_btc?.toString()
            else
                textViewOpenInterest.text = "-"

            // set icons
            Glide.with(context)
                .load(data?.image)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(context, DerivativesDetailActivity::class.java)
                    intent.putExtra("derivatives_data", data)
                    context.startActivity(intent)
                }
            })
        }
    }

    object MyComparator : DiffUtil.ItemCallback<DerivativesData>() {
        override fun areItemsTheSame(oldItem: DerivativesData, newItem: DerivativesData): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DerivativesData,
            newItem: DerivativesData
        ): Boolean {
            return oldItem == newItem
        }
    }
}