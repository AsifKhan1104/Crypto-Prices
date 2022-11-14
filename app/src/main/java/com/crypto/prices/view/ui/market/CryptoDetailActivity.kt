package com.crypto.prices.view.ui.market

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.crypto.prices.databinding.ActivityCryptoDetailBinding
import com.crypto.prices.model.CryptoData

class CryptoDetailActivity : AppCompatActivity() {
    private var _binding: ActivityCryptoDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCryptoDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        var data = intent.extras?.getParcelable<CryptoData>("crypto_data")

        // set data
        setTitle(data?.name)
        // set coin icon
        Glide.with(this)
            .load(data?.image)
            .into(binding.imageViewSymbol)

        binding.textViewSymbol.text = data?.symbol
        binding.textViewPrice.text = "$" + String.format("%.2f", data?.current_price)
        binding.textView24hp.text =
            String.format("%.1f", data?.price_change_percentage_24h) + "%"
        binding.textViewMcr.text = data?.market_cap_rank?.toString()
        binding.textViewMc.text = data?.market_cap?.toString() + "$"
        binding.textViewFdmc.text = data?.fully_diluted_valuation?.toString() + "$"
        binding.textViewTotalVol.text = data?.total_volume.toString()
        binding.textViewMaxSupply.text = data?.max_supply?.toString()
        binding.textViewCircSupply.text = data?.circulating_supply?.toString()
        binding.textViewTotalSupply.text = data?.total_supply?.toString()
        binding.textView24h.text = data?.high_24h?.toString() + "$"
        binding.textView24l.text = data?.low_24h?.toString() + "$"
        binding.textViewAth.text = data?.ath?.toString() + "$"
        binding.textViewAtl.text = data?.atl?.toString() + "$"

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}