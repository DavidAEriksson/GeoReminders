package davidaeriksson.github.io.georeminders.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import davidaeriksson.github.io.georeminders.misc.Constants;
import davidaeriksson.github.io.georeminders.model.Activity;

/**
 * @author David Eriksson
 * DatabaseHelper
 * Responsible for all database operations.
 */
public class DatabaseHelper {

    private static final int SQLITEDB_VERSION = 1;
    private static DatabaseHelper instance = null;
    private static DatabaseOpenHelper databaseOpenHelper;
    private final Context context;

    /**
     * Constructor: DatabaseHelper
     * @param context
     */
    private DatabaseHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Method: getInstance
     * @param context
     * @return instance - Instance of this context.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
            databaseOpenHelper = new DatabaseOpenHelper(context, DatabaseConstants.DB_NAME, SQLITEDB_VERSION);
        }
        return instance;
    }

    /**
     * Method: getRowCount
     * @return count - Total amount of rows in table DB_TABLE_ACTIVITY(activity_table)
     */
    public long getRowCount() {
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseConstants.DB_TABLE_ACTIVITY);
        sqLiteDatabase.close();
        return count;
    }

    /**
     * Method: insertActivityToDb
     * @param activity
     * @return rowId
     */
    public long insertActivityToDb(Activity activity) {
        long rowId = -1;

        Log.d(Constants.AddActivity, "Name: " + activity.name + " date: " + activity.date + " LAT: " + activity.latitude + " LONG: " + activity.longitude);

        if (activity != null) {
            SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseConstants.COL_ACTIVITY_NAME, activity.name);
                cv.put(DatabaseConstants.COL_ACTIVITY_DATE, activity.date);
                cv.put(DatabaseConstants.COL_ACTIVITY_LAT, activity.latitude);
                cv.put(DatabaseConstants.COL_ACTIVITY_LONG, activity.longitude);
                rowId = sqLiteDatabase.insertWithOnConflict(DatabaseConstants.DB_TABLE_ACTIVITY, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();

            // Notify change to table so that our adapter can update contents in recycle view.
            context.getContentResolver().notifyChange(DatabaseConstants.DB_TABLE_ACTIVITY_URI, null);
        }

        return rowId;
    }

    /**
     * getAllDataFromActivityTable
     * @return cursor - Contains all data in the table DB_TABLE_ACTIVITY(activity_table)
     */
    public Cursor getAllDataFromActivityTable() {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + DatabaseConstants.DB_TABLE_ACTIVITY;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null) cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * Method: getActivityDataInArrayList
     * @param index
     * @return list - Data from DB_TABLE_ACTIVITY(activity_table) as an ArrayList object.
     */
    public ArrayList getActivityDataInArrayList(int index) {
        ArrayList list = new ArrayList();
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            String query = "SELECT * FROM " + DatabaseConstants.DB_TABLE_ACTIVITY;
            cursor = sqLiteDatabase.rawQuery(query,null);
            if (cursor != null) cursor.moveToPosition(index);
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getFloat(3));
            list.add(cursor.getFloat(4));
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return list;
    }

    /**
     * Method: deleteActivityFromTable
     * @param activityId - Item to be deleted.
     */
    @SuppressLint("Recycle")
    public void deleteActivityFromTable(int activityId) {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            String query = "DELETE FROM " + DatabaseConstants.DB_TABLE_ACTIVITY + " WHERE " + DatabaseConstants.COL_ID + " = " + activityId;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null) cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

        // Notify change to table so that our adapter can update contents in recycle view.
        context.getContentResolver().notifyChange(DatabaseConstants.DB_TABLE_ACTIVITY_URI, null);
    }

    /**
     * Method: updateActivityData
     * @param activityId - Item to be updated
     * @param activityName - Updated name
     * @param activityDate - Updated date.
     */
    @SuppressLint("Recycle")
    public void updateActivityData(int activityId, String activityName, String activityDate) {
        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = databaseOpenHelper.getReadableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            String query = "UPDATE " + DatabaseConstants.DB_TABLE_ACTIVITY
                    + " SET " + DatabaseConstants.COL_ACTIVITY_NAME + " = '" + activityName + "'" + " , " + DatabaseConstants.COL_ACTIVITY_DATE + " = '" + activityDate + "'"
                    + " WHERE " + DatabaseConstants.COL_ID + " = " + activityId;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null) cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

        // Notify change to table so that our adapter can update contents in recycle view.
        context.getContentResolver().notifyChange(DatabaseConstants.DB_TABLE_ACTIVITY_URI, null);
    }
}
