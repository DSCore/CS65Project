package jog.my.memory.database;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Steve on 2/28/2015.
 */
public class Excursion {
    private long mID;
    private Calendar mTimeStamp = new GregorianCalendar();
    private double mDuration;
    private double mDistance;

    private static final java.text.SimpleDateFormat df
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * Constructs an Excursion object
     */
    public Excursion(){}

    /**
     * Constructs an Excursion with the given data
     */
    public Excursion(Calendar mTimeStamp, double mDuration, double mDistance){
        this.mTimeStamp = mTimeStamp;
        this.mDuration = mDuration;
        this.mDistance = mDistance;
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
}

