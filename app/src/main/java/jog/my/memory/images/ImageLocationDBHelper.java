package jog.my.memory.images;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Devon Cormack and Steven Muenzen on 2/3/15.
 */
public class ImageLocationDBHelper {
    private static final String TAG = "ImageLocationDBAdapter";
    private Context mContext;
    private MySQLiteHelper mSqlHelper;
    private SQLiteDatabase db;

    //Columns in table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "long";

    private String[] allColumns = {
            //TODO: Implement
            COLUMN_ID,
            COLUMN_TIMESTAMP,
            COLUMN_IMAGE,
            COLUMN_LAT,
            COLUMN_LONG};

    // TODO: Deprecated old images.
    private Integer[] mThumbIds = {

    };

    public ImageLocationDBHelper(Context c) {
        mContext = c;
        this.mSqlHelper = new MySQLiteHelper(this.mContext);
        this.open();
    }

    /**
     * DATABASE ACCESS METHODS **
     */
    //Opens the database for access.
    public void open() {
        this.db = this.mSqlHelper.getWritableDatabase();
    }

    //Closes the open database
    public void close() {
        mSqlHelper.close();
    }

    //Insert an item given all of the column's values.
    public long insertEntry(ImageLocation mIL) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, mIL.getmTimeStamp());
        values.put(COLUMN_IMAGE, mIL.getmImage());
        values.put(COLUMN_LAT, mIL.getmLocation().getLatitude());
        values.put(COLUMN_LONG, mIL.getmLocation().getLongitude());

        long insertId = db.insert(MySQLiteHelper.TABLE_IMAGES, null,
                values);
        return insertId;
    }

    //Remove an entry by its id
    public void removeEntry(long mId) {
        Log.d(TAG, "Removing row mId=" + mId);
        db.delete(MySQLiteHelper.TABLE_IMAGES, MySQLiteHelper.COLUMN_ID + " = " + mId, null);
    }

    //Query a specific entry by its id
    public ImageLocation fetchEntryByIndex(long mId) {
        //Select the entry with the right ID from the table.
        Cursor cursor = db.query(MySQLiteHelper.TABLE_IMAGES, allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + mId, null, null, null, null);
        return cursorToImageLocation(cursor);
    }

    //Query the entire table, returning all rows
    public ArrayList<ImageLocation> fetchEntries() {
        ArrayList<ImageLocation> entries = new ArrayList<ImageLocation>();

        Cursor cursor = db.query(MySQLiteHelper.TABLE_IMAGES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ImageLocation mIL = cursorToImageLocation(cursor);
            Log.d(TAG, "get IL = " + cursorToImageLocation(cursor).toString());
            entries.add(mIL);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    // Gets the ExerciseEntry that the cursor is currently at
    private ImageLocation cursorToImageLocation(Cursor cursor) {
        ImageLocation il = new ImageLocation();

        il.setId(cursor.getLong(0));
        il.setmTimeStamp(cursor.getString(1));
        il.setmImage(BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length));
        il.setmLat(cursor.getDouble(3));
        il.setmLong(cursor.getDouble(4));

        return il;
    }
}
