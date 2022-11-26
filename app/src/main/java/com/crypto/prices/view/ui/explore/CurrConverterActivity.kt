package com.crypto.prices.view.ui.explore

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.databinding.ActivityCurrencyConverterBinding
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.SharedPrefsDataRetrieval
import com.crypto.prices.view.AppRepositoryImpl
import com.crypto.prices.view.ViewModelFactory
import com.crypto.prices.view.ui.search.SearchActivity
import com.google.gson.Gson
import org.json.JSONObject

class CurrConverterActivity : AppCompatActivity(), View.OnClickListener {
    private var _binding: ActivityCurrencyConverterBinding? = null
    private lateinit var mViewModel: CurrConverterViewModel
    private val TAG = "CurrConverterActivity"
    private lateinit var toCurrencySymbol: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        initView()
        setUpViewModel()
    }

    private fun initView() {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        // set title
        setTitle("Currency Converter")

        binding.buttonConvert.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(binding.editText.text.toString())) {
                Toast.makeText(this, "Please enter amount to convert", Toast.LENGTH_LONG).show()
            } else {
                toCurrencySymbol = binding.spinnerTo.selectedItem.toString().uppercase()
                val map = HashMap<String, String>()
                map["amount"] = binding.editText.text.toString()
                map["symbol"] = binding.spinnerFrom.selectedItem.toString()
                map["convert"] = binding.spinnerTo.selectedItem.toString()

                mViewModel.getConvertedCurrData(map)
            }
        })
    }

    private fun setSpinnerData(list: List<String>) {
        // set spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        binding.spinnerFrom.setAdapter(spinnerAdapter); // this will set list of values to spinner
        binding.spinnerFrom.setSelection(0)

        binding.spinnerTo.setAdapter(spinnerAdapter); // this will set list of values to spinner
        binding.spinnerTo.setSelection(1)

        // now save this list in shared prefs
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(
                "supportedCurrencyList",
                Gson().toJson(list)
            )
            apply()
        }
    }

    private fun setDataInView(response: String) {
        val json = JSONObject(response)
        val jsonData = json.getJSONArray("data").get(0) as JSONObject
        val symbol = jsonData.optString("symbol")
        val name = jsonData.optString("name")
        val jsonQuote = jsonData.getJSONObject("quote")
        val price = jsonQuote.getJSONObject(toCurrencySymbol).optDouble("price")

        // add arraylist
        val responseList = ArrayList<String>()
        responseList.add(binding.editText.text.toString())
        responseList.add(symbol.toString())
        responseList.add(name.toString())
        responseList.add(toCurrencySymbol)
        responseList.add(price.toString())

        binding.recyclerViewCurrency.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCurrency.adapter = CurrConverterAdapter(this, responseList)
    }

    private fun setUpViewModel() {
        val repository = AppRepositoryImpl()
        val factory = ViewModelFactory(CryptoApplication.instance!!, repository, HashMap())
        mViewModel =
            ViewModelProvider(this, factory).get(CurrConverterViewModel::class.java)

        // check if supported currency exists in shared prefs
        var supportedCurrencyList = SharedPrefsDataRetrieval().getSupportedCurrData(this)
        // checking below if the array list is empty or not
        if (supportedCurrencyList == null) {
            // if the array list is empty
            // creating a new array list.
            supportedCurrencyList = ArrayList()
        }

        if (supportedCurrencyList == null || supportedCurrencyList.size == 0) {
            // load remote data
            mViewModel.getSupportedCurrData()
            loadData()
        } else {
            setSpinnerData(supportedCurrencyList)
        }

        // load converted data
        loadConvertedCurrData()
    }

    private fun onError(s: String) {
        binding.textViewError.text = s
        binding.textViewError.visibility = View.VISIBLE
        binding.loadingView.visibility = View.GONE
        binding.recyclerViewCurrency.visibility = View.GONE
    }

    private fun onLoading() {
        binding.textViewError.visibility = View.GONE
        binding.recyclerViewCurrency.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    private fun onLoadingFinished() {
        binding.recyclerViewCurrency.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
    }

    fun loadData() {
        try {
            mViewModel.suppCurrLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setSpinnerData(it)
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

    fun loadConvertedCurrData() {
        try {
            mViewModel.convertedLiveData.observe(this, Observer {
                // blank observe here
                when (it) {
                    is NetworkResult.Success -> {
                        it.networkData?.let {
                            //bind the data to the ui
                            onLoadingFinished()
                            setDataInView(it)
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