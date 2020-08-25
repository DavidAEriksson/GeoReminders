package davidaeriksson.github.io.georeminders.activity_adapter;


import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.database.DatabaseConstants;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author David Eriksson
 * ActivityListAdapter
 * Custom adapter for activity list which is used by the RecyclerView in activity_main.xml.
 */
public class ActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Cursor cursor;

    // Adapter interfaces.
    public DeleteActivityListener deleteActivityListener;
    public UpdateActivityListener updateActivityListener;

    /**
     * Constructor: ActivityListAdapter
     * @param cursor
     */
    public ActivityListAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Method: getItemCount
     * @return cursor.getCount - Count of items in cursor
     */
    @Override
    public int getItemCount() {
        if (cursor == null || cursor.isClosed()) return 0;
        else return cursor.getCount();
    }

    /**
     * Method: onCreteViewHolder
     * Creates new recyclerView.ViewHolder to be used by RecyclerVeew
     * @param parent
     * @param viewType
     * @return - ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Method: onBindViewHolder
     * Updates the RecyclerView.ViewHolder when new items are added.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @author David Eriksson
     * ViewHolder
     * Describes the ViewHolder item contents of the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;

        private final TextView activityNameTextView;
        private final TextView activityDateTextView;
        private final Button deleteActivityButton;
        private final Button updateActivityButton;

        /**
         * Constructor: ViewHolder
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            activityNameTextView = itemView.findViewById(R.id.activity_name);
            activityDateTextView = itemView.findViewById(R.id.activity_date);

            deleteActivityButton = itemView.findViewById(R.id.delete_activity);
            updateActivityButton = itemView.findViewById(R.id.update_activity);
        }

        public void bindView(int pos) {

            cursor.moveToPosition(pos);

            final int activityId = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COL_ID));
            final String activityName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COL_ACTIVITY_NAME));
            final String activityDate = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COL_ACTIVITY_DATE));

            activityNameTextView.setText(activityName);
            activityDateTextView.setText(activityDate);

            // Call deleteActivityListener to delete this entry.
            deleteActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteActivityListener.onDeleteActivityAction(activityId);
                }
            });

            // Call updateActivityListener to update this entry.
            updateActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateActivityListener.onUpdateActivityAction(activityId);
                }
            });
        }
    }
}
