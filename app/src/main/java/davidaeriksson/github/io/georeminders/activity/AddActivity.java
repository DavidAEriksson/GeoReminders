package davidaeriksson.github.io.georeminders.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.database.DatabaseHelper;
import davidaeriksson.github.io.georeminders.misc.Constants;
import davidaeriksson.github.io.georeminders.model.Activity;

/**
 * @author David Eriksson
 * AddActivity.java
 * Class responsible for handling additions to database and add_activity.xml
 */
public class AddActivity extends AppCompatActivity {

    private TextInputLayout activityField;
    private DatePicker datePicker;
    private Button addActivityButton;

    // Location variables used by AddActivity.java to add a location to database entry for
    // any given activity.
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private double activityLocationLat;
    private double activityLocationLong;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        activityField = findViewById(R.id.activity_field);
        datePicker = findViewById(R.id.date_picker);
        addActivityButton = findViewById(R.id.add_activity_button);

        // Set fused location provider client on this context.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(120000); // Standard location request timer set to 2 minutes.
        locationRequest.setFastestInterval(1000); // Set request timer to 2 seconds if we can get request earlier.
        // Set priority to location request to high accuracy.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Request location updates on the request and callback.
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        handleOnClickButton();
    }

    /**
     * Method: handleOnClickButton
     * Fetches user made input from view as well as the current location of the device and adds
     * this to database entry.
     */
    private void handleOnClickButton() {
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = String.valueOf(activityField.getEditText().getText());

                // This creates a string from the date picker object before adding it to the database.
                StringBuilder sb = new StringBuilder();
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                sb.append(day);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(year);
                String activityDate = sb.toString();

                // Checks if the user has given the activity a name before adding it to the database.
                // If empty, send user toast.
                if (!activityName.isEmpty()) {
                    try {
                        Activity activity = new Activity(activityName, activityDate, activityLocationLat, activityLocationLong);
                        DatabaseHelper.getInstance(AddActivity.this).insertActivityToDb(activity);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AddActivity.this, R.string.add_new_failed, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddActivity.this, R.string.toast_make_null_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Creates the location callback needed for the location provider.
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                lastLocation = location;
                activityLocationLat = lastLocation.getLatitude();
                activityLocationLong = lastLocation.getLongitude();
            }
        }
    };

    /**
     * Method: onPause
     * If application is broken by user when activity this stops the fusedLocationProviderClient
     * from calling locationCallback as it wont be needed at this time.
     */
    @Override
    public void onPause() {
        super.onPause();
        if(fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

}
