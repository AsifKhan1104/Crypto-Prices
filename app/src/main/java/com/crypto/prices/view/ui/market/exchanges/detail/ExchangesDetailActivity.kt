package com.crypto.prices.view.ui.market.exchanges.detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityExchangesDetailBinding
import com.crypto.prices.model.ExchangesData
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.chart.CustomMarkerView
import com.crypto.prices.utils.chart.XAxisValueFormatter
import com.crypto.prices.utils.chart.YAxisValueFormatterExchanges
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.ui.search.SearchActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import java.math.BigDecimal

class ExchangesDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityExchangesDetailBinding? = null
    private lateinit var mDetailViewModel: ExchangesDetailViewModel
    private val TAG = "ExchangesDetailActivity"

    private lateinit var chart: LineChart
    private var data: ExchangesData? = null
    private var chartData: ArrayList<ArrayList<BigDecimal>>? = null
    private lateinit var selectedTextViewTimeFilter: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExchangesDetailBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        MyAnalytics.trackScreenViews(javaClass.simpleName, javaClass.simpleName)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // get data from intent
        data = intent?.extras?.getParcelable<ExchangesData>("exchanges_data")!!
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

        binding.textViewSymbol.text = data?.name
        binding.textViewTradeVolume.text =
            "BTC " + String.format("%.2f", data?.trade_volume_24h_btc?.toFloat())
        binding.textViewTsRank.text = "#" + data?.trust_score_rank?.toString()
        binding.textViewTs.text = data?.trust_score?.toString() + "/10"
        binding.textViewYear.text = data?.year_established?.toString()
        binding.textViewHomepage.text = data?.url
        binding.textViewCountry.text = data?.country
        binding.textViewTradingIncentive.text = data?.has_trading_incentive?.toString()
        binding.textViewTradeVolNorm.text =
            "BTC" + data?.trade_volume_24h_btc_normalized?.toString()

        // on click listeners
        binding.textView24hr.setOnClickListener(this)
        binding.textView7d.setOnClickListener(this)
        binding.textView1m.setOnClickListener(this)
        binding.textView3m.setOnClickListener(this)
        binding.textView1y.setOnClickListener(this)

        initChart()
    }

    // intialize chart
    private fun initChart() {
        chart = binding.chart
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
        //chart.setBackgroundColor(Color.rgb(104, 241, 175))

        chart.setNoDataText("Building chart ...");
        // description text
        chart.description.text =
            "volume (BTC) vs time"
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
        y.valueFormatter = YAxisValueFormatterExchanges()
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

    private fun setChartData(cryptoChartData: ArrayList<ArrayList<BigDecimal>>?) {
        chartData = cryptoChartData
        val values = ArrayList<Entry>()

        for (i in 0 until chartData!!.size) {
            try {
                values.add(
                    Entry(
                        chartData?.get(i)?.get(0)!!.toFloat(), // timestamp
                        chartData?.get(i)?.get(1)!!.toFloat() // trade volume
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

    private fun setUpViewModel(data: ExchangesData?) {
        // create map of params needed for api
        val map: MutableMap<String, String> = HashMap()
        map["id"] = data?.id.toString()
        // default day selection
        binding.textView24hr.isSelected = true
        selectedTextViewTimeFilter = binding.textView24hr
        val days = 1
        map["days"] = days.toString()

        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, map)
        mDetailViewModel =
            ViewModelProvider(this, factory).get(ExchangesDetailViewModel::class.java)

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
            mDetailViewModel.exchangesChartLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setChartData(it)
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

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.textView24hr.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView24hr.isSelected = true
                selectedTextViewTimeFilter = binding.textView24hr
                mDetailViewModel.getChart(1.toString())
            }
            binding.textView7d.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView7d.isSelected = true
                selectedTextViewTimeFilter = binding.textView7d
                mDetailViewModel.getChart(7.toString())
            }
            binding.textView1m.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView1m.isSelected = true
                selectedTextViewTimeFilter = binding.textView1m
                mDetailViewModel.getChart(30.toString())
            }
            binding.textView3m.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView3m.isSelected = true
                selectedTextViewTimeFilter = binding.textView3m
                mDetailViewModel.getChart(90.toString())
            }
            binding.textView1y.id -> {
                selectedTextViewTimeFilter.isSelected = false
                binding.textView1y.isSelected = true
                selectedTextViewTimeFilter = binding.textView1y
                mDetailViewModel.getChart(365.toString())
            }
            else -> {}
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}