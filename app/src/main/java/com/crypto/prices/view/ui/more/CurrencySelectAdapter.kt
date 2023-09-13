package com.crypto.prices.view.ui.more

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.crypto.prices.utils.CurrencyData
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.item_crypto.view.textView_name
import kotlinx.android.synthetic.main.item_currency_select.view.*


class CurrencySelectAdapter(
    context: Context?,
    var data: List<CurrencyData>?,
    itemClick: ItemClick
) :
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

    class CryptoViewHolder(view: View, itemClick: ItemClick) : RecyclerView.ViewHolder(view) {
        private val parentLayout = view.parent_layout
        private val textViewName = view.textView_name
        private val mItemClick = itemClick

        fun bind(context: Context, position: Int, data: CurrencyData?) {
            textViewName.text = data?.name + " (" + data?.symbol + ")"
            val currentSelectedCurrency = Utility.getCurrency(context as Activity).let { it }
            if (data?.currency!!.equals(currentSelectedCurrency)) {
                parentLayout.background = context.getDrawable(R.drawable.list_pressed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewName.setTextColor(
                        context.resources.getColor(
                            R.color.selected_text_color,
                            context.theme
                        )
                    )
                } else {
                    textViewName.setTextColor(context.resources.getColor(R.color.selected_text_color))
                }
            } else {
                parentLayout.background = context.getDrawable(R.drawable.list_not_pressed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewName.setTextColor(
                        context.resources.getColor(
                            R.color.black,
                            context.theme
                        )
                    )
                } else {
                    textViewName.setTextColor(context.resources.getColor(R.color.black))
                }
            }

            // on click listener
            parentLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    // save selected currency in shared prefs
                    Utility.setCurrency(context as Activity, data?.currency)
                    Utility.setCurrencyName(context as Activity, data?.name)
                    Utility.setCurrencySymbol(context as Activity, data?.symbol)
                    mItemClick.onItemClicked()

                    /*// show dialog before restarting the app
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("To make selected currency active, app restart is required. \nRestarting in 10 seconds")
                        .setTitle("Alert")
                        .setIcon(R.drawable.alert)
                    builder.setCancelable(false)
                    *//*.setNeutralButton("OK", { dialog, id ->
                        Utility.restartApp(context)
                    })*//*
                    val alert: AlertDialog = builder.create()
                    alert.show()

                    // restarting the app in 10 secs
                    GlobalScope.launch {
                        delay(10000)*/
                    Utility.restartApp(context)
                    /*}*/
                }
            })
        }
    }

    interface ItemClick {
        fun onItemClicked()
    }
}