package com.crypto.prices.utils.chart;

import android.content.Context;
import android.widget.TextView;

import com.crypto.prices.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomMarkerView extends MarkerView {

    private TextView textViewEntry;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        textViewEntry = (TextView) findViewById(R.id.textViewEntry);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // format date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String dateValue = String.format("%.0f", e.getX());
        Date date = new Date(Long.valueOf(dateValue));
        String formattedDate = sdf.format(date);
        // set the entry-value as the display text
        textViewEntry.setText("$" + e.getY() + "\n" + formattedDate);
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
