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

public class AddActivity extends AppCompatActivity {

    private TextInputLayout activityField;
    private DatePicker datePicker;
    private Button addActivityButton;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private double activityLocationLat;
    private double activityLocationLong;

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
        // Create a new location request on a 10-second interval.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        // Set priority to location request.
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Request location updates on the request and callback.
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        handleOnClickButton();
    }

    private void handleOnClickButton() {
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = String.valueOf(activityField.getEditText().getText());

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

                Log.d(Constants.AddActivity, "Created new Activity: " + activityName + " activity date: " + activityDate + "Latitude: " + activityLocationLat + " Longitude: " + activityLocationLong);

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

    @Override
    public void onPause() {
        super.onPause();
        if(fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(Constants.AddActivity,"Should log when activity is swiped to.");
    }
}
