package com.crypto.prices.view.ui.market.crypto.detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.asf.cryptoprices.R
import com.asf.cryptoprices.databinding.ActivityCryptoDetailBinding
import com.bumptech.glide.Glide
import com.crypto.prices.data.offline.Watchlist
import com.crypto.prices.data.offline.WatchlistRepo
import com.crypto.prices.model.CryptoChartData
import com.crypto.prices.model.crypto.search.CryptoDetailData
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.utils.chart.CustomMarkerView
import com.crypto.prices.utils.chart.XAxisValueFormatter
import com.crypto.prices.utils.chart.YAxisValueFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class CryptoDetailSearchActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityCryptoDetailBinding? = null
    private val mCryptoDetailSearchViewModel: CryptoDetailSearchViewModel by viewModels()
    private val mCryptoDetailViewModel: CryptoDetailViewModel by viewModels()
    private val TAG = "CryptoSearchActivity"

    private lateinit var chart: LineChart
    private var cryptoId: String? = null
    private var chartData: CryptoChartData? = null
    private lateinit var selectedTextViewTimeFilter: TextView
    private lateinit var selectedTextViewFilter: TextView
    private lateinit var mDatabase: WatchlistRepo
    private lateinit var data: CryptoDetailData

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCryptoDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        MyAnalytics.trackScreenViews("CryptoDetailSearchActivity", javaClass.simpleName)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        cryptoId = intent?.getStringExtra("id")!!
        initView()
        setUpViewModel()

        mDatabase = WatchlistRepo(this)
    }

    private fun initView() {
        // on click listeners
        binding.textView1h.setOnClickListener(this)
        binding.textView24hr.setOnClickListener(this)
        binding.textView7d.setOnClickListener(this)
        binding.textView1m.setOnClickListener(this)
        binding.textView3m.setOnClickListener(this)
        binding.textView1y.setOnClickListener(this)
        binding.textViewAllTime.setOnClickListener(this)
        binding.textViewPriceFilter.setOnClickListener(this)
        binding.textViewMcFilter.setOnClickListener(this)

        initChart()
    }

    private fun setMarketStatsData(masterData: CryptoDetailData?) {
        // set title
        setTitle(masterData?.name)
        // set coin icon
        Glide.with(this)
            .load(masterData?.image?.large)
            .into(binding.imageViewSymbol)

        binding.textViewSymbol.text = masterData?.symbol

        val data = masterData?.market_data
        binding.textViewPrice.text =
            Utility.getCurrencySymbol(this) + data?.current_price?.usd?.toString()

        // set arrow %
        var priceChangePercNegative = false
        var priceArrow = R.drawable.ic_arrow_up_24
        if (data?.price_change_percentage_24h!!.compareTo(BigDecimal.ZERO) < 0) {
            priceArrow = R.drawable.ic_arrow_down_24
            priceChangePercNegative = true
        }
        binding.textView24hp.setCompoundDrawablesWithIntrinsicBounds(priceArrow, 0, 0, 0)

        var formattedPriceChangePerc = data?.price_change_percentage_24h.toString()
        binding.textView24hp.text = if (priceChangePercNegative) formattedPriceChangePerc.substring(
            1, formattedPriceChangePerc.length
        ) else formattedPriceChangePerc + "%"
        binding.textViewMcr.text = "#" + data?.market_cap_rank?.toString()
        binding.textViewMc.text =
            Utility.getCurrencySymbol(this) + data?.market_cap?.usd?.toString()
        /*binding.textViewFdmc.text =
            if (data?.fully_diluted_valuation != null) Utility.getCurrencySymbol(this) + data?.fully_diluted_valuation?.toString() else ""*/
        binding.textViewFdmcHeader.visibility = View.GONE
        binding.textViewFdmc.visibility = View.GONE
        binding.textViewTotalVol.text = data?.total_volume?.usd?.toString()
        binding.textViewMaxSupply.text = data?.max_supply?.toString()
        binding.textViewCircSupply.text = data?.circulating_supply?.toString()
        binding.textViewTotalSupply.text = data?.total_supply?.toString()
        binding.textView24h.text = Utility.getCurrencySymbol(this) + data?.high_24h?.usd?.toString()
        binding.textView24l.text = Utility.getCurrencySymbol(this) + data?.low_24h?.usd?.toString()

        // set arrow %
        var athChangePerc: String? = data?.ath_change_percentage?.usd?.toString()
        var atlChangePerc: String? = data?.atl_change_percentage?.usd?.toString()
        var priceArrowAltH = R.drawable.ic_arrow_up_24
        if (data?.ath_change_percentage?.usd?.compareTo(BigDecimal.ZERO) < 0) {
            priceArrowAltH = R.drawable.ic_arrow_down_24
            athChangePerc = data?.ath_change_percentage?.usd?.toString()
                .substring(1, data?.ath_change_percentage?.usd?.toString()!!.length)
        }
        binding.textViewAthPerc.setCompoundDrawablesWithIntrinsicBounds(priceArrowAltH, 0, 0, 0)
        var priceArrowAltL = R.drawable.ic_arrow_up_24
        if (data?.atl_change_percentage?.usd?.compareTo(BigDecimal.ZERO) < 0) {
            priceArrowAltL = R.drawable.ic_arrow_down_24
            atlChangePerc = data?.atl_change_percentage?.usd?.toString()
                .substring(1, data?.atl_change_percentage?.usd?.toString()!!.length)
        }
        binding.textViewAtlPerc.setCompoundDrawablesWithIntrinsicBounds(priceArrowAltL, 0, 0, 0)

        binding.textViewAth.text = Utility.getCurrencySymbol(this) + data?.ath?.usd?.toString()
        binding.textViewAthPerc.text = athChangePerc + "%"
        binding.textViewAtl.text = Utility.getCurrencySymbol(this) + data?.atl?.usd?.toString()
        binding.textViewAtlPerc.text = atlChangePerc + "%"
    }

    // intialize chart
    private fun initChart() {
        chart = binding.chart
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
        //chart.setBackgroundColor(Color.rgb(104, 241, 175))

        chart.setNoDataText("Building chart ...");
        // description text
        chart.description.text =
            "price (" + Utility.getCurrencySymbol(this)?.let { it } + ") vs time"
        chart.description.isEnabled = true

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f

        val x = chart.xAxis
        x.textColor = Color.BLACK
        x.axisLineColor = Color.BLACK
        x.setPosition(XAxisPosition.BOTTOM)
        x.enableGridDashedLine(2f, 7f, 0f)
        //x.setAxisMaximum(5f)
        //x.setAxisMinimum(0f)
        x.setLabelCount(6, false)
        x.setDrawLabels(true)
        x.setEnabled(true)
        x.setDrawAxisLine(true)
        x.setDrawGridLines(true)
        x.isGranularityEnabled = true
        x.granularity = 7f
        x.labelRotationAngle = 315f
        x.valueFormatter = XAxisValueFormatter()
        x.setCenterAxisLabels(false)
        x.setDrawLimitLinesBehindData(true)


        val y = chart.axisLeft
        //y.setTypeface(tfLight)
        y.setLabelCount(6, false)
        y.setDrawAxisLine(true)
        y.textColor = Color.BLACK
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.valueFormatter = YAxisValueFormatter()
        y.axisLineColor = Color.BLACK
        y.setDrawZeroLine(false)
        y.setDrawLimitLinesBehindData(false);

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        //chart.animateXY(2000, 2000)
        chart.animateX(2000)
        // set marker
        val customMarker = CustomMarkerView(this, R.layout.custom_marker_view)
        chart.marker = customMarker
    }

    private fun setChartData(cryptoChartData: CryptoChartData?, priceFilterActive: Boolean) {
        chartData = cryptoChartData
        val values = ArrayList<Entry>()

        var chartFillData: List<List<BigDecimal?>>
        if (priceFilterActive) {
            chartFillData = cryptoChartData?.prices!!
        } else {
            chartFillData = cryptoChartData?.market_caps!!
        }
        for (i in 0 until chartFillData.size) {
            try {
                values.add(
                    Entry(
                        chartFillData.get(i).get(0)!!.toFloat(), // timestamp
                        chartFillData.get(i).get(1)!!.toFloat() // price
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        val set: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set = chart.data.getDataSetByIndex(0) as LineDataSet
            set.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a data set and give it a type
            set = LineDataSet(values, "DataSet")
            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            set.cubicIntensity = 0.2f
            set.setDrawFilled(true)
            set.setDrawCircles(false)
            set.lineWidth = 1.8f
            set.circleRadius = 4f
            set.setCircleColor(Color.BLUE)
            set.highLightColor = Color.rgb(244, 117, 117)
            set.color = resources.getColor(R.color.chart_color)
            set.fillColor = resources.getColor(R.color.chart_fill_color)
            //set.fillAlpha = 40
            set.setDrawHorizontalHighlightIndicator(true)
            set.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // create a data object with the data sets
            val data = LineData(set)
            //data.setValueTypeface(tfLight)
            data.setValueTextSize(9f)
            data.setDrawValues(false)

            // set data
            chart.data = data
        }
        // don't forget to refresh the drawing
        chart.invalidate()
    }

    private fun setUpViewModel() {
        // create map of params needed for api
        val mapList: MutableMap<String, String> = HashMap()
        mapList["id"] = cryptoId.toString()

        mCryptoDetailSearchViewModel.getData(mapList)

        binding.textViewPriceFilter.isSelected = true
        selectedTextViewFilter = binding.textViewPriceFilter
        // create map of params needed for api
        val map: MutableMap<String, String> = HashMap()
        map["symbol"] = cryptoId.toString()
        Utility.getCurrency(this)?.let { map["currency"] = it }
        // default day selection
        binding.textView24hr.isSelected = true
        selectedTextViewTimeFilter = binding.textView24hr
        val days = 1
        map["days"] = days.toString()

        mCryptoDetailViewModel.initCryptoChart(map)

        // load remote data
        loadData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
        binding.groupStats.visibility = View.INVISIBLE
    }

    private fun onLoading() {
        binding.groupStats.visibility = View.INVISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.groupStats.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    private fun onErrorChart(s: String) {
        binding.textViewErrorChart.text = s
        binding.textViewErrorChart.visibility = View.VISIBLE
        //binding.loadingViewChart.visibility = View.GONE
        binding.chart.visibility = View.GONE
        binding.tableLayout.visibility = View.INVISIBLE
        binding.linearLayoutFilter.visibility = View.INVISIBLE
    }

    private fun onLoadingChart() {
        binding.textViewErrorChart.visibility = View.GONE
        binding.tableLayout.visibility = View.INVISIBLE
        binding.linearLayoutFilter.visibility = View.INVISIBLE
        //binding.loadingViewChart.visibility = View.VISIBLE
    }

    private fun onLoadingFinishedChart() {
        binding.chart.visibility = View.VISIBLE
        binding.tableLayout.visibility = View.VISIBLE
        binding.linearLayoutFilter.visibility = View.VISIBLE
        binding.textViewErrorChart.visibility = View.GONE
        //binding.loadingViewChart.visibility = View.GONE
    }

    fun loadData() {
        try {
            mCryptoDetailSearchViewModel.cryptoDetailLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            data = it
                            setMarketStatsData(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        onError(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader
                        onLoading()
                    }
                }
            })

            mCryptoDetailViewModel.cryptoChartLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinishedChart()
                            setChartData(
                                it,
                                if (selectedTextViewFilter == binding.textViewPriceFilter) true else false
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        //show error message
                        onErrorChart(it.networkErrorMessage.toString())
                    }

                    is NetworkResult.Loading -> {
                        //show loader
                        onLoadingChart()
                    }
                }
            })
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        if (mDatabase.isWatchlisted(cryptoId!!)) {
            menu.getItem(0).setIcon(R.drawable.star_selected)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_fav -> {
                val id = cryptoId!!
                if (mDatabase.isWatchlisted(id)) {
                    item.setIcon(R.drawable.star)
                    mDatabase.delete(id)
                    Utility.showToast(this, getString(R.string.coin_removed))
                } else {
                    item.setIcon(R.drawable.star_selected)
                    mDatabase.insert(
                        Watchlist(
                            id,
                            data?.image.thumb!!,
                            data?.market_data?.market_cap?.usd?.toString(),
                            data?.market_cap_rank.toString(),
                            data?.name.toString(),
                            data?.market_data?.current_price?.usd?.toString(),
                            data?.market_data?.price_change_24h_in_currency?.usd?.toString(),
                            data?.symbol.toString(),
                            "$",
                            "crypto"
                        )
                    )
                    Utility.showToast(this, getString(R.string.coin_added))
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.textView1h.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView1h.isSelected = true
                selectedTextViewTimeFilter = binding.textView1h
                mCryptoDetailViewModel.getCryptoChart(0.04.toString())
            }
            binding.textView24hr.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView24hr.isSelected = true
                selectedTextViewTimeFilter = binding.textView24hr
                mCryptoDetailViewModel.getCryptoChart(1.toString())
            }
            binding.textView7d.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView7d.isSelected = true
                selectedTextViewTimeFilter = binding.textView7d
                mCryptoDetailViewModel.getCryptoChart(7.toString())
            }
            binding.textView1m.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView1m.isSelected = true
                selectedTextViewTimeFilter = binding.textView1m
                mCryptoDetailViewModel.getCryptoChart(30.toString())
            }
            binding.textView3m.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView3m.isSelected = true
                selectedTextViewTimeFilter = binding.textView3m
                mCryptoDetailViewModel.getCryptoChart(90.toString())
            }
            binding.textView1y.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView1y.isSelected = true
                selectedTextViewTimeFilter = binding.textView1y
                mCryptoDetailViewModel.getCryptoChart(365.toString())
            }
            binding.textViewAllTime.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textViewAllTime.isSelected = true
                selectedTextViewTimeFilter = binding.textViewAllTime
                mCryptoDetailViewModel.getCryptoChart("max")
            }
            binding.textViewPriceFilter.id -> {
                selectedTextViewFilter.isSelected = false
                binding.textViewPriceFilter.isSelected = true
                selectedTextViewFilter = binding.textViewPriceFilter
                setChartData(chartData, true)
            }
            binding.textViewMcFilter.id -> {
                selectedTextViewFilter.isSelected = false
                binding.textViewMcFilter.isSelected = true
                selectedTextViewFilter = binding.textViewMcFilter
                setChartData(chartData, false)
            }
            else -> {}
        }
    }
}