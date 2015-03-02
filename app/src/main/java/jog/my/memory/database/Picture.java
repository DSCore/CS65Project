package jog.my.memory.database;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.ByteArrayOutputStream;

/**
 * Created by Steve on 3/1/2015.
 */
public class Picture {
    private long id;
    private long mExcursionID;
    private Bitmap mImage;
    private Location mLocation;
    private double mLat;
    private double mLong;

    /**
     * Constructs a Picture object
     */
    public Picture(){}

    /**
     * Non-default constructor
     */
    public Picture(double mLat, double mLong, Bitmap mImage){
        this.mLat = mLat;
        this.mLong = mLong;
        this.mImage = mImage;
    }


    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getExcursionID(){
        return mExcursionID;
    }

    public void setmExcursionID(long mExcursionID){
        this.mExcursionID = mExcursionID;
    }

    public Bitmap getmImage(){
        return mImage;
    }

    public void setmImage(Bitmap mImage){
        this.mImage = mImage;
    }

    public byte[] getmImageAsByteArray(){
        //Convert mImage to byte array for storage as BLOB in db
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);  //Middle parameter???
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    public void setmLocation(Location mLocation){
        this.mLocation = mLocation;
    }

    public Location getmLocation(){
        if(mLocation == null){
            //Create a new location with no provider
            mLocation = new Location("");
            mLocation.setLatitude(this.mLat);
            mLocation.setLongitude(this.mLong);
        }
        return mLocation;
    }

    public double getmLat(){
        return mLat;
    }

    public void setmLat(double mLat){
        this.mLat = mLat;
    }

    public double getmLong(){
        return mLong;
    }

    public void setmLong(double mLong){
        this.mLong = mLong;
    }

}
