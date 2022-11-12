package com.crypto.prices.view.ui.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.R
import com.crypto.prices.model.Article
import com.crypto.prices.utils.Utility
import com.crypto.prices.utils.Utility.formatPublishedDateTime
import kotlinx.android.synthetic.main.item_news.view.*

class NewsAdapter(context: Context?, var data: List<Article>?) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = NewsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
    )

    override fun getItemCount() = data!!.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(context!!, position, data!!.get(position))
    }

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardViewNews = view.cardViewNews
        private val imageView = view.imageView
        private val textViewTitle = view.textViewTitle
        private val textViewSource = view.textViewSource
        private val textViewDate = view.textViewDate

        fun bind(context: Context, position: Int, article: Article) {
            // show news icon
            val reqOptions = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(R.drawable.image_placeholder) // any placeholder to load at start
                .error(R.drawable.image_placeholder)  // any image in case of error

            Glide.with(context)
                .load(article.urlToImage) // image url
                /*.override(200, 200) // resizing*/
                .apply(reqOptions)
                .into(imageView)  // imageview object

            textViewTitle.text = article.title
            textViewSource.text = article.source.name
            textViewDate.text = formatPublishedDateTime(article.publishedAt)

            // on click listener
            cardViewNews.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    Utility.openChromeCustomTabUrlNews(context, article.url)
                }
            })
        }
    }

    /*fun update(newReqs: List<WithdrawReq>) {
        requests.clear()
        requests.addAll(newReqs)
        notifyDataSetChanged()
    }*/
}