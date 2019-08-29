package aaa.weatherapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.Date;

import io.paperdb.Paper;

public class WeatherApiClient {

    public static final String CHART_DATA_STORAGE_KEY = "chartData";
    private final Context context;
    private final String apiKey;
    private static final String OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String LOG_TAG = "Open Weather API";

    public WeatherApiClient(Context context) {
        this.context = context;
        apiKey = BuildConfig.apiKey;
    }

    public void getAndCacheForecast(String cityId, Response.Listener<ChartData> callbackFunction, ErrorHandler errorHandler) {
        boolean shouldGetUpdateFromServer = shouldRefreshFromServer(cityId);
        Log.i(LOG_TAG, "shouldGetUpdateFromServer:" + shouldGetUpdateFromServer);
        if (shouldGetUpdateFromServer) {
            getFromServer(cityId, callbackFunction, errorHandler);
        } else {
            callbackFunction.onResponse(getFromAppStateOrFile());
        }
    }

    private void getFromServer(String cityId, Response.Listener<ChartData> callbackFunction, ErrorHandler errorHandler) {
        String url = getOpenWeatherUrl(cityId);
        RequestQueue queue = Volley.newRequestQueue(context);
        showToast();
        Log.i(LOG_TAG,"Requesting " + url);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                ChartData chartData = ChartData.parse(response);
                chartData.setLastUpdatedToNow();
                AppState.setChartData(chartData);
                Paper.book().write(CHART_DATA_STORAGE_KEY, chartData);
                callbackFunction.onResponse(chartData);
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
                ChartData chartData = getFromAppStateOrFile();
                if (chartData != null) {
                    callbackFunction.onResponse(chartData);
                    return;
                }
                errorString = "Not connected to the internet";
            }
            errorHandler.handleError(errorString);
        }));
    }

    private void showToast() {
        Toast.makeText(this.context, this.context.getString(R.string.loadingWeatherDataFromServer), Toast.LENGTH_SHORT).show();
    }

    private ChartData getFromAppStateOrFile() {
        if (AppState.getChartData() != null) {
            return AppState.getChartData();
        }
        return Paper.book().read(CHART_DATA_STORAGE_KEY);
    }

    private boolean shouldRefreshFromServer(String cityId) {
        ChartData cachedChartData = getFromAppStateOrFile();
        return isLastUpdateTooOld(cachedChartData) || !cityId.equals(cachedChartData.cityId);
    }

    private boolean isLastUpdateTooOld(ChartData cachedChartData) {
        long timeoutMinutes = 15;
        long timeoutMilliseconds = timeoutMinutes * 60 * 1000;
        Date timeoutDate = new Date(System.currentTimeMillis() - timeoutMilliseconds);
        return cachedChartData == null || cachedChartData.getLastUpdated().before(timeoutDate);
    }

    private String getOpenWeatherUrl(String cityId) {
        return OPEN_WEATHER_BASE_URL + "?id=" + cityId + "&units=metric&APPID=" + apiKey;
    }
}
