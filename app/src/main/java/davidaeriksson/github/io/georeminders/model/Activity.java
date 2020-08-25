package davidaeriksson.github.io.georeminders.model;

/**
 * @author David Eriksson
 * Activity.java
 * Model for activity object.
 */
public class Activity {
    public String name;
    public String date;
    public double latitude;
    public double longitude;

    /**
     * Constructor: Activity
     * @param name
     * @param date
     * @param latitude
     * @param longitude
     */
    public Activity(String name, String date, double latitude, double longitude) {
        this.name = name;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
