package jog.my.memory.GPS;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import jog.my.memory.HomeActivity;
import jog.my.memory.R;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    final static String ACTION_NOTIFYSERVICESTOP = "NotifyServiceOfSomething";
    final static String STOP_SERVICE_BROADCAST_KEY = "StopTheService";
    final static int RQST_STOP_SERVICE = 1;
    //Hold the data in LocationService, which is persistent.
//    public static ExerciseEntry sCurEE;
//    public static int sLengthOfLastLocationArrayProcessed = 0;

////    private LocationServiceReceiver mLSR;
//    private LocationManager mLM;
    private PendingIntent mPendingIntent;
    private NotificationManager mNotificationManager;
//    private Timer mLocationUpdateTimer;
//    private Location lastSampled;
//    private int samplingInterval = 1000;

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

//        //Set up the exercise entry with the data passed from the intent
//        Bundle extras = intent.getExtras();
//        int mActType = extras.getInt(MapsActivity.KEY_ACTIVITY_TYPE,-1);
//        int mInputType = extras.getInt(MapsActivity.KEY_INPUT_TYPE,-1);
//
//        //If the types were passed in, set them.
//        if(mActType != -1)
//            LocationService.sCurEE.setmActivityType(mActType);
//        if(mActType != -1)
//            LocationService.sCurEE.setmInputType(mInputType);
//
//        //Set the time of the object to now.
//        LocationService.sCurEE.setmDateTime(new GregorianCalendar());
//
//        //Wipe out any previous information in the LocationList
//        LocationService.sCurEE.getmLocationList().clear();
//
//        //Start up the LocationManager and request updates as often as is scheduled
//        this.mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////        Log.d(TAG, "Requesting Location Updates!");
//        this.startLocationUpdates();

        //Set up the intent to be notified by the recevier
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NOTIFYSERVICESTOP);
//        this.mLSR = new LocationServiceReceiver();
//        registerReceiver(this.mLSR, intentFilter);

        // Send Notification
        String notificationTitle = "Jog My Memory!";
        String notificationText = "Tracking your adventure!";
        Intent mNotificationIntent = new Intent(getBaseContext(), HomeActivity.class);
        this.mPendingIntent
                = PendingIntent.getActivity(getBaseContext(),
                0, mNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(mPendingIntent).build();
        this.mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        this.mNotificationManager.notify(0, notification);

        return START_NOT_STICKY;
    }


    //Don't return a binder since we will use a BroadcastReceiver to notify MapsActivity
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //Unregister the receiver
//        this.unregisterReceiver(this.mLSR);
//        Log.d(TAG,"Destroying!");
        destroySelf();
        super.onDestroy();
    }

    @Override
    public void onCreate(){
        //Start up the LocationListener is done in the inner class at the bottom.
        //Reinitialize the ExerciseEntry on a new activity.
//        Log.d(TAG, sCurEE == null ? "Initializing the ExerciseEntry" : "Reinitializing the ExerciseEntry");
        //Initialize a new ExerciseEntry object
//        LocationService.sCurEE = new ExerciseEntry();
//        Log.d(TAG,"The sCurEE has been created!!!");


    }

//    /**
//     * Gets location updates from the best available provider.
//     */
//    protected void startLocationUpdates(){
//        //Ideas for this section from http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a/3145655#3145655
//        //Determine what location providers are available
//        Boolean mGpsIsEnabled = false;
//        try{mGpsIsEnabled = this.mLM.isProviderEnabled(LocationManager.GPS_PROVIDER);}
//        catch(Exception e){
//            Log.d(TAG, "GPS is not enabled");}
//        Boolean mNetworkIsEnabled = false;
//        try{mNetworkIsEnabled = this.mLM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}
//        catch(Exception e){
//            Log.d(TAG, "Network is not enabled");}
//
//        //Start both providers if they are available
//        if(mGpsIsEnabled) {
//            this.mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
//        }
//        if(mNetworkIsEnabled) {
//            this.mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
//        }
//
//        //Schedule to check for a location update every half second starting now
//        this.mLocationUpdateTimer = new Timer();
//        this.mLocationUpdateTimer.scheduleAtFixedRate(new ProviderCheckIn(),
//                new Date(), samplingInterval);
//
////        Criteria mCriteria = new Criteria();
////        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
////        String mBestProvider = this.mLM.getBestProvider(mCriteria,true);
////        if(!this.mLM.getAllProviders().contains(mBestProvider)){
////            Log.d(TAG,"The chosen provider wasn't available.");
////            mBestProvider = LocationManager.GPS_PROVIDER;
////        }
////        Log.d(TAG,"Using provider: "+mBestProvider);
////        this.mLM.requestLocationUpdates(mBestProvider,0,(float)0.0,locationListenerGps);
//        //Get the last known location.
////        this.findLastKnownLocation();
//    }
//
//    /**
//     * Update and broadcast the new location.
//     * @param location - new location
//     */
//    public void updateWithNewLocation(Location location){
//        //Add the location to the database
//        LocationService.sCurEE.addLocation(location);
//        lastSampled = location;
//
//        Intent broadcast = new Intent(MapsActivity.ACTION_NOTIFY_LOC_UPDATE);
////        Log.d(TAG, "Sending location update!");
////        Log.d(TAG,"Location = ("+location.getLatitude()+","+location.getLongitude()+")");
//        sendBroadcast(broadcast);
//    }

//    /**
//     * Stop the receiver if it is started
//     */
//    public void stopReceiver(){
//        try {
//            unregisterReceiver(this.mLSR);
//        }catch(IllegalArgumentException iae){
//            Log.d(TAG,"The receiver has already been destroyed!");
//        }
//    }

    /**
     * Finds the last known location and broadcasts it.
     */
//    public void findLastKnownLocation(){
////        Log.d(TAG, "Getting last known location!");
//        Location mLastKnownGps = mLM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Location mLastKnownNetwork = mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if(mLastKnownGps != null && mLastKnownNetwork != null) {
//            if(mLastKnownGps.getTime() > mLastKnownNetwork.getTime())
//                updateWithNewLocation(mLastKnownGps);
//            else
//                updateWithNewLocation(mLastKnownNetwork);
//        }
//        else if(mLastKnownGps != null){
//            updateWithNewLocation(mLastKnownGps);
//        }
//        else if(mLastKnownNetwork != null){
//            updateWithNewLocation(mLastKnownNetwork);
//        }
//        else {
//            Log.d(TAG, "Sorry! There is no last known location at this time.");
//        }
//    }

    private void destroySelf(){
//        stopReceiver();
//        Log.d(TAG,"Destroying self!");
//        mLM.removeUpdates(locationListenerGps);
//        mLM.removeUpdates(locationListenerNetwork);
//        mLocationUpdateTimer.cancel();

        mPendingIntent.cancel();
        mNotificationManager.cancelAll();
        stopSelf();
    }

//    /**
//     * Returns true if the locations are the same to within 3 decimal places
//     * for both latitude and longitude
//     * @param a - location a
//     * @param b - location b
//     * @return true if a and b are very close
//     */
//    public Boolean locationsAreSame(Location a, Location b){
//        NumberFormat mDf = new DecimalFormat();
//        mDf.setMaximumFractionDigits(3);
//        mDf.setMinimumFractionDigits(3);
//        mDf.setRoundingMode(RoundingMode.DOWN);
//        String mALat = mDf.format(a.getLatitude());
//        String mALng = mDf.format(a.getLongitude());
//        String mBLat = mDf.format(b.getLatitude());
//        String mBLng = mDf.format(b.getLongitude());
//
//        return mALat.equals(mBLat) && mALng.equals(mBLng);
//    }
//
//    /**
//      * Set up the LocationListener to pass the locations from Google Maps to our app.
//      * Callback method is updateWithNewLocation()
//      */
//    public final LocationListener locationListenerGps = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            //Updates only triggered from the timer
////            updateWithNewLocation(location);
//        }
//
//        public void onProviderDisabled(String provider) {
////            Log.d(TAG, "Provider " + provider + " was disabled!");
////            //TODO: Implement error-handling
////            //mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////            if (mLM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////                mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
////            } else {
////                mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerGps);
////            }
//        }
//
//        public void onProviderEnabled(String provider) {
////            Log.d(TAG, "Provider " + provider + " was enabled!");
////            //TODO: Implement error-handling
////            if (mLM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////                mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
////            } else {
////                mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerGps);
////            }
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            //TODO: Implement for error-handling when the provider may have been enabled or disabled.
////            Log.d(TAG, "Provider " + provider + "'s status changed to " + status);
//
//        }
//    };
//
//        /**
//         * Set up the LocationListener to pass the locations from Google Maps to our app.
//         * Callback method is updateWithNewLocation()
//         */
//        public final LocationListener locationListenerNetwork = new LocationListener(){
//            public void onLocationChanged(Location location){
//                //Upates only triggered from the timer
////                updateWithNewLocation(location); //TODO: Will need to make it only send a location update on the timer
//            }
//            public void onProviderDisabled(String provider) {
////                Log.d(TAG,"Provider "+provider+" was disabled!");
////                //TODO: Implement error-handling
////                if (mLM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////                    mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerNetwork);
////                } else {
////                    mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
////                }
//            }
//            public void onProviderEnabled(String provider) {
////                Log.d(TAG,"Provider "+provider+" was enabled!");
//                //TODO: Implement error-handling
////                if (mLM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////                    mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerNetwork);
////                } else {
////                    mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
////                }
//            }
//            public void onStatusChanged(String provider, int status, Bundle extras){
//                //TODO: Implement for error-handling when the provider may have been enabled or disabled.
////                Log.d(TAG,"Provider "+provider+"'s status changed to "+status);
//
//            }
//    };
//
//    class ProviderCheckIn extends TimerTask {
//
//        /**
//         * Updates the ExerciseEntry with the most recent update
//         */
//        @Override
//        public void run() {
//            //Determine what location providers are available
//            Boolean mGpsIsEnabled = false;
//            try{mGpsIsEnabled = mLM.isProviderEnabled(LocationManager.GPS_PROVIDER);}
//            catch(Exception e){
//                Log.d(TAG, "GPS is not enabled");}
//            Boolean mNetworkIsEnabled = false;
//            try{mNetworkIsEnabled = mLM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}
//            catch(Exception e){
//                Log.d(TAG, "Network is not enabled");}
//
////            //Turn off updates until we are done processing
////            if(mGpsIsEnabled) {
////                mLM.removeUpdates(locationListenerGps);
////            }
////            if(mNetworkIsEnabled) {
////                mLM.removeUpdates(locationListenerNetwork);
////            }
//
//            //Get the last locations from each provider
//            Location mGpsLoc = null, mNetLoc = null;
//
//            if(mGpsIsEnabled)
//                mGpsLoc=mLM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if(mNetworkIsEnabled)
//                mNetLoc=mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//            //if there are both values use the latest one
//            if(mGpsLoc!=null && mNetLoc!=null){
//                if(mGpsLoc.getTime()>mNetLoc.getTime()){
////                    Log.d(TAG,"Updating with GPS location!");
//                    if(!mGpsLoc.equals(lastSampled))
//                        updateWithNewLocation(mGpsLoc);
//                }
//                else{
////                    Log.d(TAG,"Updating with NETWORK location!");
//                    if(!mNetLoc.equals(lastSampled))
//                        updateWithNewLocation(mNetLoc);
//                }
//                return;
//            }
//            else if(mGpsLoc != null){
////                Log.d(TAG,"Updating with GPS location!");
//                if(!mGpsLoc.equals(lastSampled))
//                    updateWithNewLocation(mGpsLoc);
//            }
//            else{
////                Log.d(TAG,"Updating with NETWORK location!");
//                if(!mNetLoc.equals(lastSampled))
//                updateWithNewLocation(mNetLoc);
//            }
//
//
////            //Turn on available providers again, we are done processing
////            if(mGpsIsEnabled) {
////                mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
////            }
////            if(mNetworkIsEnabled) {
////                mLM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
////            }
//        }
//    }
//
////    /**
////     * Reads and handles signals from the MapsActivity
////     */
////    public class LocationServiceReceiver extends BroadcastReceiver {
////
////        @Override
////        public void onReceive(Context arg0, Intent arg1) {
////            //Triggers on ACTION_NOTIFYSERVICESTOP
////            Log.d(TAG, "Received Stop Service!");
////            destroySelf();
////        }
////    }
}
