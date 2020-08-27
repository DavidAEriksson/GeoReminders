package davidaeriksson.github.io.georeminders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author David Eriksson
 * DatabaseOpenHelper
 * Responsible for intializing database and creating table.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final int newVersion;
    private final String name;

    /**
     * Constructor: DatabaseOpenHelper
     * @param context
     * @param name
     * @param version
     */
    public DatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.newVersion = version;
        this.name = name;
    }

    /**
     * Method: onCreate
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createActivityTable(sqLiteDatabase);
    }

    /**
     * Method createActivityTable
     * @param sqLiteDatabase
     */
    private void createActivityTable(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("create table if not exists "
                + DatabaseConstants.DB_TABLE_ACTIVITY + "("
                + DatabaseConstants.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseConstants.COL_ACTIVITY_NAME + " TEXT NOT NULL, "
                + DatabaseConstants.COL_ACTIVITY_DATE + " TEXT NOT NULL, "
                + DatabaseConstants.COL_ACTIVITY_LAT  + " FLOAT NOT NULL, "
                + DatabaseConstants.COL_ACTIVITY_LONG  + " FLOAT NOT NULL "
                + ")");
    }

    /**
     * Method: onUpgrade
     * Empty method, required by SQLiteOpenHelper extension
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
