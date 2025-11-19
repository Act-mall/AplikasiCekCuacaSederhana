package app.cuaca;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.*;

public class WeatherService {
    private final String apiKey;

    public WeatherService(String apiKey) { this.apiKey = apiKey; }

    public WeatherData fetch(String city) throws IOException, JSONException {
        String urlStr = "https://api.openweathermap.org/data/2.5/weather?q="
                + URLEncoder.encode(city, "UTF-8")
                + "&units=metric&lang=id&appid=" + apiKey;

        HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int code = con.getResponseCode();
        InputStream is = (code == 200 ? con.getInputStream() : con.getErrorStream());
        String json = readAll(is);

        if (code != 200) throw new IOException("HTTP " + code + ": " + json);

        JSONObject obj = new JSONObject(json);

        WeatherData d = new WeatherData();
        d.city = obj.optString("name", city);

        JSONObject main = obj.getJSONObject("main");
        d.temp = main.getDouble("temp");
        d.humidity = main.getInt("humidity");

        JSONObject wind = obj.optJSONObject("wind");
        d.wind = (wind != null ? wind.optDouble("speed", 0) : 0);

        JSONArray weatherArr = obj.getJSONArray("weather");
        JSONObject w0 = weatherArr.getJSONObject(0);
        
        d.condition = w0.getString("main");
        d.description = w0.getString("description");

        d.timestamp = obj.getLong("dt");
        d.iconName = WeatherData.mapConditionToIcon(d.condition);
        d.iconCode    = w0.optString("icon", "01d");
        

        return d;
    }

    private static String readAll(InputStream is) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = bis.read(buf)) != -1) bos.write(buf, 0, r);
            return bos.toString(StandardCharsets.UTF_8);
        }
    }
}
