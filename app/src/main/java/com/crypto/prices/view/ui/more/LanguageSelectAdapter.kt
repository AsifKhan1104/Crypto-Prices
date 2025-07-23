package com.crypto.prices.view.ui.more

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ItemLanguageSelectBinding
import com.crypto.prices.model.LanguageData
import com.crypto.prices.utils.Utility

class LanguageSelectAdapter(
    private val context: Context?,
    private var data: List<LanguageData>?,
    private val itemClick: ItemClick
) : RecyclerView.Adapter<LanguageSelectAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLanguageSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, itemClick)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { languageData ->
                holder.bind(ctx, position, languageData)
            }
        }
    }

    class MyViewHolder(
        private val binding: ItemLanguageSelectBinding,
        private val itemClick: ItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(context: Context, position: Int, data: LanguageData) {
            binding.textViewName.text = data.name

            val selectedLang = Utility.getLanguage(context as Activity)
            val isSelected = data.name.equals(selectedLang, ignoreCase = true)

            binding.parentLayout.background = if (isSelected) {
                context.getDrawable(R.drawable.list_pressed)
            } else {
                context.getDrawable(R.drawable.list_not_pressed)
            }

            val textColorRes = if (isSelected) R.color.selected_text_color else R.color.black
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(textColorRes, context.theme)
            } else {
                context.resources.getColor(textColorRes)
            }
            binding.textViewName.setTextColor(color)

            binding.parentLayout.setOnClickListener {
                Utility.setLanguage(context, data.name)
                itemClick.onItemClicked(position)
            }
        }
    }

    interface ItemClick {
        fun onItemClicked(position: Int)
    }
}