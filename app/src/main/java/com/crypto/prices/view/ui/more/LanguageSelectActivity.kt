package com.crypto.prices.view.ui.more

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.databinding.ActivityLanguageBinding
import com.crypto.prices.model.LanguageData
import com.crypto.prices.utils.Localee
import com.crypto.prices.utils.Utility

class LanguageSelectActivity : AppCompatActivity(), LanguageSelectAdapter.ItemClick {
    private var _binding: ActivityLanguageBinding? = null
    private val TAG = "LanguageActivity"
    private var mLanguageList = ArrayList<LanguageData>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        constructLanguageList()
    }

    // create language list
    private fun constructLanguageList() {
        mLanguageList.add(LanguageData("عربى", "ar"))
        mLanguageList.add(LanguageData("Deutsche", "de"))
        mLanguageList.add(LanguageData("English", "en"))
        mLanguageList.add(LanguageData("Español", "es"))
        mLanguageList.add(LanguageData("Français", "fr"))
        mLanguageList.add(LanguageData("ગુજરાતી", "gu"))
        mLanguageList.add(LanguageData("हिंदी", "hi"))
        mLanguageList.add(LanguageData("Magyar", "hu"))
        mLanguageList.add(LanguageData("bahasa Indonesia", "id"))
        mLanguageList.add(LanguageData("Italiano", "it"))
        mLanguageList.add(LanguageData("日本", "ja"))
        mLanguageList.add(LanguageData("ಕನ್ನಡ", "kn"))
        mLanguageList.add(LanguageData("한국인", "ko"))
        mLanguageList.add(LanguageData("Nederlands", "nl"))
        mLanguageList.add(LanguageData("Polski", "pl"))
        mLanguageList.add(LanguageData("Română", "ro"))
        mLanguageList.add(LanguageData("Svenska", "sv"))
        mLanguageList.add(LanguageData("தமிழ்", "ta"))
        mLanguageList.add(LanguageData("తెలుగు", "te"))
        mLanguageList.add(LanguageData("Türkçe", "tr"))
        mLanguageList.add(LanguageData("中国人", "zh"))

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