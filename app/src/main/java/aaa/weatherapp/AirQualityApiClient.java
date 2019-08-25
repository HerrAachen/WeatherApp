package aaa.weatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import io.paperdb.Paper;

public class AirQualityApiClient {
    private static final String LOG_TAG = "AQICN API";
    private final Context context;
    private final String apiKey;
    private static final String AQICN_BASE_URL = "https://api.waqi.info/feed/";

    public AirQualityApiClient(Context context) {
        this.context = context;
        this.apiKey = BuildConfig.aqicnApiKey;
    }

    public void getAndCacheAirQualityData(String latitude, String longitude, Response.Listener<AirQualityData> callbackFunction, ErrorHandler errorHandler) {
        getFromServer(latitude, longitude, callbackFunction, errorHandler);
    }

    private void getFromServer(String latitude, String longitude, Response.Listener<AirQualityData> callbackFunction, ErrorHandler errorHandler) {
        String url = getAqicnUrl(latitude, longitude);
        Log.i(LOG_TAG, "Requesting " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                AirQualityData airQualityData = AirQualityData.parse(response);
//                chartData.setLastUpdatedToNow();
//                AppState.setChartData(chartData);
//                Paper.book().write(CHART_DATA_STORAGE_KEY, chartData);
                callbackFunction.onResponse(airQualityData);
            } catch (JSONException e) {
                e.printStackTrace();
                errorHandler.handleError(e.getMessage());
            }
        }, error -> {
            String errorString = error.toString();
            Log.e(LOG_TAG, errorString);
            if (error.networkResponse != null) {
                errorString = new String(error.networkResponse.data);
            }
            if (error instanceof NoConnectionError || error instanceof TimeoutError) {
//                ChartData chartData = getFromAppStateOrFile();
//                if (chartData != null) {
//                    callbackFunction.onResponse(chartData);
//                    return;
//                }
                errorString = "Not connected to the internet";
            }
            errorHandler.handleError(errorString);
        }));
    }

    private String getAqicnUrl(String latitude, String longitude) {
        return AQICN_BASE_URL + "geo:" + latitude + ";" + longitude + "/?token=" + apiKey;
    }
}
