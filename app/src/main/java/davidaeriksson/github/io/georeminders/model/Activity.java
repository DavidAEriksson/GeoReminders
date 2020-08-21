package davidaeriksson.github.io.georeminders.model;

public class Activity {
    public String name;
    public String date;
    public double latitude;
    public double longitude;

    public Activity(String name, String date, double latitude, double longitude) {
        this.name = name;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
