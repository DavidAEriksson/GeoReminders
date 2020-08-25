package davidaeriksson.github.io.georeminders.misc;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.database.DatabaseHelper;

/**
 * @author DavidEriksson
 * LocationService.java
 * Handles location services when application is in the background or when application
 * is destroyed by user(exited). Also responsible for sending notification to user when
 * they are close enough(50m) to an activity.
 */
public class LocationService extends Service {

    private ArrayList<ArrayList> activityList = new ArrayList();
    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

    // LocationCallback object used by LocationServices
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult != null && locationResult.getLastLocation() != null) {

                // Get device latitude and longitude from last known location
                LatLng deviceLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                // Check every database entry
                for (int i = 0; i < activityList.size(); i++) {

                    String activityName = (String) activityList.get(i).get(0);

                    float latFromDb = (float) activityList.get(i).get(2);
                    float longFromDb = (float) activityList.get(i).get(3);

                    double activityLat = latFromDb;
                    double activityLong = longFromDb;

                    LatLng activityLatLng = new LatLng(activityLat,activityLong);

                    // Check if device is within range to send notification.
                    if (distanceBetween(deviceLatLng,activityLatLng) < 50) {

                        String channelId = "location_notification_channel";
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intent = new Intent();
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle("You have an activity here!");
                        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                        builder.setContentText(activityName);
                        builder.setContentIntent(pendingIntent);
                        builder.setAutoCancel(false);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);

                                notificationChannel.setDescription("This channel is used by location service.");
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                        }
                        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
                        Log.d(Constants.MapTag, "Less than 50 meters to activity");
                    } else {Log.d(Constants.MapTag, "More than 50 meters to activity");}
                }
            }
        }
    };

    /**
     * Method: distanceBetween
     * Calculates the distance between device and activity
     * @param first - LatLng, device position
     * @param second - LatLng, activity position
     * @return - distance(m)
     */
    public float distanceBetween(LatLng first, LatLng second) {
        float[] distance = new float[1];
        Location.distanceBetween(first.latitude, first.longitude, second.latitude, second.longitude, distance);
        return distance[0];
    }

    /**
     * Method: startLocationService
     */
    @SuppressLint("MissingPermission")
    private void startLocationService() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Method: onStartCommand
     * Populates activity list when application is started and starts location service.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Populate activity list on start.
        for (int i = 0; i < databaseHelper.getRowCount(); i++) {
            activityList.add(databaseHelper.getActivityDataInArrayList(i));
        }

        if (intent != null) {
            startLocationService();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Method onBind
     * Required method from Service extension
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
