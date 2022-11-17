package com.crypto.prices.view.ui.market

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityCryptoDetailBinding
import com.crypto.prices.model.CryptoChartData
import com.crypto.prices.model.CryptoData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.chart.CustomMarkerView
import com.crypto.prices.utils.chart.XAxisValueFormatter
import com.crypto.prices.utils.chart.YAxisValueFormatter
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter


class CryptoDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityCryptoDetailBinding? = null
    private lateinit var mCryptoDetailViewModel: CryptoDetailViewModel
    private val TAG = "CryptoDetailActivity"

    private lateinit var chart: LineChart
    private lateinit var data: CryptoData
    private lateinit var chartData: CryptoChartData
    private lateinit var selectedTextViewTimeFilter: TextView
    private lateinit var selectedTextViewFilter: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCryptoDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        data = intent?.extras?.getParcelable<CryptoData>("crypto_data")!!
        setUpViewModel(data)
        setMarketStatsData()
    }

    private fun setMarketStatsData() {
        // set title
        setTitle(data?.name)
        // set coin icon
        Glide.with(this)
            .load(data?.image)
            .into(binding.imageViewSymbol)

        binding.textViewSymbol.text = data?.symbol
        binding.textViewPrice.text = "$" + String.format("%.6f", data?.current_price)
        binding.textView24hp.text =
            String.format("%.1f", data?.price_change_percentage_24h) + "%"
        binding.textViewMcr.text = "#" + data?.market_cap_rank?.toString()
        binding.textViewMc.text = "$" + String.format("%.2f", data?.market_cap)
        binding.textViewFdmc.text = "$" + String.format("%.2f", data?.fully_diluted_valuation)
        binding.textViewTotalVol.text = String.format("%.2f", data?.total_volume)
        binding.textViewMaxSupply.text = String.format("%.2f", data?.max_supply)
        binding.textViewCircSupply.text = String.format("%.2f", data?.circulating_supply)
        binding.textViewTotalSupply.text = String.format("%.2f", data?.total_supply)
        binding.textView24h.text = "$" + data?.high_24h?.toString()
        binding.textView24l.text = "$" + data?.low_24h?.toString()
        binding.textViewAth.text = "$" + data?.ath?.toString()
        binding.textViewAthPerc.text = data?.ath_change_percentage?.toString() + "%"
        binding.textViewAtl.text = "$" + data?.atl?.toString()
        binding.textViewAtlPerc.text = data?.atl_change_percentage?.toString() + "%"

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

    // intialize chart
    private fun initChart() {
        chart = binding.chart
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
        //chart.setBackgroundColor(Color.rgb(104, 241, 175))

        chart.setNoDataText("Building chart ...");
        // description text
        chart.description.text = "price ($) vs time"
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

        chart.animateXY(2000, 2000)
        // set marker
        val customMarker = CustomMarkerView(this, R.layout.custom_marker_view)
        chart.marker = customMarker
    }

    private fun setChartData(cryptoChartData: CryptoChartData, priceFilterActive: Boolean) {
        chartData = cryptoChartData
        val values = ArrayList<Entry>()

        var chartFillData: List<List<Double>>
        if (priceFilterActive) {
            chartFillData = cryptoChartData?.prices
        } else {
            chartFillData = cryptoChartData?.market_caps
        }
        for (i in 0 until chartFillData.size) {
            try {
                values.add(
                    Entry(
                        chartFillData.get(i).get(0).toFloat(), // timestamp
                        chartFillData.get(i).get(1).toFloat() // price
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

    private fun setUpViewModel(data: CryptoData?) {
        binding.textViewPriceFilter.isSelected = true
        // create map of params needed for api
        val map: MutableMap<String, String> = HashMap()
        map["symbol"] = data?.id.toString()
        map["currency"] = "usd"
        // default day selection
        binding.textView24hr.isSelected = true
        selectedTextViewTimeFilter = binding.textView24hr
        val days = 1
        map["days"] = days.toString()

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mCryptoDetailViewModel =
            ViewModelProvider(this, factory).get(CryptoDetailViewModel::class.java)

        // load remote data
        loadData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        //binding.loadingView.visibility = View.GONE
        binding.chart.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        //binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.chart.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        //binding.loadingView.visibility = View.GONE
    }

    fun loadData() {
        try {
            mCryptoDetailViewModel.cryptoChartLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setChartData(it, true)
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
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
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

    override fun onClick(view: View?) {
        selectedTextViewTimeFilter.isSelected = false
        when (view?.id) {
            binding.textView1h.id -> {
                selectedTextViewTimeFilter = binding.textView1h
                binding.textView1h.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(0.04.toString())
            }
            binding.textView24hr.id -> {
                selectedTextViewTimeFilter = binding.textView24hr
                binding.textView24hr.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(1.toString())
            }
            binding.textView7d.id -> {
                selectedTextViewTimeFilter = binding.textView7d
                binding.textView7d.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(7.toString())
            }
            binding.textView1m.id -> {
                selectedTextViewTimeFilter = binding.textView1m
                binding.textView1m.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(30.toString())
            }
            binding.textView3m.id -> {
                selectedTextViewTimeFilter = binding.textView3m
                binding.textView3m.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(90.toString())
            }
            binding.textView1y.id -> {
                selectedTextViewTimeFilter = binding.textView1y
                binding.textView1y.isSelected = true
                mCryptoDetailViewModel.getCryptoChart(365.toString())
            }
            binding.textViewAllTime.id -> {
                selectedTextViewTimeFilter = binding.textViewAllTime
                binding.textViewAllTime.isSelected = true
                mCryptoDetailViewModel.getCryptoChart("max")
            }
            binding.textViewPriceFilter.id -> {
                selectedTextViewFilter = binding.textViewPriceFilter
                binding.textViewPriceFilter.isSelected = true
                setChartData(chartData, true)
            }
            binding.textViewMcFilter.id -> {
                selectedTextViewFilter = binding.textViewMcFilter
                binding.textViewMcFilter.isSelected = true
                setChartData(chartData, false)
            }
            else -> {}
        }
    }
}