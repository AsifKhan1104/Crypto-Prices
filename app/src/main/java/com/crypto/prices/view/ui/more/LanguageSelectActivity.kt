package com.crypto.prices.view.ui.more

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.databinding.ActivityLanguageBinding
import com.crypto.prices.model.LanguageData
import com.crypto.prices.utils.Localee
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.Utility

class LanguageSelectActivity : AppCompatActivity(), LanguageSelectAdapter.ItemClick {
    private var _binding: ActivityLanguageBinding? = null
    private val TAG = "LanguageActivity"
    private var mLanguageList = Localee.fetchLanguageList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        MyAnalytics.trackScreenViews(javaClass.simpleName, javaClass.simpleName)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LanguageSelectAdapter(this, mLanguageList, this)
        binding.recyclerView.adapter = adapter
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

    override fun onItemClicked(position: Int) {
        Localee.setLocale(this, mLanguageList[position].locale)
        Utility.restartApp(this)
    }
}