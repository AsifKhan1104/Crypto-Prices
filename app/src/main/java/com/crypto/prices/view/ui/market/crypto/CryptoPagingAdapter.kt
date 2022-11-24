package com.crypto.prices.view.ui.market.crypto

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.prices.R
import com.crypto.prices.model.CryptoData
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailActivity
import kotlinx.android.synthetic.main.item_crypto.view.*

class CryptoPagingAdapter(context: Context?) :
    PagingDataAdapter<CryptoData, CryptoPagingAdapter.CryptoViewHolder>(MyComparator) {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CryptoViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
    )

    //override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.bind(context!!, position, getItem(position) as CryptoData)
    }

    class CryptoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageViewIcon = view.imageView_id
        private val textViewId = view.textView_id
        private val textViewSymbol = view.textView_symbol
        private val textViewName = view.textView_name
        private val textViewPrice = view.textView_price
        private val textView24hp = view.textView_24hp
        private val textViewMarketCap = view.textView_market_cap

        fun bind(context: Context, position: Int, data: CryptoData?) {
            //textViewId.text = (position+1).toString()
            textViewSymbol.text = data?.symbol
            textViewName.text = data?.name
            textViewPrice.text = data?.current_price?.toString()
            try {
                textView24hp.text =
                    String.format("%.1f", data?.price_change_percentage_24h).toString() + "%"
                textViewMarketCap.text = data?.market_cap?.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // set icons
            /*val imageUrl =
                "https://s2.coinmarketcap.com/static/img/coins/64x64/" + data.id.toString() + ".png"*/
            Glide.with(context)
                .load(data?.image)
                .into(imageViewIcon)

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(context, CryptoDetailActivity::class.java)
                    intent.putExtra("crypto_data", data)
                    context.startActivity(intent)
                }
            })
        }
    }

    object MyComparator : DiffUtil.ItemCallback<CryptoData>() {
        override fun areItemsTheSame(oldItem: CryptoData, newItem: CryptoData): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CryptoData, newItem: CryptoData): Boolean {
            return oldItem == newItem
        }
    }
}