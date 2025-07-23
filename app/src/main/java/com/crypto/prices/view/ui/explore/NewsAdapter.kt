package com.crypto.prices.view.ui.explore

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ItemNewsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.crypto.prices.model.Article
import com.crypto.prices.utils.Utility
import com.crypto.prices.utils.Utility.formatPublishedDateTime
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class NewsAdapter @Inject constructor(@ActivityContext private val context: Context?) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var data: List<Article>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        context?.let { ctx ->
            data?.get(position)?.let { article ->
                holder.bind(ctx, article)
            }
        }
    }

    class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, article: Article) {
            val reqOptions = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)

            Glide.with(context)
                .load(article.urlToImage)
                .apply(reqOptions)
                .into(binding.imageView)

            binding.textViewTitle.text = article.title
            binding.textViewSource.text = article.source.name
            binding.textViewDate.text = formatPublishedDateTime(article.publishedAt)

            binding.cardViewNews.setOnClickListener {
                Utility.openChromeCustomTabUrlNews(context, article.url)
            }
        }
    }

    fun update(listArticle: List<Article>) {
        data = listArticle
        notifyDataSetChanged()
    }
}