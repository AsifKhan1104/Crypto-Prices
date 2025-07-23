package com.crypto.prices.view.ui.market.derivatives.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.DialogDerivativesDetailBinding
import com.asf.cryptoprices.databinding.ItemDerivativesDetailBinding
import com.crypto.prices.model.DerivativesDetailData
import com.crypto.prices.utils.Utility
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class DerivativesDetailAdapter @Inject constructor(@ActivityContext private val context: Context?) :
    RecyclerView.Adapter<DerivativesDetailAdapter.MyViewHolder>() {

    private var data: List<DerivativesDetailData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDerivativesDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { item ->
                holder.bind(ctx, item)
            }
        }
    }

    class MyViewHolder(private val binding: ItemDerivativesDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, detailData: DerivativesDetailData) {
            binding.textViewSymbol.text = detailData.symbol
            binding.textViewPrice.text = "$${detailData.price}"
            binding.textViewOpenInterest.text = detailData.open_interest?.let { "$$it" } ?: "-"

            binding.tableLayout.setOnClickListener {
                showCustomDialog(context, detailData)
            }
        }

        private fun showCustomDialog(context: Context, data: DerivativesDetailData) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)

            val dialogBinding = DialogDerivativesDetailBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(dialogBinding.root)

            dialogBinding.apply {
                textViewMarket.text = data.market
                textViewCType.text = data.contract_type
                textViewOpenInterest.text = data.open_interest?.let { "$$it" } ?: "-"
                textView24HChange.text = String.format("%.4f", data.price_percentage_change_24h?.toFloat()) + " %"
                textViewLastPrice.text = "$${data.price}"
                textViewIndexBasis.text = data.index?.toString()
                textViewPair.text = data.symbol
                textViewLastTraded.text = Utility.formatPublishedDateTimeLong(data.last_traded_at!!)
                textView24HVol.text = "$" + String.format("%.2f", data.volume_24h?.toFloat())
                textViewBidAskSpread.text = data.spread?.toString()?.plus(" %") ?: "-"
                textViewIndexPrice.text = String.format("%.4f", data.index?.toFloat()) + " USDT"
                textViewFundingRate.text = data.funding_rate?.toString()?.plus(" %")
                textViewFundingRate.isSelected = true
            }

            dialog.show()
        }
    }

    fun updateList(list: List<DerivativesDetailData>) {
        data = list
        notifyDataSetChanged()
    }
}