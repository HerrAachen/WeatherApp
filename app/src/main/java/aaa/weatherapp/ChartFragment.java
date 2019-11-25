package aaa.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ChartFragment extends Fragment {

    private WeatherApiClient weatherApiClient;
    private ChartView viewToShow;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VIEW_TO_SHOW_PARAM = "viewToShow";


    private OnFragmentInteractionListener mListener;

    public ChartFragment() {

    }

    public static ChartFragment newInstance(ChartView viewToShow) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(VIEW_TO_SHOW_PARAM, viewToShow.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewToShow = ChartView.valueOf(getArguments().getString(VIEW_TO_SHOW_PARAM));
        weatherApiClient = new WeatherApiClient(getActivity().getApplicationContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshWeatherView(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateChart(ChartData chartData) {
        LineChart chart = getView().findViewById(R.id.temperatureChart);
        LineDataSet temperatureDataSet = createLineDataSet(chartData.getTemperatureEntries(), "Temperature °C", Color.GREEN, 5, YAxis.AxisDependency.LEFT);
        LineDataSet cloudCoverDataSet = createLineDataSet(chartData.getCloudCoverEntries(), "Cloud Cover %", Color.GRAY, 2, YAxis.AxisDependency.LEFT);
        LineDataSet humidityDataSet = createLineDataSet(chartData.getHumidities(), "Humidity %", Color.MAGENTA, 2, YAxis.AxisDependency.LEFT);
        LineDataSet rainDataSet = createLineDataSet(chartData.getRainEntries(), "Rain 3h mm", Color.BLUE, 4, YAxis.AxisDependency.RIGHT);
        chart.setData(new LineData(temperatureDataSet, cloudCoverDataSet, humidityDataSet, rainDataSet));
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float epochSeconds) {
                return getWeekday((long) epochSeconds);
            }
        });
        chart.getXAxis().setLabelRotationAngle(-15);
        configureRainAxis(chartData, chart.getAxisRight());
        configureTemperatureAxis(chartData, chart.getAxisLeft());
        chart.setDescription(null);
        chart.setMarker(new ChartMarker(this.getContext(), chartData));
        chart.invalidate();
    }

    private void configureRainAxis(ChartData chartData, YAxis yAxis) {
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum((float) Math.max(4, chartData.getMaxRainValue()));
        yAxis.setGranularity(2f);
    }

    private void configureTemperatureAxis(ChartData chartData, YAxis yAxis) {
        yAxis.setAxisMinimum((float) Math.min(0, chartData.getMinTemperatureValue()));
        yAxis.setAxisMaximum(105);
        yAxis.setGranularity(20f);
    }

    private LineDataSet createLineDataSet(List<Entry> entries, String title, int color, int circleRadius, YAxis.AxisDependency axis) {
        LineDataSet temperatureDataSet = new LineDataSet(entries, title);
        temperatureDataSet.setColor(color);
        temperatureDataSet.setCircleRadius(circleRadius);
        temperatureDataSet.setCircleColor(color);
        temperatureDataSet.setAxisDependency(axis);
        return temperatureDataSet;
    }

    private String getWeekday(long epochSeconds) {
        long epochMilliseconds = epochSeconds * 1000;
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE ha");
        return weekdayFormat.format(new Date(epochMilliseconds));
    }

    public void refreshWeatherView(MenuItem item) {
        showLoadingScreen();
        weatherApiClient.getAndCacheForecast(AppState.getCityId(), fullChartData -> {
            try {
                ChartData chartData;
                if (viewToShow == ChartView.DAY) {
                    chartData = fullChartData.getSubSet(9);
                } else {
                    chartData = fullChartData;
                }

                updateChart(chartData);
                showChart(chartData);
                showLastUpdatedLabel(chartData);
                setCityName(chartData);
            } catch (Exception e) {
                showError(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> showError("Error: " + errorMessage));
    }

    private void setCityName(ChartData chartData) {
        getActivity().setTitle(chartData.cityName + " (" + chartData.country + ")");
    }

    private void showLastUpdatedLabel(ChartData chartData) {
        getView().findViewById(R.id.lastUpdated).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.lastUpdatedDateTime).setVisibility(View.VISIBLE);
        ((TextView) getView().findViewById(R.id.lastUpdatedDateTime)).setText(Constants.lastUpdatedDateFormat.format(chartData.getLastUpdated()));
    }

    private void showChart(ChartData chartData) {
        getView().findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.GONE);
        getView().findViewById(R.id.temperatureChart).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.errorText).setVisibility(View.GONE);
        showLastUpdatedLabel(chartData);
    }

    private void showLoadingScreen() {
        getView().findViewById(R.id.mainActivityLoadingIcon).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.temperatureChart).setVisibility(View.GONE);
        getView().findViewById(R.id.errorText).setVisibility(View.GONE);
        hideLastUpdatedLabel();
    }

    private void showError(String errorText) {
        Toast.makeText(getContext(), errorText, Toast.LENGTH_LONG).show();
    }

    private void hideLastUpdatedLabel() {
        getView().findViewById(R.id.lastUpdated).setVisibility(View.GONE);
        getView().findViewById(R.id.lastUpdatedDateTime).setVisibility(View.GONE);
    }
}
