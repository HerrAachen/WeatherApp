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
             FileWriter writer = new FileWriter("..\\app\\src\\main\\res\\raw\\cities.properties"))
        {
            Set<String> uniqueCities = new HashSet<>();
            Map<Long, String> id2CityName = new HashMap<>();
            JSONArray cities = (JSONArray) jsonParser.parse(reader);
            cities.forEach( cityJson -> {
                JSONObject cityJsonObject = (JSONObject) cityJson;
                String cityName = (String) cityJsonObject.get("name");
                String cityCountry = (String) cityJsonObject.get("country");
                long cityId = (long) cityJsonObject.get("id");
                String lat = getNumberAsString(((JSONObject)cityJsonObject.get("coord")).get("lat"));
                String longitude = getNumberAsString(((JSONObject)cityJsonObject.get("coord")).get("lon"));
                if (uniqueCities.add(cityName + ", " + cityCountry)) {
                    id2CityName.put(cityId, cityName + ", " + cityCountry);
                    try {
                        writer.write(cityId + ";" + cityName + ";" + cityCountry + ";" + lat + ";" + longitude + "\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } );

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String getNumberAsString(Object numberObject) {
        return String.valueOf(numberObject);
    }
}
