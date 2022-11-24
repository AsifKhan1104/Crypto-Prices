package com.crypto.prices.view.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.R
import com.crypto.prices.model.Coin
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_search.view.*

class SearchCryptoAdapter(context: Context?, var data: List<Coin>?) :
    RecyclerView.Adapter<SearchCryptoAdapter.MyViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tableLayout = view.table_layout
        private val imageView = view.imageView
        private val textViewSymbol = view.textView_symbol
        private val textViewName = view.textView_name

        fun bind(context: Context, position: Int, data: Coin?) {
            try {
                textViewSymbol.text = data?.symbol
                textViewName.text = data?.name

                // set icons
                Glide.with(context)
                    .load(data?.large)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // on click listener
            tableLayout.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    /*val intent = Intent(context, CryptoDetailActivity::class.java)
                    intent.putExtra("categoryId", data?.id)
                    intent.putExtra("categoryName", data?.name)
                    context.startActivity(intent)*/
                }
            })
        }
    }
}