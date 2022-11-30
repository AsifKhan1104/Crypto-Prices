package com.crypto.prices.view.ui.market.derivatives.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crypto.prices.R
import com.crypto.prices.model.DerivativesDetailData
import kotlinx.android.synthetic.main.item_derivatives_detail.view.*

class DerivativesDetailAdapter(context: Context?, var data: List<DerivativesDetailData>?) :
    RecyclerView.Adapter<DerivativesDetailAdapter.MyViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_derivatives_detail, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewSymbol = view.textView_symbol
        private val textViewPrice = view.textView_price
        private val textViewOpenInterest = view.textView_open_interest

        fun bind(context: Context, position: Int, detailData: DerivativesDetailData) {
            textViewSymbol.text = detailData?.symbol
            textViewPrice.text = "$" + detailData?.price
            if (detailData?.open_interest != null) {
                textViewOpenInterest.text = "$" + detailData?.open_interest?.toString()
            } else {
                textViewOpenInterest.text = "-"
            }
        }
    }
}