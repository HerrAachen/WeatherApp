package aaa.weatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
//        showLoadingScreen();
        airQualityApiClient.getAndCacheAirQualityData(AppState.getLatitude(), AppState.getLongitude(), airQualityData -> {
            try {

                ((TextView) getView().findViewById(R.id.aqiValue)).setText(getStringValue(airQualityData.getAqi()));
                ((TextView) getView().findViewById(R.id.pm10Value)).setText(getStringValue(airQualityData.getPm10()));
                ((TextView) getView().findViewById(R.id.pm25Value)).setText(getStringValue(airQualityData.getPm25()));
                ((TextView) getView().findViewById(R.id.so2Value)).setText(getStringValue(airQualityData.getSo2()));
                ((TextView) getView().findViewById(R.id.ozoneValue)).setText(getStringValue(airQualityData.getO3()));
                ((TextView) getView().findViewById(R.id.airQualityLocation)).setText(airQualityData.getCityName());
            } catch (Exception e) {
//                showError(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> {});
    }

    private String getStringValue(Double number) {
        if (number == null) {
            return getString(R.string.noData);
        }
        return String.valueOf(number);
    }

}
