package jog.my.memory.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Devon Cormack and Steven Muenzen on 2/3/15.
 */
public class ImageLocation {
    private long id;
    private Calendar mTimeStamp = new GregorianCalendar();
    private Bitmap mImage;
    private Location mLocation;
    private double mLat;
    private double mLong;

    private static final java.text.SimpleDateFormat df
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //TODO: Set variable, getter and setter for IMAGE

    /**
     * Constructs an ImageLocation object
     */
    public ImageLocation(){}

    /**
     * Constructs an ImageLocation with the given data
     */
    public ImageLocation(Calendar mTimeStamp, double mLatitude, double mLongitude, Bitmap mImage){
        this.mTimeStamp = mTimeStamp;
        this.mLat = mLatitude;
        this.mLong = mLongitude;
        this.mImage = mImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setmImage(Bitmap mImage){
        this.mImage = mImage;
    }

    public void setmImageToByteArray(byte[] mImage){
        Bitmap bmp= BitmapFactory.decodeByteArray(mImage, 0, mImage.length);
        this.mImage = bmp;
    }

    public byte[] getmImage(){
        //Convert mImage to byte array for storage as BLOB in db
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);  //Middle parameter???
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public Bitmap getmImageAsBitmap(){
        return this.mImage;
    }
}

