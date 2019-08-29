package aaa.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AirQualityData {

    private Double aqi;
    private Double pm25;
    private Double pm10;
    private Double so2;
    private Double o3;
    private String cityName;
    private int airQualityStationId;
    private Date lastUpdated;
    private Date measurementDate;
    private String triggeredByOpenWeatherId;


    public static AirQualityData parse(JSONObject response) throws JSONException {
        AirQualityData airQualityData = new AirQualityData();
        JSONObject dataObject = response.getJSONObject("data");
        airQualityData.airQualityStationId = dataObject.getInt("idx");
        airQualityData.aqi = dataObject.getDouble("aqi");
        JSONObject cityObject = dataObject.getJSONObject("city");
        airQualityData.cityName = cityObject.getString("name");
        JSONObject iaqiObject = dataObject.getJSONObject("iaqi");
        airQualityData.pm10 = getIaqiValue("pm10", iaqiObject);
        airQualityData.pm25 = getIaqiValue("pm25", iaqiObject);
        airQualityData.so2 = getIaqiValue("so2", iaqiObject);
        airQualityData.o3 = getIaqiValue("o3", iaqiObject);
        airQualityData.measurementDate = getDate(dataObject.getJSONObject("time").getLong("v"));
        return airQualityData;
    }

    private static Date getDate(long epochSeconds) {
        return new Date(epochSeconds * 1000);
    }

    private static Double getIaqiValue(String key, JSONObject iaqiObject) throws JSONException {
        if (iaqiObject.has(key)) {
            return iaqiObject.getJSONObject(key).getDouble("v");
        }
        return null;
    }

    public Double getPm25() {
        return pm25;
    }
    public Double getPm10() {
        return pm10;
    }
    public Double getAqi() {
        return aqi;
    }
    public Double getSo2() {
        return so2;
    }
    public Double getO3() {
        return o3;
    }
    public int getAirQualityStationId() {
        return airQualityStationId;
    }

    public String getCityName() {
        return cityName;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
    public Date getMeasurementDate() {
        return measurementDate;
    }
    public String getTriggeredByOpenWeatherId() {
        return triggeredByOpenWeatherId;
    }

    public void setLastUpdatedToNow() {
        lastUpdated = new Date();
    }
    public void setTriggeredByOpenWeatherId(String openWeatherId) {
        this.triggeredByOpenWeatherId = openWeatherId;
    }
}
