package com.crypto.prices.view.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.crypto.prices.databinding.FragmentCurrencySelectBinding
import com.crypto.prices.utils.MyAnalytics
import com.crypto.prices.utils.SharedPrefsDataRetrieval
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CurrencySelectFragment : BottomSheetDialogFragment(), CurrencySelectAdapter.ItemClick {
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
        MyAnalytics.trackScreenViews(javaClass.simpleName, requireActivity().javaClass.simpleName)
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
        // sort by type fiat / crypto
        currencyList.sortBy { it.type }

        binding.recyclerViewCurrency.layoutManager =
            LinearLayoutManager(context)
        binding.recyclerViewCurrency.adapter = CurrencySelectAdapter(context, currencyList, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): CurrencySelectFragment = CurrencySelectFragment()
    }

    override fun onItemClicked() {
        // dimiss bottom sheet
        dismiss()
    }
}