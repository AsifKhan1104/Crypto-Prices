package com.crypto.prices.view.ui.market

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.prices.R
import com.crypto.prices.model.Data
import com.crypto.prices.view.activity.MarketDetailActivity
import kotlinx.android.synthetic.main.item_crypto.view.*

class CryptoAdapter(context: Context?, var data: List<Data>?) :
    RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CryptoViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
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

        fun bind(context: Context, position: Int, data: Data) {
            textViewId.text = (position+1).toString()
            textViewSymbol.text = data.symbol
            textViewName.text = data.name
            /*if (!data.symbol.equals("SHIB"))
                textViewPrice.text =
                    String.format("%.9f", data.quote.USD.price).toDouble().toString()
            else*/
                textViewPrice.text = data.quote.USD.price.toString()
            try {
                textView24hp.text =
                    String.format("%.1f", data.quote.USD.percent_change_24h).toDouble().toString()+"%"
                textViewMarketCap.text = String.format("%.1f", data.quote.USD.market_cap)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // set icons
            val imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+data.id.toString()+".png"
            Glide.with(context)
                .load(imageUrl)
                .into(imageViewIcon)

            // on click listener
            tableLayout.setOnClickListener(object:View.OnClickListener{
                override fun onClick(p0: View?) {
                    val intent = Intent(context, MarketDetailActivity::class.java)
                    intent.putExtra("market_listings_data", data)
                    context.startActivity(intent)
                }
            })
        }
    }

    /*fun update(newReqs: List<WithdrawReq>) {
        requests.clear()
        requests.addAll(newReqs)
        notifyDataSetChanged()
    }*/
}