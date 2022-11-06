package com.crypto.prices.view.activity

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.crypto.prices.R
import com.crypto.prices.model.Data
import kotlinx.android.synthetic.main.activity_market_detail.*

class MarketDetailActivity : AppCompatActivity() {
    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_detail)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        // get data from intent
        var data = intent.extras?.getParcelable<Data>("market_listings_data")

        // set data
        setTitle(data?.name + " (" + data?.symbol + ")")
        textView_price.text = "$ " + String.format("%.9f", data?.quote?.USD?.price).toDouble()
        textView_24hp.text =
            String.format("%.1f", data?.quote?.USD?.percent_change_24h).toDouble().toString() + "%"
        textView_mcr.text = data?.cmc_rank.toString()
        textView_mc.text = data?.quote?.USD?.market_cap?.toString()
        textView_fdmc.text = data?.quote?.USD?.fully_diluted_market_cap?.toString()
        textView_trdng_vol.text = data?.quote?.USD?.volume_24h?.toString()
        textView_max_supply.text = data?.max_supply?.toString()
        textView_circ_supply.text = data?.circulating_supply?.toString()
        textView_total_supply.text = data?.total_supply?.toString()
        textView_pc_7.text = data?.quote?.USD?.percent_change_7d?.toString()
        textView_pc_30.text = data?.quote?.USD?.percent_change_30d?.toString()
        textView_pc_60.text = data?.quote?.USD?.percent_change_60d?.toString()
        textView_pc_90.text = data?.quote?.USD?.percent_change_90d?.toString()
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