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
public class ExcursionDBHelper {
    private static final String TAG = "ExcursionDBHelper";
    private Context mContext;
    private ExcursionSQLiteHelper mSqlHelper;
    private SQLiteDatabase db;

    //Columns in the table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_GPS_DATA = "GPSData";
    public static final String COLUMN_PICTUREIDS = "pictureids";
    public static final String COLUMN_NAME = "name";

    private String[] allColumns = {
            COLUMN_ID,
            COLUMN_TIMESTAMP,
            COLUMN_DURATION,
            COLUMN_DISTANCE,
            COLUMN_GPS_DATA,
            COLUMN_PICTUREIDS,
            COLUMN_NAME};


    public ExcursionDBHelper(Context c) {
        mContext = c;
        this.mSqlHelper = new ExcursionSQLiteHelper(this.mContext);
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

    public long insertEntry(Excursion mExcursion) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, mExcursion.getmTimeStamp());
        values.put(COLUMN_DURATION, mExcursion.getmDuration());
        values.put(COLUMN_DISTANCE, mExcursion.getmDistance());
        values.put(COLUMN_GPS_DATA, mExcursion.getmGPSDATA());
        values.put(COLUMN_PICTUREIDS, mExcursion.getmPictureIDsAsByteArray());
        values.put(COLUMN_NAME, mExcursion.getmName());

        long insertId = db.insert(ExcursionSQLiteHelper.TABLE_EXCURSIONS, null,
                values);
        return insertId;
    }

    public boolean updateEntry(long rowId, Excursion mExcursion)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, mExcursion.getmTimeStamp());
        values.put(COLUMN_DURATION, mExcursion.getmDuration());
        values.put(COLUMN_DISTANCE, mExcursion.getmDistance());
        values.put(COLUMN_GPS_DATA, mExcursion.getmGPSDATA());
        values.put(COLUMN_PICTUREIDS, mExcursion.getmPictureIDsAsByteArray());
        values.put(COLUMN_NAME, mExcursion.getmName());
        int i =  db.update(ExcursionSQLiteHelper.TABLE_EXCURSIONS, values, COLUMN_ID + "=" + rowId, null);
        return i > 0;
    }

    //Remove an entry by its id
    public void removeEntry(long mId) {
        Log.d(TAG, "Removing row mId=" + mId);
        db.delete(ExcursionSQLiteHelper.TABLE_EXCURSIONS, ExcursionSQLiteHelper.COLUMN_ID + " = " + mId, null);
    }


    //Query a specific entry by its id
    public Excursion fetchEntryByIndex(long mId) {
        //Select the entry with the right ID from the table.
        Cursor cursor = db.query(ExcursionSQLiteHelper.TABLE_EXCURSIONS, allColumns,
                ExcursionSQLiteHelper.COLUMN_ID + " = " + mId, null, null, null, null);
        return cursorToExcursion(cursor);
    }

    //Query the entire table, returning all rows
    public ArrayList<Excursion> fetchEntries() {
        ArrayList<Excursion> entries = new ArrayList<Excursion>();

        Cursor cursor = db.query(ExcursionSQLiteHelper.TABLE_EXCURSIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Excursion mIL = cursorToExcursion(cursor);
            Log.d(TAG, "get IL = " + cursorToExcursion(cursor).toString());
            entries.add(mIL);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }


    // Gets the ExerciseEntry that the cursor is currently at
    private Excursion cursorToExcursion(Cursor cursor) {
        Excursion exc = new Excursion();

        exc.setmID(cursor.getLong(0));
        exc.setmTimeStamp(cursor.getString(1));
        exc.setmDuration(cursor.getDouble(2));
        exc.setmDistance(cursor.getDouble(3));
        exc.setmGPSDATA(cursor.getBlob(4));
        exc.setmPictureIDs(deserialize(cursor.getBlob(5)));
        exc.setmName(cursor.getString(6));

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