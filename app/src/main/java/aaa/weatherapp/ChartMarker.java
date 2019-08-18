package aaa.weatherapp;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChartMarker extends MarkerView {

    private static SimpleDateFormat popupDateFormat = new SimpleDateFormat("E hh:mm a");

    private TextView textView;
    public ChartMarker(Context context) {
        super(context, R.layout.chart_popup);
        textView = findViewById(R.id.chartPopupContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        System.out.println("ChartMarker.refreshContent " + e);
        System.out.println("Data " + e.getData());
        System.out.println(highlight);
        String text = "";
        text += popupDateFormat.format(new Date((long)e.getX() * 1000)) + "\n";
        if (highlight.getDataSetIndex() == 0) {
            text += e.getY() + "°C";
        }
        if (highlight.getDataSetIndex() == 1) {
            text += e.getY() + "% Cloud Cover";
        }
        if (highlight.getDataSetIndex() == 2) {
            text += e.getY() + "% Humidity";
        }
        if (highlight.getDataSetIndex() == 3) {
            text += e.getY() + "mm rain in 3h";
        }
        textView.setText(text);

        super.refreshContent(e, highlight);
    }
}
