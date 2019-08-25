package aaa.weatherapp;

import android.content.Context;
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
//    private ChartFragment.OnFragmentInteractionListener mListener;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChartFragment.OnFragmentInteractionListener) {
//            mListener = (ChartFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    public void refreshView() {
//        showLoadingScreen();
        airQualityApiClient.getAndCacheAirQualityData(AppState.getLatitude(), AppState.getLongitude(), airQualityData -> {
            try {

                ((TextView) getView().findViewById(R.id.pm25Value)).setText(String.valueOf(airQualityData.getPm25()));
                ((TextView) getView().findViewById(R.id.airQualityLocation)).setText(String.valueOf(airQualityData.getCityName()));
            } catch (Exception e) {
//                showError(e.getMessage());
                e.printStackTrace();
            }
        }, errorMessage -> {});
    }

}
