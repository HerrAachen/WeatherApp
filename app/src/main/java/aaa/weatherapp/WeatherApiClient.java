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
    public WeatherApiClient(Context context) {
        this.context = context;
    }

    public void callFiveDayForecast(Response.Listener<JSONObject> callbackFunction) {
        int resultLimit = 12;
        String url = "http://api.openweathermap.org/data/2.5/forecast?id=6173331&units=metric&APPID=-appid-&cnt=" + resultLimit;
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, callbackFunction, error -> {
            Log.e("Open Weather API", error.getMessage());
        }));

    }
}
