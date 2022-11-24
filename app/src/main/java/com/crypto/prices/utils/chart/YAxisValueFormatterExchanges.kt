package com.crypto.prices.utils.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class YAxisValueFormatterExchanges : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return "BTC " + value.toString()
    }
}