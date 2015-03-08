package jog.my.memory.database;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Steve on 2/28/2015.
 */
public class Excursion implements Serializable{
    private long mID;
    private Calendar mTimeStamp = new GregorianCalendar();
    private double mDuration;
    private double mDistance;
    private ArrayList<MyLatLng> mLocationList; // Location list
    private ArrayList<Long> mPictureIDs;
    private String mName;


    private static final java.text.SimpleDateFormat df
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs an Excursion object
     */
    public Excursion(){}

    /**
     * Constructs an Excursion with the given data
     */
    public Excursion(Calendar mTimeStamp, double mDuration, double mDistance, ArrayList<MyLatLng> mLocationList, ArrayList<Long> mPictureIDs, String mName){
        this.mTimeStamp = mTimeStamp;
        this.mDuration = mDuration;
        this.mDistance = mDistance;
        this.mLocationList = mLocationList;
        this.mPictureIDs = mPictureIDs;
        this.mName = mName;
    }

    public long getmID() {
        return mID;
    }

    public void setmID(long mID) {
        this.mID = mID;
    }

    public String getmTimeStamp() {
        return df.format(this.mTimeStamp.getTime());
    }

    public void setmTimeStamp(String date) {
        try {
            this.mTimeStamp.setTime(df.parse(date));
        }
        catch(ParseException pe){
            pe.printStackTrace();
        }
    }

    public double getmDuration() {
        return mDuration;
    }

    public void setmDuration(double mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public void setmGPSDATA(byte[] bytePointArray) {
        mLocationList = new ArrayList<MyLatLng>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        int locationNum = intArray.length / 2;

        for (int i = 0; i < locationNum; i++) {
            MyLatLng latLng = new MyLatLng((double) intArray[i * 2] / 1E6F,
                    (double) intArray[i * 2 + 1] / 1E6F);
            mLocationList.add(latLng);
        }
    }

    public byte[] getmGPSDATA() {
        int[] intArray = new int[mLocationList.size() * 2];

        for (int i = 0; i < mLocationList.size(); i++) {
            intArray[i * 2] = (int) (mLocationList.get(i).getLatitude() * 1E6);
            intArray[(i * 2) + 1] = (int) (mLocationList.get(i).getLongitude() * 1E6);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
                * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }

    //Define getter/setter functions to update a new ExerciseEntry
    public void setmLocationList(ArrayList<MyLatLng> latlngIn){
        this.mLocationList = latlngIn;
    }

    public ArrayList<MyLatLng> getmLocationList(){
        return mLocationList;
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

    //Todo
    public void setmPictureIDsFromByteArray(byte[] bIn){

    }

    public void setmName(String mName){
        this.mName = mName;
    }

    public String getmName(){
        return mName;
    }
}