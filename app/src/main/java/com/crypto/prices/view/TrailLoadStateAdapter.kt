package com.crypto.prices.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crypto.prices.CryptoApplication
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.LoadStateViewBinding

class TrailLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<TrailLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        val binding = holder.binding

        binding.loadStateRetry.isVisible = loadState !is LoadState.Loading
        binding.loadStateErrorMessage.isVisible = loadState !is LoadState.Loading
        binding.loadStateProgress.isVisible = loadState is LoadState.Loading

        if (loadState is LoadState.Error) {
            // Optionally use the error message
            // binding.loadStateErrorMessage.text = loadState.error.localizedMessage
            binding.loadStateErrorMessage.text = CryptoApplication.instance?.getString(R.string.error_msg_retry)
        }

        binding.loadStateRetry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    class LoadStateViewHolder(val binding: LoadStateViewBinding) : RecyclerView.ViewHolder(binding.root)
}