package com.crypto.prices.utils.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class XAxisValueFormatter() : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        /*
        Depends on the position number on the X axis, we need to display the label, Here,
        this is the logic to convert the float value to integer so that I can get the value
        from array based on that integer and can convert it to the required value here, month
        and date as value. This is required for my data to show properly, you can customize
        according to your needs.
        */
        val sdf = SimpleDateFormat("MMM dd")
        val date = Date(Math.round(value).toLong())
        return sdf.format(date)
    }
}