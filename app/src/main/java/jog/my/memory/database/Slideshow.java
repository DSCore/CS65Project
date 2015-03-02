package jog.my.memory.database;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Steve on 2/28/2015.
 */
public class Slideshow {
    private long id;
    private long mExcursionID;
    private ArrayList<Long> mPictureIDs;


    /**
     * Constructs a Picture object
     */
    public Slideshow() {
    }

    /**
     * Non-default constructor
     */
    public Slideshow(long mExcursionID, ArrayList<Long> mPictureIDs) {
        this.mExcursionID = mExcursionID;
        this.mPictureIDs = mPictureIDs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setmExcursionID(long mExcursionID) {
        this.mExcursionID = mExcursionID;
    }

    public long getmExcursionID() {
        return mExcursionID;
    }

    public void setmPictureIDs(ArrayList<Long> mPictureIDs) {
        this.mPictureIDs = mPictureIDs;
    }

    public ArrayList<Long> getmPictureIDs() {
        return mPictureIDs;
    }

    public byte[] getmPictureIDsAsByteArray() {
        Object o = mPictureIDs;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();

            return buf;
        } catch (IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }
}
