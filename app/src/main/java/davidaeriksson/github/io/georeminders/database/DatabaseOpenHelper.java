package davidaeriksson.github.io.georeminders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final int newVersion;
    private final String name;

    public DatabaseOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.newVersion = version;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createActivityTable(sqLiteDatabase);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
