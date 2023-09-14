package com.crypto.prices.view.ui.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.Coin
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchActivity
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.android.synthetic.main.item_crypto.view.table_layout
import kotlinx.android.synthetic.main.item_search.view.imageView
import kotlinx.android.synthetic.main.item_search.view.textView_name
import kotlinx.android.synthetic.main.item_search.view.textView_symbol
import javax.inject.Inject

class SearchCryptoAdapter @Inject constructor(@ActivityContext val context: Context?) :
    RecyclerView.Adapter<SearchCryptoAdapter.MyViewHolder>() {
    private var data: List<Coin>? = null

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
                    val intent = Intent(context, CryptoDetailSearchActivity::class.java)
                    intent.putExtra("id", data?.id)
                    context.startActivity(intent)
                }
            })
        }
    }

    fun updateList(list: List<Coin>) {
        data = list
        notifyDataSetChanged()
    }
}