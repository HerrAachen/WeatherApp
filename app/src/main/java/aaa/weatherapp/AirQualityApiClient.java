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

public class AirQualityApiClient {
    private static final String LOG_TAG = "AQICN API";
    private final Context context;
    private final String apiKey;
    private static final String AQICN_BASE_URL = "https://api.waqi.info/feed/";
    private static final String AIR_QUALITY_DATA_STORAGE_KEY = "AIR_QUALITY_DATA_STORAGE_KEY";

    public AirQualityApiClient(Context context) {
        this.context = context;
        this.apiKey = BuildConfig.aqicnApiKey;
    }

    public void getAndCacheAirQualityData(String latitude, String longitude, Response.Listener<AirQualityData> callbackFunction, ErrorHandler errorHandler) {
        if (shouldRefreshFromServer()) {
            getFromServer(latitude, longitude, callbackFunction, errorHandler);
        } else {
            callbackFunction.onResponse(getFromAppStateOrFile());
        }
    }

    private void getFromServer(String latitude, String longitude, Response.Listener<AirQualityData> callbackFunction, ErrorHandler errorHandler) {
        String url = getAqicnUrl(latitude, longitude);
        Log.i(LOG_TAG, "Requesting " + url);
        showNotification();
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                AirQualityData airQualityData = AirQualityData.parse(response);
                airQualityData.setLastUpdatedToNow();
                airQualityData.setTriggeredByOpenWeatherId(AppState.getCityId());
                AppState.setAirQualityData(airQualityData);
                Paper.book().write(AIR_QUALITY_DATA_STORAGE_KEY, airQualityData);
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
                AirQualityData airQualityData = getFromAppStateOrFile();
                if (airQualityData != null) {
                    callbackFunction.onResponse(airQualityData);
                    return;
                }
                errorString = "Not connected to the internet";
            }
            errorHandler.handleError(errorString);
        }));
    }

    private void showNotification() {
        Toast.makeText(this.context, this.context.getString(R.string.loadingAirQualityDataFromServer), Toast.LENGTH_SHORT).show();
    }

    private String getAqicnUrl(String latitude, String longitude) {
        return AQICN_BASE_URL + "geo:" + latitude + ";" + longitude + "/?token=" + apiKey;
    }

    private AirQualityData getFromAppStateOrFile() {
        if (AppState.getAirQualityData() != null) {
            return AppState.getAirQualityData();
        }
        return Paper.book().read(AIR_QUALITY_DATA_STORAGE_KEY);
    }

    private boolean shouldRefreshFromServer() {
        AirQualityData cachedData = getFromAppStateOrFile();
        return isLastUpdateTooOld(cachedData) || cachedData.getTriggeredByOpenWeatherId() != AppState.getCityId();
    }

    private boolean isLastUpdateTooOld(AirQualityData cachedData) {
        long timeoutMinutes = 15;
        long timeoutMilliseconds = timeoutMinutes * 60 * 1000;
        Date timeoutDate = new Date(System.currentTimeMillis() - timeoutMilliseconds);
        return cachedData == null || cachedData.getLastUpdated().before(timeoutDate);
    }
}
