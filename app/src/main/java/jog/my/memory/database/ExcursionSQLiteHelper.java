package jog.my.memory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Steve on 3/1/2015.
 */
public class ExcursionSQLiteHelper extends SQLiteOpenHelper {
    //Name of the table
    public static final String TABLE_EXCURSIONS = "EXCURSIONS";

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_EXCURSIONS +  " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "timestamp DATETIME NOT NULL," +
            "duration FLOAT," +
            "distance FLOAT," +
            "GPSData BLOB," +
            "pictureids BLOB,"+
            "name TEXT);";


    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_GPS_DATA = "GPSData";
    public static final String COLUMN_PICTUREIDS = "pictureids";
    public static final String COLUMN_NAME = "name";


    //Database information
    private static final String DATABASE_NAME = "EXCURSIONS.db";
    private static final int DATABASE_VERSION = 1;

    public ExcursionSQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create the database if it doesn't exist.
     * @param db - database to create
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /**
     * Upgrade the database to a new version.
     * @param db - database to upgrade
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ExcursionSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCURSIONS);
        onCreate(db);
    }
}
