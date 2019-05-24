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
import org.json.JSONObject;

import io.paperdb.Paper;

public class WeatherApiClient {

    public static final String CHART_DATA_STORAGE_KEY = "chartData";
    private final Context context;
    private final String apiKey;
    private static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";

    public WeatherApiClient(Context context) {
        this.context = context;
        apiKey = BuildConfig.apiKey;
    }

    public void getAndCacheForecast(String cityId, Response.Listener<ChartData> callbackFunction, ErrorHandler errorHandler) {
        String url = getOpenWeatherUrl(cityId);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                ChartData chartData = ChartData.parse(response);
                chartData.setLastUpdatedToNow();
                AppState.setChartData(chartData);
                Paper.book().write(CHART_DATA_STORAGE_KEY, chartData);
                callbackFunction.onResponse(chartData);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, error -> {
            String errorString = error.toString();
            if (error.networkResponse != null) {
                errorString = new String(error.networkResponse.data);
            }
            if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                if (AppState.getChartData() != null) {
                    callbackFunction.onResponse(AppState.getChartData());
                    return;
                } else {
                    ChartData chartData = Paper.book().read(CHART_DATA_STORAGE_KEY);
                    if (chartData != null) {
                        callbackFunction.onResponse(chartData);
                        return;
                    }
                }
                errorString = "Not connected to the internet";
            }
            Log.e("Open Weather API Error", errorString);
            errorHandler.handleError(errorString);
        }));
    }

    private String getOpenWeatherUrl(String cityId) {
        return OPEN_WEATHER_BASE_URL + "?id=" + cityId + "&units=metric&APPID=" + apiKey;
    }
}
