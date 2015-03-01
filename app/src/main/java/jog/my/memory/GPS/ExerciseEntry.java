package jog.my.memory.GPS;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Devon Cormack on 1/28/15.
 */
public class ExerciseEntry implements Serializable {

    private static final String TAG = "ExerciseEntry";
    private Long id;

    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime = new GregorianCalendar();    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private double mSpeed;
    private int mCalorie;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate = 85;        // Heart rate
    private String mComment;       // Comments
    private int mPrivacy;          // Privacy
    private ArrayList<MyLatLng> mLocationList = new ArrayList<MyLatLng>(); // Location list

    //Additional information needed:
    private long mStartTime = 0;
    private double mStartAltitude = 0;

    //Conversion object for converting dates between Java and MySQL
    private static final java.text.SimpleDateFormat df
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //Constructor
    public ExerciseEntry(){}

    //Constructor for testing
    public ExerciseEntry(Long id, int mInputType, int mActivityType, String mDateTime,
                         int mDuration, double mDistance, double mAvgPace, double mAvgSpeed,
                         int mCalorie, int mClimb, int mHeartRate, String mComment, int mPrivacy,
                         byte[] mLocationList){
        this.setId(id);
        this.setmInputType(mInputType);
        this.setmActivityType(mActivityType);
        this.setmDateTime(mDateTime);
        this.setmDuration(mDuration);
        this.setmDistance(mDistance);
        this.setmAvgPace(mAvgPace);
        this.setmAvgSpeed(mAvgSpeed);
        this.setmCalorie(mCalorie);
        this.setmClimb(mClimb);
        this.setmHeartRate(mHeartRate);
        this.setmComment(mComment);
        this.setmPrivacy(mPrivacy);
        this.setmLocationList(mLocationList);
    }

    //Constructor for testing with Calendar
    public ExerciseEntry(Long id, int mInputType, int mActivityType, Calendar mDateTime,
                         int mDuration, double mDistance, double mAvgPace, double mAvgSpeed,
                         int mCalorie, int mClimb, int mHeartRate, String mComment, int mPrivacy,
                         byte[] mLocationList){
        this.setId(id);
        this.setmInputType(mInputType);
        this.setmActivityType(mActivityType);
        this.setmDateTime(mDateTime);
        this.setmDuration(mDuration);
        this.setmDistance(mDistance);
        this.setmAvgPace(mAvgPace);
        this.setmAvgSpeed(mAvgSpeed);
        this.setmCalorie(mCalorie);
        this.setmClimb(mClimb);
        this.setmHeartRate(mHeartRate);
        this.setmComment(mComment);
        this.setmPrivacy(mPrivacy);
        this.setmLocationList(mLocationList);
    }

    /*** GETTERS AND SETTERS ***/

    public byte[] getmLocationListAsByteArray() {
        //TODO: Implement when GPS is working
        //This can be done using serialize() in Hibernate, see
        // http://docs.jboss.org/hibernate/core/3.3/api/org/hibernate/util/SerializationHelper.html
        if(this.mLocationList != null) {
//            ArrayList<MyLatLng> mTempList = new ArrayList<MyLatLng>();
//            for (int i = 0; i < this.mLocationList.size(); i++) {
//                MyLatLng mll = this.mLocationList.get(i);
//                mTempList.add(new MyLatLng(mll.latitude, mll.longitude));
//            }
            try {
                ByteArrayOutputStream mbos = new ByteArrayOutputStream();
                ObjectOutputStream mOOS = new ObjectOutputStream(mbos);
                mOOS.writeObject(/*mTempList*/this.mLocationList);
                byte[] mBytesOut = mbos.toByteArray();
                return mBytesOut;
            } catch (IOException ioe) {
                Log.d(TAG, "Getting mLocationList as Byte Array failed!");
                ioe.printStackTrace();
            }
        }
        return null;
    }

    public void setmLocationList(byte[] mLocationList) {
        //TODO: Implement when GPS is working
        //This can be done using deserialize() in Hibernate, see
        // http://docs.jboss.org/hibernate/core/3.3/api/org/hibernate/util/SerializationHelper.html
        if(mLocationList != null) {
            try {
                ByteArrayInputStream mbis = new ByteArrayInputStream(mLocationList);
                ObjectInputStream mOIS = new ObjectInputStream(mbis);
                this.mLocationList = (ArrayList<MyLatLng>) mOIS.readObject();
//                ArrayList<MyLatLng> mTempList = (ArrayList<MyLatLng>) mOIS.readObject();
//                ArrayList<LatLng> mNewLocationList = new ArrayList<LatLng>();
//                for(int i=0; i<mTempList.size();i++){
//                    MyLatLng mll = mTempList.get(i);
//                    mNewLocationList.add(new LatLng(mll.getLatitude(),mll.getLongitude()));
//                }
//                this.mLocationList = mNewLocationList;
            } catch (IOException ioe) {
                Log.d(TAG, "Setting mLocationList from Byte Array failed!");
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                Log.d(TAG, "Setting mLocationList from Byte Array failed!");
                cnfe.printStackTrace();
            }
        }
    }

    public ArrayList<MyLatLng> getmLocationList(){
        return this.mLocationList;
    }

    public void setmLocationList(ArrayList<LatLng> locationList){
        this.mLocationList.clear();
        for(int i=0;i<locationList.size();i++){
            this.mLocationList.add(new MyLatLng(locationList.get(i)));
        }
    }

    /**c
     * Populates the relevant fields with information in mLoc
     * @param mLoc - Location
     */
    public void addLocation(Location mLoc){
        //Set the distance
        if(this.getmLocationList().isEmpty()){
            this.mDistance = 0;
        }
        else {
            MyLatLng mLl = this.getmLocationList().get(this.getmLocationList().size() - 1);
            Location mPrevLoc = new Location("");
            mPrevLoc.setLatitude(mLl.getLatitude());
            mPrevLoc.setLongitude(mLl.getLongitude());
            this.mDistance += mLoc.distanceTo(mPrevLoc)/1000; //TODO: Make sure that this scale factor is accurate!
        }
        //Set the duration
        this.setmDuration(this.calculateTimePassed(mLoc.getTime()));
        //Set the current speed
        this.mSpeed = mLoc.getSpeed();
        //Set the average speed
        this.mAvgSpeed = this.calculateAverageSpeed(mLoc.getSpeed());
        //Set the average pace
        this.mAvgPace = this.calculateAveragePace();
        //Add the new entry's location to the location list
        this.mLocationList.add(new MyLatLng(mLoc.getLatitude(),mLoc.getLongitude()));
        //Save the altitude
        this.mClimb = this.calculateClimb(mLoc.getAltitude());
        this.setmCalorie(this.calculateCaloriesBurnt());
    }

    public double calculateClimb(double mNewAltitude){
        if(this.mStartAltitude == 0){
            this.mStartAltitude = mNewAltitude;
            return 0;
        }
        return (mNewAltitude-this.mStartAltitude);
    }

    /**
     * Calculates the average speed of the journey
     * in miles per hour
     * @return the average speed in mph
     */
    public double calculateAverageSpeed(float newSpeed){
        if(this.getmAvgSpeed() == 0){
            this.setmAvgSpeed(newSpeed);
        }
        double mNumEntries = this.getmLocationList().size();
        return (this.getmAvgSpeed()*mNumEntries+newSpeed)/(mNumEntries+1);
    }

    /**
     * Calculates the average pace of the journey bsaed on
     * the current speed and distance.
     * @return the average pace in mph
     */
    public double calculateAveragePace(){
        return this.getmAvgSpeed()/this.getmDistance();
    }

    /**
     * Calculate the amount of time that has passed since the start
     * the exercise.
     * @return - the amount of time in minutes.
     */
    public int calculateTimePassed(long milliseconds){
        if(this.mStartTime == 0){
            this.mStartTime = milliseconds;
            return 0;
        }
        return (int)((milliseconds-this.mStartTime)/60000);
    }

    /**
     * Calculate the number of calories that were burnt so far from exercise
     * @return - Calories burnt
     */
    public int calculateCaloriesBurnt(){
        /** Table of calories:
         * Data from
         * http://www.mayoclinic.org/healthy-living/weight-loss/in-depth/exercise/art-20050999?pg=2
         * http://tntoday.utk.edu/2011/10/28/wheelchair-exercise-calorie-burning/
         * http://www.livestrong.com/article/294822-calories-burned-while-mountain-biking/
         * http://www.livestrong.com/article/73916-calories-burned-standing-vs.-sitting/
         * http://www.livestrong.com/article/117387-calories-burned-snowboarding/
         * 0  Running - 14.35 cal/min
         * 1  Walking - 5.23 cal/min
         * 2  Standing - 1.65 cal/min
         * 3  Cycling - 4.87 cal/min
         * 4  Hiking - 7.30 cal/min
         * 5  Downhill Skiing - 5.23 cal/min
         * 6  Cross-Country Skiing - 8.27 cal/min
         * 7  Snowboarding - 6.67 cal/min
         * 8  Skating - 8.52 cal/min
         * 9  Swimming - 7.05 cal/min
         * 10 Mountain Biking - 8.80 cal/min
         * 11 Wheelchair - 4 cal/min
         * 12 Elliptical - 6.08 cal/min
         * 13 Other - 7 cal/min
         */
        switch(this.getmActivityType()){
            case 0:
                return (int)14.35*this.getmDuration();
            case 1:
                return (int)5.23*this.getmDuration();
            case 2:
                return (int)1.65*this.getmDuration();
            case 3:
                return (int)4.87*this.getmDuration();
            case 4:
                return (int)7.30*this.getmDuration();
            case 5:
                return (int)5.23*this.getmDuration();
            case 6:
                return (int)8.27*this.getmDuration();
            case 7:
                return (int)6.67*this.getmDuration();
            case 8:
                return (int)8.52*this.getmDuration();
            case 9:
                return (int)7.05*this.getmDuration();
            case 10:
                return (int)8.80*this.getmDuration();
            case 11:
                return (int)4*this.getmDuration();
            case 12:
                return (int)6.08*this.getmDuration();
            case 13:
            default:
                Log.d(TAG, "The case is not found");
                return (int)7*this.getmDuration();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public int getmInputType() {
        return mInputType;
    }

    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getmActivityType() {
        return mActivityType;
    }

    public void setmActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public String getmDateTime() {
        //Format the Date so that it can be used in MySQL's DATETIME field
        java.util.Date date = this.mDateTime.getTime();
        return df.format(date);
    }

    public void setmDateTime(String mDateTime) {
        try {
            this.mDateTime.setTime(df.parse(mDateTime));
        }catch(ParseException pe){
            Log.d(TAG, "ParseException thrown on parsing " + mDateTime + " to a Date object.");
        }
    }

    //Overload for non-database setting
    public void setmDateTime(Calendar mDateTime){
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public double getmAvgPace() {
        return mAvgPace;
    }

    public void setmAvgPace(double mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public double getmAvgSpeed() {
        return mAvgSpeed;
    }

    public void setmAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public int getmCalorie() {
        return mCalorie;
    }

    public void setmCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public double getmClimb() {
        return mClimb;
    }

    public void setmClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPrivacy() {
        return mPrivacy;
    }

    public void setmPrivacy(int mPrivacy) {
        this.mPrivacy = mPrivacy;
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

}
