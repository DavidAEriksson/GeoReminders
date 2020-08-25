package davidaeriksson.github.io.georeminders.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import davidaeriksson.github.io.georeminders.R;
import davidaeriksson.github.io.georeminders.database.DatabaseHelper;

/**
 * @author David Eriksson
 * UpdateActivity.java
 * Handles updating database entries and update_activity.xml
 */
public class UpdateActivity extends AppCompatActivity {

    public static final String ACTIVITY_ID = "activity_id";

    private TextInputLayout activityFieldUpdate;
    private DatePicker datePickerUpdate;
    private Button commitUpdateButton;

    private int activityId;

    /**
     * Method: onCreate
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

        activityFieldUpdate = findViewById(R.id.activity_field_update);
        datePickerUpdate = findViewById(R.id.date_picker_update);
        commitUpdateButton = findViewById(R.id.update_activity_button);
        activityId = getIntent().getIntExtra(ACTIVITY_ID, -1);

        handleOnClickUpdateButton();
    }

    /**
     * Method: handleOnClickUpdateButton
     * Fetches user made input from view and updates the selected database entry to fetched
     * information.
     */
    public void handleOnClickUpdateButton() {
        commitUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityName = String.valueOf(activityFieldUpdate.getEditText().getText());

                StringBuilder sb = new StringBuilder();
                int day = datePickerUpdate.getDayOfMonth();
                int month = datePickerUpdate.getMonth() + 1;
                int year = datePickerUpdate.getYear();
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
                        DatabaseHelper.getInstance(UpdateActivity.this).updateActivityData(activityId, activityName, activityDate);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(UpdateActivity.this, "Failed to update activity, try again!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UpdateActivity.this, "You need to provide a new name for the activity!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
