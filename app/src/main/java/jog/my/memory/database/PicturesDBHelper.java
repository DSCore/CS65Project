package jog.my.memory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Steve on 2/28/2015.
 * Directly accesses local SQLite DB, and provides other helper functions
 */
public class PicturesDBHelper {

    private static final String TAG = "PicturesDBHelper";
    private Context mContext;
    private PicturesSQLiteHelper mSqlHelper;
    private SQLiteDatabase db;

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXCURSIONID = "excursionid";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "long";

    private String[] allColumns = {
            COLUMN_ID,
            COLUMN_EXCURSIONID,
            COLUMN_IMAGE,
            COLUMN_LAT,
            COLUMN_LONG};


    public PicturesDBHelper(Context c) {
        mContext = c;
        this.mSqlHelper = new PicturesSQLiteHelper(this.mContext);
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

    public long insertEntry(Picture mPicture) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXCURSIONID, mPicture.getExcursionID());
        values.put(COLUMN_IMAGE, mPicture.getmImageAsByteArray());
        values.put(COLUMN_LAT, mPicture.getmLat());
        values.put(COLUMN_LONG, mPicture.getmLong());


        long insertId = db.insert(PicturesSQLiteHelper.TABLE_PICTURES, null,
                values);
        return insertId;
    }

    //Remove an entry by its id
    public void removeEntry(long mId) {
        Log.d(TAG, "Removing row mId=" + mId);
        db.delete(PicturesSQLiteHelper.TABLE_PICTURES, PicturesSQLiteHelper.COLUMN_ID + " = " + mId, null);
    }


    //Query a specific entry by its id
    public Picture fetchEntryByIndex(long mId) {
        //Select the entry with the right ID from the table.
        Cursor cursor = db.query(PicturesSQLiteHelper.TABLE_PICTURES, allColumns,
                PicturesSQLiteHelper.COLUMN_ID + " = " + mId, null, null, null, null);
        return cursorToPicture(cursor);
    }

    //Query the entire table, returning all rows
    public ArrayList<Picture> fetchEntries() {
        ArrayList<Picture> entries = new ArrayList<Picture>();

        Cursor cursor = db.query(PicturesSQLiteHelper.TABLE_PICTURES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Picture mIL = cursorToPicture(cursor);
            Log.d(TAG, "get IL = " + cursorToPicture(cursor).toString());
            entries.add(mIL);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }


    // Gets the ExerciseEntry that the cursor is currently at
    private Picture cursorToPicture(Cursor cursor) {
        Picture exc = new Picture();

        exc.setId(cursor.getLong(0));
        exc.setmExcursionID(cursor.getLong(1));
        exc.setmImage(BitmapFactory.decodeByteArray(cursor.getBlob(2), 0, cursor.getBlob(2).length));
        exc.setmLat(cursor.getDouble(3));
        exc.setmLong(cursor.getDouble(4));

        return exc;
    }
}
