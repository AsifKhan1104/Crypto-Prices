package com.crypto.prices.view.ui.market

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.crypto.prices.databinding.ActivityCryptoDetailBinding
import com.crypto.prices.model.CryptoData

class CryptoDetailActivity : AppCompatActivity() {
    private var _binding: ActivityCryptoDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCryptoDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        // get data from intent
        var data = intent.extras?.getParcelable<CryptoData>("crypto_data")

        // set data
        setTitle(data?.name + " (" + data?.symbol + ")")
        binding.textViewPrice.text = "$ " + String.format("%.9f", data?.current_price)
        binding.textView24hp.text =
            String.format("%.1f", data?.price_change_percentage_24h) + "%"
        binding.textViewMcr.text = data?.market_cap_rank?.toString()
        binding.textViewMc.text = data?.market_cap?.toString()
        binding.textViewFdmc.text = data?.fully_diluted_valuation?.toString()
        binding.textViewTotalVol.text = data?.total_volume.toString()
        binding.textViewMaxSupply.text = data?.max_supply?.toString()
        binding.textViewCircSupply.text = data?.circulating_supply?.toString()
        binding.textViewTotalSupply.text = data?.total_supply?.toString()
        /*textView_pc_7.text = data?.quote?.USD?.percent_change_7d?.toString()
        textView_pc_30.text = data?.quote?.USD?.percent_change_30d?.toString()
        textView_pc_60.text = data?.quote?.USD?.percent_change_60d?.toString()
        textView_pc_90.text = data?.quote?.USD?.percent_change_90d?.toString()*/
        binding.textViewPc7.visibility = View.GONE
        binding.textViewPc30.visibility = View.GONE
        binding.textViewPc60.visibility = View.GONE
        binding.textViewPc90.visibility = View.GONE
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