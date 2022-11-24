package com.crypto.prices.view.ui.market.categories

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.prices.R
import com.crypto.prices.model.CategoriesData
import kotlinx.android.synthetic.main.item_categories.view.*
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_crypto.view.textView_24hp
import kotlinx.android.synthetic.main.item_crypto.view.textView_market_cap

class CategoriesAdapter(context: Context?, var data: List<CategoriesData>?) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = CategoriesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageViewIcon1 = view.imageView_topCoin1
        private val imageViewIcon2 = view.imageView_topCoin2
        private val imageViewIcon3 = view.imageView_topCoin3
        private val textViewName = view.textView_category
        private val textView24hp = view.textView_24hp
        private val textViewMarketCap = view.textView_market_cap

        fun bind(context: Context, position: Int, data: CategoriesData?) {
            try {
                textViewName.text = data?.name
                textView24hp.text =
                    String.format("%.1f", data?.market_cap_change_24h).toString() + "%"
                textViewMarketCap.text = data?.market_cap?.toString()

                // set icons
                Glide.with(context)
                    .load(data?.top_3_coins?.get(0))
                    .into(imageViewIcon1)
                Glide.with(context)
                    .load(data?.top_3_coins?.get(1))
                    .into(imageViewIcon2)
                Glide.with(context)
                    .load(data?.top_3_coins?.get(2))
                    .into(imageViewIcon3)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val intent = Intent(context, CategoriesCoinListActivity::class.java)
                    intent.putExtra("categoryId", data?.id)
                    intent.putExtra("categoryName", data?.name)
                    context.startActivity(intent)
                }
            })
        }
    }
}