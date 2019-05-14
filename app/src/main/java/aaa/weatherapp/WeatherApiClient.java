package aaa.weatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class WeatherApiClient {

    private final Context context;
    private final String apiKey;
    private static final String OPEN_WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";

    public WeatherApiClient(Context context) {
        this.context = context;
        apiKey = BuildConfig.apiKey;
    }

    public void getOneDayForecast(String cityId, Response.Listener<JSONObject> callbackFunction) {
        int resultLimit = 12;
        String url = getOpenWeatherUrl(cityId, resultLimit);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, callbackFunction, error -> {
            String errorString = error.toString();
            if (error.networkResponse != null) {
                errorString = new String(error.networkResponse.data);
            }
            Log.e("Open Weather API Error", errorString);
        }));
    }

    private String getOpenWeatherUrl(String cityId, int resultLimit) {
        return OPEN_WEATHER_BASE_URL + "?id=" + cityId + "&units=metric&APPID=" + apiKey + "&cnt=" + resultLimit;
    }
}
