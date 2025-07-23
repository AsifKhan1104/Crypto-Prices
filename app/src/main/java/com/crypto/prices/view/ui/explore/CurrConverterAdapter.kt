package com.crypto.prices.view.ui.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.databinding.ItemCurrencyConverterBinding
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class CurrConverterAdapter @Inject constructor(@ActivityContext val context: Context?) :
    RecyclerView.Adapter<CurrConverterAdapter.CurrConverter>() {

    private var data: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrConverter {
        val binding = ItemCurrencyConverterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrConverter(binding)
    }

    override fun getItemCount(): Int = 1 // Replace with: data?.size ?: 0 when needed

    override fun onBindViewHolder(holder: CurrConverter, position: Int) {
        context?.let { ctx ->
            data?.let { holder.bind(ctx, position, it) }
        }
    }

    class CurrConverter(private val binding: ItemCurrencyConverterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, position: Int, data: ArrayList<String>) {
            binding.textViewSymbol.text = "${data[0]} ${data[1]} (${data[2]})"
            binding.textViewValue.text = "${data[3]} ${"%.9f".format(data[4].toFloat())}"
        }
    }

    fun updateList(list: ArrayList<String>) {
        data = list
        notifyDataSetChanged()
    }
}