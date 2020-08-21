package davidaeriksson.github.io.georeminders.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.activity.AddActivity;
import davidaeriksson.github.io.georeminders.activity.UpdateActivity;
import davidaeriksson.github.io.georeminders.activity_adapter.ActivityListAdapter;
import davidaeriksson.github.io.georeminders.activity_adapter.DeleteActivityListener;
import davidaeriksson.github.io.georeminders.activity_adapter.UpdateActivityListener;
import davidaeriksson.github.io.georeminders.database.DatabaseConstants;
import davidaeriksson.github.io.georeminders.database.DatabaseHelper;
import davidaeriksson.github.io.georeminders.database.SQLiteCursorLoader;

public class ActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, UpdateActivityListener, DeleteActivityListener {

    private static final String TAG = "ActivityFragment";
    private static final int GET_ACTIVITY_QUERY_LOADER = 0;
    Context activityContext;

    private FloatingActionButton addActivityButton;
    private RecyclerView recyclerView;
    private ActivityListAdapter activityListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView activityListEmpty;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View activityFragment = inflater.inflate(R.layout.fragment_activity, container, false);
        this.activityContext = activityFragment.getContext();
        addActivityButton = activityFragment.findViewById(R.id.add_new_activity_fab);
        linearLayoutManager = new LinearLayoutManager(activityContext);
        recyclerView = activityFragment.findViewById(R.id.activity_list);
        recyclerView.setLayoutManager(linearLayoutManager);
        activityListEmpty = activityFragment.findViewById(R.id.activity_list_empty);


        handleOnClickFab();
        LoaderManager.getInstance(this).initLoader(GET_ACTIVITY_QUERY_LOADER,null,this);

        return activityFragment;
    }

    public void setupAdapter(Cursor cursor) {
        activityListAdapter = new ActivityListAdapter(cursor);
        activityListAdapter.deleteActivityListener = (DeleteActivityListener) this;
        activityListAdapter.updateActivityListener = (UpdateActivityListener) this;
        recyclerView.setAdapter(activityListAdapter);
    }

    private void handleOnClickFab() {
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewActivityIntent = new Intent(activityContext, AddActivity.class);
                startActivity(addNewActivityIntent);
                Log.d(TAG, "FAB Clicked, starting Intent: " + addNewActivityIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LoaderManager.getInstance(this).initLoader(GET_ACTIVITY_QUERY_LOADER,null,this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoaderManager.getInstance(this).initLoader(GET_ACTIVITY_QUERY_LOADER,null,this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new SQLiteCursorLoader(activityContext) {
            @Override
            public Cursor loadInBackground() {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(activityContext);
                Cursor cursor;
                cursor = databaseHelper.getAllDataFromActivityTable();

                this.registerContentObserver(cursor, DatabaseConstants.DB_TABLE_ACTIVITY_URI);
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            activityListEmpty.setVisibility(View.GONE);
        } else activityListEmpty.setVisibility(View.VISIBLE);
        setupAdapter(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onDeleteActivityAction(int activityId) {
        DatabaseHelper.getInstance(activityContext).deleteActivityFromTable(activityId);
    }

    @Override
    public void onUpdateActivityAction(int activityId) {
        Intent updateActivityIntent = new Intent(activityContext, UpdateActivity.class);
        updateActivityIntent.putExtra(UpdateActivity.ACTIVITY_ID, activityId);
        startActivity(updateActivityIntent);
    }
}