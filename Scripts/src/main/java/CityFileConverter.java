import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
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
                String cityName = (String) ((JSONObject) cityJson).get("name");
                String cityCountry = (String) ((JSONObject) cityJson).get("country");
                long cityId = (long) ((JSONObject) cityJson).get("id");
                if (uniqueCities.add(cityName + ", " + cityCountry)) {
                    id2CityName.put(cityId, cityName + ", " + cityCountry);
                    try {
                        writer.write(cityId + "=" + cityName + ", " + cityCountry + "\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } );

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
