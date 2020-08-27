package davidaeriksson.github.io.georeminders.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.loader.content.AsyncTaskLoader;

/**
 * @author David Eriksson
 * SQLiteCursorLoader.java
 * Loader which queries ContentResolver and returns a Cursor.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

    private final ForceLoadContentObserver observer;
    private Cursor cursor;

    /**
     * Constructor: SQLiteCursorLoader
     * Creates an empty unspecified CursorLoader.
     */
    public SQLiteCursorLoader(Context context) {
        super(context);
        observer = new ForceLoadContentObserver();
    }

    /* Runs on a worker thread */
    @Override
    public abstract Cursor loadInBackground();

    /**
     * Registers an observer to get notifications from the content provider
     * when the cursor needs to be refreshed.
     */
    public void registerContentObserver(Cursor cursor, Uri observerUri) {
        cursor.registerContentObserver(observer);
        cursor.setNotificationUri(getContext().getContentResolver(), observerUri);
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        try {
            if (isReset()) {
                // An async query came in while the loader is stopped
                if (cursor != null) {
                    cursor.close();
                }
                return;
            }
            Cursor oldCursor = this.cursor;
            this.cursor = cursor;

            if (isStarted()) {
                super.deliverResult(cursor);
            }

            if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
                oldCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method: onStartLoading
     * Starts an asynchronous load of the list data.
     */
    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    /**
     * Method: onStopLoading
     * Attempt to cancel the current load task if possible.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Method onCanceled
     * @param cursor
     */
    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * Method: onReset
     */
    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
    }

}