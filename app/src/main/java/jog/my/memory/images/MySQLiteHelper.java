package jog.my.memory.images;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Devon Cormack on 1/28/15.
 *
 * Class that directly accesses the SQLite database for the MyRuns application.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS IMAGES (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "timestamp DATETIME NOT NULL," +
            "image BLOB,"+
            "lat real," +
            "long real);"; //The binary large object (BLOB) will be used to store
    //the image. Lat and long stored as REAL

    //Name of the table
    public static final String TABLE_IMAGES = "IMAGES";

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "long";

    //Database information
    private static final String DATABASE_NAME = "IMAGES.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context){
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
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }
}
