package com.crypto.prices.view.ui.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crypto.prices.R
import kotlinx.android.synthetic.main.item_currency_converter.view.*

class CurrConverterAdapter(context: Context?, var data: ArrayList<String>) :
    RecyclerView.Adapter<CurrConverterAdapter.CurrConverter>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CurrConverter(
        LayoutInflater.from(parent.context).inflate(R.layout.item_currency_converter, parent, false)
    )

    override fun getItemCount() = /*data.size*/1

    override fun onBindViewHolder(holder: CurrConverter, position: Int) {
        holder.bind(context!!, position, data)
    }

    class CurrConverter(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewSymbol = view.textViewSymbol
        //private val textViewName = view.textViewName
        private val textViewValue = view.textViewValue

        fun bind(context: Context, position: Int, data: ArrayList<String>) {
            textViewSymbol.text = data.get(0) + " " + data.get(1) + " (" + data.get(2) + ") "
            //textViewName.text = data.get(2) + " - "
            textViewValue.text = data.get(3) + " " + String.format("%.9f", data.get(4).toFloat())
        }
    }
}