import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityFileConverter {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("openweathermap.city.list.json");
             FileWriter writer = new FileWriter("..\\app\\src\\main\\res\\raw\\cities.properties")) {
            Map<Long, String> id2CityName = new HashMap<>();
            Map<String, Integer> cityNameToCount = new HashMap<>();
            JSONArray cities = (JSONArray) jsonParser.parse(reader);
            cities.forEach(cityJson -> {
                JSONObject cityJsonObject = (JSONObject) cityJson;
                String cityName = (String) cityJsonObject.get("name");
                String country = (String) cityJsonObject.get("country");
                long cityId = (long) cityJsonObject.get("id");
                String lat = getNumberAsString(((JSONObject) cityJsonObject.get("coord")).get("lat"));
                String longitude = getNumberAsString(((JSONObject) cityJsonObject.get("coord")).get("lon"));
                updateCityCount(cityNameToCount, getCityDisplayName(cityName, country));
                id2CityName.put(cityId, getCityDisplayName(cityName, country));
                try {
                    Integer count = cityNameToCount.get(getCityDisplayName(cityName, country));
                    String cityNameForFile = cityName + (count > 1 ? "_" + count : "");
                    writer.write(cityId + ";" + cityNameForFile + ";" + country + ";" + lat + ";" + longitude + "\r\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void updateCityCount(Map<String, Integer> cityNameToCount, String cityDisplayName) {
        Integer cityCount = cityNameToCount.get(cityDisplayName);
        if (cityCount == null) {
            cityCount = 0;
        }
        cityCount++;
        cityNameToCount.put(cityDisplayName, cityCount);
    }

    private static String getCityDisplayName(String cityName, String cityCountry) {
        return cityName + ", " + cityCountry;
    }

    private static String getNumberAsString(Object numberObject) {
        return String.valueOf(numberObject);
    }
}
