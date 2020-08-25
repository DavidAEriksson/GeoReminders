package davidaeriksson.github.io.georeminders.activity_adapter;

/**
 * @author David Eriksson
 * interface: DeleteActivityListener
 * Simple interface which is used by ActivityFragment to delete activites.
 */
public interface DeleteActivityListener {
    void onDeleteActivityAction(int activityId);
}
