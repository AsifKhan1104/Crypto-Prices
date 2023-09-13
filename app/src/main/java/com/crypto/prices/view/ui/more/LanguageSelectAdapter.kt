package com.crypto.prices.view.ui.more

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.crypto.prices.model.LanguageData
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.item_crypto.view.textView_name
import kotlinx.android.synthetic.main.item_currency_select.view.*

class LanguageSelectAdapter(
    context: Context?,
    var data: List<LanguageData>?,
    itemClick: ItemClick
) :
    RecyclerView.Adapter<LanguageSelectAdapter.MyViewHolder>() {
    private val context = context
    private val mItemClick = itemClick

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_language_select, parent, false),
        mItemClick
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class MyViewHolder(view: View, itemClick: ItemClick) : RecyclerView.ViewHolder(view) {
        private val parentLayout = view.parent_layout
        private val textViewName = view.textView_name
        private val mItemClick = itemClick

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(context: Context, position: Int, data: LanguageData?) {
            textViewName.text = data?.name
            val selectedLang = Utility.getLanguage(context as Activity).let { it }
            if (data?.name!!.equals(selectedLang, true)) {
                parentLayout.background = context.getDrawable(R.drawable.list_pressed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewName.setTextColor(context.resources.getColor(R.color.selected_text_color, context.theme))
                } else {
                    textViewName.setTextColor(context.resources.getColor(R.color.selected_text_color))
                }
            } else {
                parentLayout.background = context.getDrawable(R.drawable.list_not_pressed)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewName.setTextColor(context.resources.getColor(R.color.black, context.theme))
                } else {
                    textViewName.setTextColor(context.resources.getColor(R.color.black))
                }
            }

            // on click listener
            parentLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    // save selected language in shared prefs
                    Utility.setLanguage(context, data?.name)
                    mItemClick.onItemClicked(position)
                }
            })
        }
    }

    interface ItemClick {
        fun onItemClicked(position: Int)
    }
}