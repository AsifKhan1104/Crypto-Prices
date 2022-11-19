package com.crypto.prices.view.ui.more

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.databinding.FragmentCurrencySelectBinding
import com.crypto.prices.utils.CurrencyData
import com.crypto.prices.utils.SharedPrefsDataRetrieval
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CurrencySelectFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCurrencySelectBinding? = null
    private val TAG = CurrencySelectFragment.javaClass.simpleName

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencySelectBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showCurrencyList()
        return root
    }

    private fun showCurrencyList() {
        // get currency list from shared prefs
        var currencyList = SharedPrefsDataRetrieval().getData(requireActivity())
        // checking below if the array list is empty or not
        if (currencyList == null) {
            // if the array list is empty
            // creating a new array list.
            currencyList = ArrayList()
        }
        binding.recyclerViewCurrency.layoutManager =
            LinearLayoutManager(context)
        binding.recyclerViewCurrency.adapter = CurrencySelectAdapter(context, currencyList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): CurrencySelectFragment = CurrencySelectFragment()
    }
}