package davidaeriksson.github.io.georeminders.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import davidaeriksson.github.io.georeminders.database.DatabaseHelper;
import davidaeriksson.github.io.georeminders.fragment.ActivityFragment;
import davidaeriksson.github.io.georeminders.fragment.MapFragment;
import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.fragment.ViewPagerAdapter;
import davidaeriksson.github.io.georeminders.misc.Constants;
import davidaeriksson.github.io.georeminders.misc.LocationService;


/**
 * @author David Eriksson
 * Main class for application, handles all view elements of the main activity, permissions and tabs.
 */

public class MainActivity extends AppCompatActivity {

    // Request code for background location permission.
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    // Static strings for map permissions.
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    // Initial request for map permissions request.
    private static final int INITIAL_REQUEST = 1337;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private MapFragment mapFragment;
    private ActivityFragment activityFragment;
    private DatabaseHelper databaseHelper;

    /**
     * Method: onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user has allowed application to use location as a background service, if not, ask
        // for permission, else, start location services.
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startLocationService();
        }

        // Ask user for permission to access location for use in MapFragment.java
        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }


        // Set up toolbar with view pager and tab layouts.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        mapFragment = new MapFragment();
        activityFragment = new ActivityFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragment(mapFragment, "Map");
        viewPagerAdapter.addFragment(activityFragment, "Activities");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_map_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_list_24);

        // Init database helper and create a badge with the number of activities at activity icon
        // in top menu.
        databaseHelper = DatabaseHelper.getInstance(this);
        for (int i = 0; i < databaseHelper.getRowCount(); i++) {
            if (databaseHelper.getRowCount() >= 1) {
                BadgeDrawable badgeDrawable = Objects.requireNonNull(tabLayout.getTabAt(1)).getOrCreateBadge();
                badgeDrawable.setVisible(true);
                badgeDrawable.setNumber((int) databaseHelper.getRowCount());
            }
        }
    }

    /**
     * Method: canAccessLocation
     * @return true?false
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    /**
     * Method: hasPermission
     * @param perm - Permission String, ACCESS_FINE_LOCATION.
     * @return true?false
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }

    /**
     * Method: onRequestPermissionsResult
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0 ) {
            startLocationService();
        } else {
            Toast.makeText(this, "PERMISSION DENIED" , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method: isLocationServiceRunning
     * @return true?false
     */
    private boolean isLocationServiceRunning () {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if(LocationService.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground) {
                        return true;
                    }
                }
            } return false;
        } return false;
    }

    /**
     * Method: startLocationService
     */
    private void startLocationService() {
        if(!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }
}