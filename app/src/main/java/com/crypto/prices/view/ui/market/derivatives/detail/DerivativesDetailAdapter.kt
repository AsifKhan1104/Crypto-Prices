package com.crypto.prices.view.ui.market.derivatives.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.crypto.prices.model.DerivativesDetailData
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.item_derivatives_detail.view.*

class DerivativesDetailAdapter(context: Context?, var data: List<DerivativesDetailData>?) :
    RecyclerView.Adapter<DerivativesDetailAdapter.MyViewHolder>() {
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_derivatives_detail, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val parentLayout = view.table_layout
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

            // parent view click listener
            parentLayout.setOnClickListener(View.OnClickListener {
                showCustomDialog(context, detailData)
            })
        }

        fun showCustomDialog(context: Context, data: DerivativesDetailData?) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_derivatives_detail)
            val market = dialog.findViewById(R.id.textViewMarket) as TextView
            val ctType = dialog.findViewById(R.id.textViewCType) as TextView
            val openInterest = dialog.findViewById(R.id.textViewOpenInterest) as TextView
            val hPerc = dialog.findViewById(R.id.textView24HChange) as TextView
            val lastPrice = dialog.findViewById(R.id.textViewLastPrice) as TextView
            val indexBasisPerc = dialog.findViewById(R.id.textViewIndexBasis) as TextView
            val marketPair = dialog.findViewById(R.id.textViewPair) as TextView
            val lastTraded = dialog.findViewById(R.id.textViewLastTraded) as TextView
            val hVolume = dialog.findViewById(R.id.textView24HVol) as TextView
            val bidAskSpread = dialog.findViewById(R.id.textViewBidAskSpread) as TextView
            val indexPrice = dialog.findViewById(R.id.textViewIndexPrice) as TextView
            val fundingRate = dialog.findViewById(R.id.textViewFundingRate) as TextView

            // set text
            market.text = data?.market
            ctType.text = data?.contract_type
            openInterest.text =
                if (data?.open_interest != null) "$" + data?.open_interest?.toString() else "-"
            hPerc.text = String.format("%.4f", data?.price_percentage_change_24h?.toFloat()) + " %"
            lastPrice.text = "$" + data?.price
            indexBasisPerc.text = data?.index?.toString()
            marketPair.text = data?.symbol
            lastTraded.text = Utility.formatPublishedDateTimeLong(data?.last_traded_at!!)
            hVolume.text = "$" + String.format("%.2f", data?.volume_24h?.toFloat())
            bidAskSpread.text = if (data?.spread != null) data?.spread?.toString() + " %" else "-"
            indexPrice.text = String.format("%.4f", data?.index?.toFloat()) + " USDT"
            fundingRate.text = data?.funding_rate?.toString() + " %"
            fundingRate.isSelected = true

            dialog.show()
        }
    }
}