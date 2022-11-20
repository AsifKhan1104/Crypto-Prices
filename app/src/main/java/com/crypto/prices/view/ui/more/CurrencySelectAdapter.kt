package com.crypto.prices.view.ui.more

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crypto.prices.R
import com.crypto.prices.utils.CurrencyData
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.item_crypto.view.textView_name
import kotlinx.android.synthetic.main.item_currency_select.view.*

class CurrencySelectAdapter(context: Context?, var data: List<CurrencyData>?, itemClick:ItemClick) :
    RecyclerView.Adapter<CurrencySelectAdapter.CryptoViewHolder>() {
    private val context = context
    private val mItemClick = itemClick

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CryptoViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_currency_select, parent, false),
        mItemClick
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class CryptoViewHolder(view: View, itemClick:ItemClick) : RecyclerView.ViewHolder(view) {
        private val parentLayout = view.parent_layout
        private val textViewName = view.textView_name
        private val mItemClick = itemClick

        fun bind(context: Context, position: Int, data: CurrencyData?) {
            textViewName.text = data?.name + " (" + data?.symbol + ")"

            // on click listener
            parentLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    // save selected currency in shared prefs
                    Utility.setCurrency(context as Activity, data?.currency)
                    Utility.setCurrencyName(context as Activity, data?.name)
                    mItemClick.onItemClicked()
                }
            })
        }
    }

    interface ItemClick {
        fun onItemClicked()
    }
}