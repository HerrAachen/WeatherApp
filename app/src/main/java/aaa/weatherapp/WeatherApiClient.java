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

    public void getOneDayForecast(Response.Listener<JSONObject> callbackFunction) {
        int resultLimit = 12;
        String url = getOpenWeatherUrl(resultLimit);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, callbackFunction, error -> {
            Log.e("Open Weather API", error.getMessage());
        }));
    }

    private String getOpenWeatherUrl(int resultLimit) {
        return OPEN_WEATHER_BASE_URL + "?id=6173331&units=metric&APPID=" + apiKey + "&cnt=" + resultLimit;
    }
}
