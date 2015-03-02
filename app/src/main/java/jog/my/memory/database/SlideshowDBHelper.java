package jog.my.memory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Steve on 2/28/2015.
 * Directly accesses local SQLite DB, and provides other helper functions
 */
public class SlideshowDBHelper {

    private static final String TAG = "SlideshowDBHelper";
    private Context mContext;
    private SlideshowSQLiteHelper mSqlHelper;
    private SQLiteDatabase db;

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXCURSIONID = "excursionid";
    public static final String COLUMN_PICTUREIDS = "pictureids";

    private String[] allColumns = {
            COLUMN_ID,
            COLUMN_EXCURSIONID,
            COLUMN_PICTUREIDS};


    public SlideshowDBHelper(Context c) {
        mContext = c;
        this.mSqlHelper = new SlideshowSQLiteHelper(this.mContext);
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

    public long insertEntry(Slideshow mSlideshow) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXCURSIONID, mSlideshow.getmExcursionID());
        values.put(COLUMN_PICTUREIDS, mSlideshow.getmPictureIDsAsByteArray());


        long insertId = db.insert(SlideshowSQLiteHelper.TABLE_SLIDESHOWS, null,
                values);
        return insertId;
    }

    //Remove an entry by its id
    public void removeEntry(long mId) {
        Log.d(TAG, "Removing row mId=" + mId);
        db.delete(SlideshowSQLiteHelper.TABLE_SLIDESHOWS, SlideshowSQLiteHelper.COLUMN_ID + " = " + mId, null);
    }


    //Query a specific entry by its id
    public Slideshow fetchEntryByIndex(long mId) {
        //Select the entry with the right ID from the table.
        Cursor cursor = db.query(SlideshowSQLiteHelper.TABLE_SLIDESHOWS, allColumns,
                SlideshowSQLiteHelper.COLUMN_ID + " = " + mId, null, null, null, null);
        return cursorToSlideshow(cursor);
    }

    //Query the entire table, returning all rows
    public ArrayList<Slideshow> fetchEntries() {
        ArrayList<Slideshow> entries = new ArrayList<Slideshow>();

        Cursor cursor = db.query(SlideshowSQLiteHelper.TABLE_SLIDESHOWS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Slideshow mIL = cursorToSlideshow(cursor);
            Log.d(TAG, "get IL = " + cursorToSlideshow(cursor).toString());
            entries.add(mIL);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }


    // Gets the ExerciseEntry that the cursor is currently at
    private Slideshow cursorToSlideshow(Cursor cursor) {
        Slideshow exc = new Slideshow();

        exc.setId(cursor.getLong(0));
        exc.setmExcursionID(cursor.getLong(1));
        exc.setmPictureIDs(deserialize(cursor.getBlob(2)));

        return exc;
    }

    public ArrayList<Long> deserialize(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            ArrayList<Long> list = (ArrayList<Long>) object;

            return list;
        } catch (ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch (IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);
            return null;
        }

    }
}
