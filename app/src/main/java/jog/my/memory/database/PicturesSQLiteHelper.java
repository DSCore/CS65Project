package jog.my.memory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Steve on 3/1/2015.
 */
public class PicturesSQLiteHelper extends SQLiteOpenHelper{

    //Name of the table
    public static final String TABLE_PICTURES = "PICTURES";

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PICTURES +  " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "excursionid INTEGER," +
            "image BLOB," +
            "lat real,"+
            "long real," +
            "caption TEXT," +
            "timestamp DATETIME NOT NULL);";

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXCURSIONID = "excursionid";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "long";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_TIMESTAMP = "timestamp";


    //Database information
    private static final String DATABASE_NAME = "PICTURES.db";
    private static final int DATABASE_VERSION = 1;

    public PicturesSQLiteHelper(Context context){
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        onCreate(db);
    }


}
