package jog.my.memory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Steve on 3/1/2015.
 */
public class SlideshowSQLiteHelper extends SQLiteOpenHelper {


    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "excursionid INTEGER," +
            "pictureids BLOB);";   //ArrayList of picture IDs, stored in Pictures database

    //Name of the table
    public static final String TABLE_SLIDESHOWS = "SLIDESHOWS";

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXCURSIONID = "excursionid";
    public static final String COLUMN_PICTUREIDS = "pictureids";

    //Database information
    private static final String DATABASE_NAME = "SLIDESHOW.db";
    private static final int DATABASE_VERSION = 1;

    public SlideshowSQLiteHelper(Context context){
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
        Log.w(SlideshowSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLIDESHOWS);
        onCreate(db);
    }
}
