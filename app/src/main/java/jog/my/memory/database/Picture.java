package jog.my.memory.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    private String mCaption;
    private Calendar mTimeStamp = new GregorianCalendar();

    private static final java.text.SimpleDateFormat df
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Constructs a Picture object
     * @param mContext
     */
    public Picture(){}

    /**
     * Non-default constructor
     */
    //Todo
    //Todo
    public Picture(double mLat, double mLong, Bitmap mImage, Location mLocation, String mCaption, Calendar mTimeStamp, long mExcursionID){
        this.mLat = mLat;
        this.mLong = mLong;
        this.mImage = mImage;
        this.mLocation = mLocation;
        this.mCaption = mCaption;
        this.mTimeStamp = mTimeStamp;
        this.mExcursionID = mExcursionID;
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

    public void setmCaption(String mCaption){
        this.mCaption = mCaption;
    }

    public String getmCaption(){
        return mCaption;
    }

    public String getmTimeStamp() {
        return df.format(this.mTimeStamp.getTime());
    }

    public void setmTimeStamp(Calendar cal) {
        this.mTimeStamp = cal;
    }

    public void setmTimeStamp(String date) {
        try {
            this.mTimeStamp.setTime(df.parse(date));
        }
        catch(ParseException pe){
            pe.printStackTrace();
        }
    }

}
