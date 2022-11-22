package com.crypto.prices.view.ui.market

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.prices.R
import com.crypto.prices.model.CryptoData
import com.crypto.prices.view.BaseViewHolder
import kotlinx.android.synthetic.main.item_crypto.view.*

class CryptoAdapter(context: Context?, var data: MutableList<CryptoData>?) :
    RecyclerView.Adapter<BaseViewHolder>() {
    val mContext = context!!
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    val mListItems = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            VIEW_TYPE_NORMAL -> return CryptoViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
            )
            VIEW_TYPE_LOADING -> return ProgressViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            )
        }
        return return CryptoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_crypto, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == mListItems!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount() = if (mListItems == null) 0 else mListItems.size

    fun addItems(postItems: MutableList<CryptoData>) {
        mListItems?.addAll(postItems)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        mListItems?.add(CryptoData())
        notifyItemInserted(mListItems!!.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = mListItems!!.size - 1
        val item: CryptoData? = getItem(position)
        if (item != null) {
            mListItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mListItems?.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): CryptoData? {
        return mListItems?.get(position)
    }

    inner class CryptoViewHolder(view: View) : BaseViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageViewIcon = view.imageView_id
        private val textViewId = view.textView_id
        private val textViewSymbol = view.textView_symbol
        private val textViewName = view.textView_name
        private val textViewPrice = view.textView_price
        private val textView24hp = view.textView_24hp
        private val textViewMarketCap = view.textView_market_cap

        override fun bind(position: Int) {
            super.bind(position)
            val data = mListItems?.get(position)
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
            Glide.with(mContext)
                .load(data?.image)
                .into(imageViewIcon)

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(mContext, CryptoDetailActivity::class.java)
                    intent.putExtra("crypto_data", data)
                    mContext.startActivity(intent)
                }
            })
        }

        override fun clear() {

        }
    }

    class ProgressViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(position: Int) {
            super.bind(position)
        }

        override fun clear() {
        }
    }
}