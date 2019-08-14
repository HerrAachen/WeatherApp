package aaa.weatherapp;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class ChartMarker extends MarkerView {

    private TextView textView;
    public ChartMarker(Context context) {
        super(context, R.layout.chart_popup);
        textView = findViewById(R.id.chartPopupContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        System.out.println("ChartMarker.refreshContent " + e);
        System.out.println(highlight);
        if (highlight.getDataSetIndex() == 0) {
            textView.setText(e.getY() + "°C");
        }
        if (highlight.getDataSetIndex() == 1) {
            textView.setText(e.getY() + "% Cloud Cover");
        }
        if (highlight.getDataSetIndex() == 2) {
            textView.setText(e.getY() + "% Humidity");
        }
        if (highlight.getDataSetIndex() == 3) {
            textView.setText(e.getY() + "mm rain in 3h");
        }

        super.refreshContent(e, highlight);
    }
}
