package app.cuaca;

public class WeatherData {
    public String city;
    public double temp;
    public String condition;
    public String description;
    public int humidity;
    public double wind;
    public long timestamp;
    public String iconName;

    public Object[] toRow() {
        String waktu = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp * 1000L));
        return new Object[] {
            waktu, city,
            String.format("%.1f", temp),
            condition, humidity,
            String.format("%.1f", wind),
            description
        };
    }

    public static String mapConditionToIcon(String main) {
        String m = (main == null ? "" : main.toLowerCase());
        if (m.contains("clear")) return "cerah";
        if (m.contains("cloud")) return "berawan";
        if (m.contains("rain") || m.contains("drizzle")) return "hujan";
        if (m.contains("thunder")) return "badai";
        if (m.contains("snow")) return "salju";
        if (m.contains("mist") || m.contains("haze") || m.contains("fog")) return "kabut";
        return "lainnya";
    }
}
