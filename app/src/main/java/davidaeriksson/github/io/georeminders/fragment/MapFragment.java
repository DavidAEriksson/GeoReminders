package davidaeriksson.github.io.georeminders.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.database.DatabaseHelper;
import davidaeriksson.github.io.georeminders.misc.Constants;

/**
 * @author David Eriksson
 * MapFragment.java
 * Handles the "Map" tab which displays the users position and all of the saved activities.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private DatabaseHelper databaseHelper;

    Context mapContext;

    private GoogleMap googleMap;
    private MapView mapView;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private ArrayList<ArrayList> activityList;

    /**
     * Constructor: MapFragment
     */
    public MapFragment() {
        // Required empty public constructor
    }


    /**
     * Method: onCreateView
     * Creates the View mapFragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return mapFragment - this View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mapFragment = inflater.inflate(R.layout.fragment_map, container, false);
        mapContext = mapFragment.getContext();
        databaseHelper = DatabaseHelper.getInstance(mapContext);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mapContext);

        mapView = mapFragment.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        activityList = new ArrayList();
        // Populate activity list from database when view is created.
        for (int i = 0; i < databaseHelper.getRowCount(); i++) {
            activityList.add(databaseHelper.getActivityDataInArrayList(i));
        }
        return mapFragment;
    }

    /**
     * Method: onMapReady
     * Callback for onMapReadyCallback which this fragment implements.
     * @param gm
     */
    @Override
    public void onMapReady(GoogleMap gm) {
        googleMap = gm;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Set the map style as defined in style_json.json.
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mapContext, R.raw.style_json));

        locationRequest = new LocationRequest();
        locationRequest.setInterval(120000); // Standard location request timer set to 2 minutes.
        locationRequest.setFastestInterval(2000); // Set request timer to 2 seconds if we can get request earlier.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check if application has ACCESS_FINE_LOCATION permission
        if (ActivityCompat.checkSelfPermission(mapContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mapContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        //Focus camera on users location.
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location locationResult) {
                LatLng userPos = new LatLng(locationResult.getLatitude(), locationResult.getLongitude());
                CameraPosition cameraPosition = new CameraPosition(userPos, 15,0,0);
                //googleMap.addMarker(new MarkerOptions().position(userPos).title("Horunge"));
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        //Place a marker on the map for each activity in the list
        for (int i = 0; i < activityList.size(); i++) {
            String activityName = (String) activityList.get(i).get(0);
            String activityDate = (String) activityList.get(i).get(1);
            float latFromDb = (float) activityList.get(i).get(2);
            float longFromDb = (float) activityList.get(i).get(3);

            double activityLat = latFromDb;
            double activityLong = longFromDb;

            LatLng activityLatLng = new LatLng(activityLat,activityLong);

            googleMap.addMarker((new MarkerOptions().position(activityLatLng).title(activityName).snippet(activityDate)));
        }
        googleMap.setMyLocationEnabled(true);
    }

    // Required callback object for fusedLocationProviderClient
    LocationCallback locationCallback = new LocationCallback() {

    };


    /**
     * Method: onResume()
     * Overrides onResume for MapView object.
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Method: onPause
     * - If application is broken by user when activity this stops the fusedLocationProviderClient
     *   from calling locationCallback as it wont be needed at this time.
     * - Overrides onPause for MapView object.
     */
    @Override
    public void onPause() {
        super.onPause();
        if(fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        mapView.onPause();
    }

    /**
     * Method: onDestroy
     * Overrides onDestroy for MapView object.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * Method: onLowMemory
     * Overrides onLowMemory for MapView object.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}