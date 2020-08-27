package davidaeriksson.github.io.georeminders.database;

import android.net.Uri;

import davidaeriksson.github.io.georeminders.misc.Constants;

/**
 * @author David Eriksson
 * DatabaseConstants.java
 */
public class DatabaseConstants {
    public static final String DB_NAME = "activity_database";
    public static final String DB_TABLE_ACTIVITY = "activity_table";
    public static final Uri DB_TABLE_ACTIVITY_URI = Uri
            .parse("sqlite://" + Constants.ApplicationPackage + "/" + DB_TABLE_ACTIVITY);

    // Activity table columns
    public static final String COL_ID = "id";
    public static final String COL_ACTIVITY_NAME = "activity_name";
    public static final String COL_ACTIVITY_DATE = "activity_date";
    public static final String COL_ACTIVITY_LAT = "activity_lat";
    public static final String COL_ACTIVITY_LONG = "activity_long";
}
