package aaa.weatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class AirQualityFragment extends Fragment {
    private AirQualityApiClient airQualityApiClient;
    public AirQualityFragment() {

    }

    public static AirQualityFragment newInstance() {
        return new AirQualityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        airQualityApiClient = new AirQualityApiClient(getActivity().getApplicationContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_air_quality, container, false);
    }

    public void refreshView() {
        airQualityApiClient.getAndCacheAirQualityData(AppState.getLatitude(), AppState.getLongitude(), airQualityData -> {
            try {

                ((TextView) getView().findViewById(R.id.aqiValue)).setText(getStringValue(airQualityData.getAqi()));
                ((TextView) getView().findViewById(R.id.pm10Value)).setText(getStringValue(airQualityData.getPm10()));
                ((TextView) getView().findViewById(R.id.pm25Value)).setText(getStringValue(airQualityData.getPm25()));
                ((TextView) getView().findViewById(R.id.so2Value)).setText(getStringValue(airQualityData.getSo2()));
                ((TextView) getView().findViewById(R.id.ozoneValue)).setText(getStringValue(airQualityData.getO3()));
                ((TextView) getView().findViewById(R.id.carbonMonoxideValue)).setText(getStringValue(airQualityData.getCo()));
                ((TextView) getView().findViewById(R.id.nitrogenDioxideValue)).setText(getStringValue(airQualityData.getNo2()));
                ((TextView) getView().findViewById(R.id.airQualityLocation)).setText(airQualityData.getCityName());
                ((TextView) getView().findViewById(R.id.airQualityLastUpdateValue)).setText(Constants.lastUpdatedDateFormat.format(airQualityData.getLastUpdated()));
                ((TextView) getView().findViewById(R.id.measurementDateString)).setText(new SimpleDateFormat("YYYY-MM-dd hh:mm a").format(airQualityData.getMeasurementDate()));
                ((TextView) getView().findViewById(R.id.aqiIndexLevel)).setText(airQualityData.getIndexLevelShortText());
                getView().findViewById(R.id.aqiIndexLevel).setBackgroundColor(getResources().getColor(airQualityData.getIndexLevelColor()));
                addAttributions(airQualityData);

            } catch (Exception e) {
                showErrorToast(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> showErrorToast(errorMessage));
    }

    private void addAttributions(AirQualityData airQualityData) {
        TextView firstAttribution = getView().findViewById(R.id.firstAttribution);
        firstAttribution.setText(Html.fromHtml("<a href='" + airQualityData.getAttributions().get(0).getUrl() + "'>" + airQualityData.getAttributions().get(0).getName() + "</a>"));
        firstAttribution.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showErrorToast(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private String getStringValue(Double number) {
        if (number == null) {
            return getString(R.string.noData);
        }
        return String.valueOf(number);
    }

}
