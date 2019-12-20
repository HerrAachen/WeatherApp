package aaa.weatherapp;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChartMarker extends MarkerView {

    private static SimpleDateFormat popupDateFormat = new SimpleDateFormat("E hh:mm a");

    private TextView textView;
    private ChartData chartData;
    public ChartMarker(Context context, ChartData chartData) {
        super(context, R.layout.chart_popup);
        this.chartData = chartData;
        textView = findViewById(R.id.chartPopupContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        System.out.println("ChartMarker.refreshContent " + e);
        System.out.println("Data " + e.getData());
        System.out.println(highlight);
        String text = "";
        int dataIndex = chartData.getIndex((int) e.getX());
        text += popupDateFormat.format(new Date((long)e.getX() * 1000)) + "\n";
        text += chartData.temperatures.get(dataIndex) + "°C\n";
        text += Math.round(chartData.cloudCoverValues.get(dataIndex)) + "% Cloud Cover\n";
        text += Math.round(chartData.humidity.get(dataIndex)) + "% Humidity\n";
        text += roundToOneDecimal(chartData.rainValues.get(dataIndex)) + "mm Rain (in 3h)\n";
        text += roundToOneDecimal(chartData.snowfallValues.get(dataIndex)) + "cm Snow (in 3h)\n";
        text += roundToOneDecimal(chartData.pressureValues.get(dataIndex)) + "hPa\n";
        textView.setText(text);

        super.refreshContent(e, highlight);
    }

    private String roundToOneDecimal(Double aDouble) {
        DecimalFormat oneDecimalFormat = new DecimalFormat("#.#");
        return oneDecimalFormat.format(aDouble);
    }
}
