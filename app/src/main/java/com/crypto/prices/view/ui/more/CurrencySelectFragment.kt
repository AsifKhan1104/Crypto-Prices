package com.crypto.prices.view.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crypto.prices.databinding.FragmentCurrencySelectBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): CurrencySelectFragment = CurrencySelectFragment()
    }
}