package jog.my.memory.GPS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import jog.my.memory.R;

//import cormack.devon.lab1_1.view.UnitConverter;

public class MapsActivity extends FragmentActivity /*implements SensorEventListener*/ {

    private static final String TAG = "MapsActivity";
    final static String ACTION_NOTIFY_LOC_UPDATE = "NotifyMapOfUpdate";
    public static final String KEY_ACTIVITY_TYPE = "activity_type";
    public static final String KEY_INPUT_TYPE = "input_type";
    //    private static Boolean sTrackingLocation = false;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mEndMarker;
    private Marker mStartMarker;
    private Polyline mRouteTrace;

    private LocationReceiver mLR;
    private IntentFilter mIF;

    private int mInputType;
    public static SensorManager sensorManager;
    private long lastUpdate;

//    private double classifiedType = -1; //Start classified type off as unkown.
//    private ArrayList<String> labelItems = new ArrayList<String>(3);

//    private double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
//    int blockSize = 0;
//    private ActivityDecisionTask mAsyncActivityDecision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(this.getIntent() != null &&
                this.getIntent().getExtras() != null){
            Bundle extras = this.getIntent().getExtras();
            this.mInputType = extras.getInt(StartFragment.KEY_INPUT);
        }

        Log.d(TAG, "This was called as a " + ((this.mInputType == 2) ? "AUTOMATIC" : "GPS"));

//        if(this.mInputType == StartFragment.AUTOMATIC){
//            //Start the activity detection service
//            this.startActivityDetection();
//        }

        //Start tracking the location and set up the ExerciseEntry
        this.startTrackingLocation();

//        //Start collecting data from the accelerometer if this is an AUTOMATIC
//        if(this.mInputType == StartFragment.AUTOMATIC) {
//            this.initializeSensorManager();
//        }

        setUpMapIfNeeded();

        //Create the LocationReceiver and the IntentFilter
        this.mLR = new LocationReceiver();
        this.mIF = new IntentFilter();
        this.mIF.addAction(ACTION_NOTIFY_LOC_UPDATE);

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "Restarting!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        Log.d(TAG, "Resuming!");
        //Register the BroadcastReceiver
        //Set up the intent to be notified by the recevier
        registerReceiver(this.mLR, this.mIF);
//        if(this.mInputType == StartFragment.AUTOMATIC) {
//            this.startSensorManager();
//        }
    }

    /**
     * Unregister the receiver when the application is not in focus
     */
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "Now paused");
        unregisterReceiver(this.mLR);
//        if(this.mInputType == StartFragment.AUTOMATIC) {
//            this.stopSensorManager();
//        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "Now stopped");
    }

    @Override
    protected void onDestroy(){
        stopService(new Intent(this,LocationService.class));
//        if(this.sensorManager != null) {
//            this.sensorManager.unregisterListener(this.mAsyncActivityDecision);
//        }
        Log.d(TAG, "Now destroyed");
        super.onDestroy();
    }

//    private void startActivityDetection(){
//        Log.d(TAG, "Starting the Activity Detection service.");
//        //Get the values to pass to the service (won't be there on resume)
//        Intent mActivityDectionService = new Intent(this, SensorsService.class);
//        startService(mActivityDectionService);
//    }

    //Set up the Sensor Manager
//    private void initializeSensorManager(){
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
////        labelItems.add(Globals.CLASS_LABEL_STANDING);
////        labelItems.add(Globals.CLASS_LABEL_WALKING);
////        labelItems.add(Globals.CLASS_LABEL_RUNNING);
////        labelItems.add(Globals.CLASS_LABEL_OTHER);
////        this.startSensorManager();
////        this.lastUpdate = System.currentTimeMillis();
//
////        this.mAsyncActivityDecision = new ActivityDecisionTask();
////        this.startSensorManager();
//    }

//    private void startSensorManager(){
////        this.sensorManager.registerListener(this,
////                this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
////                SensorManager.SENSOR_DELAY_NORMAL);
//
//        try{
//            this.mAsyncActivityDecision.execute();
//        }catch(IllegalStateException ise){
//            Log.d(TAG, "The activity was already started!");
//        }
//    }

    //Stop tracking the location
//    private void stopSensorManager(){
//
////        this.sensorManager.unregisterListener(this);
//        if(!this.mAsyncActivityDecision.isCancelled()) {
//            Log.d(TAG, "Stopped sensor manager!");
//            this.mAsyncActivityDecision.cancel(true); //true means may continue if running
//        }
//    }

    //Start tracking the location
    private void startTrackingLocation(){
        Log.d(TAG, "Starting the service.");
        //Get the values to pass to the service (won't be there on resume)
        int mActType = -1, mInputType = -1;
        if(this.getIntent().getExtras() != null) {
            //Set the values of Input and Activity Type
            Bundle extras = this.getIntent().getExtras();
            mActType = extras.getInt(StartFragment.KEY_ACTIVITY);
            mInputType = extras.getInt(StartFragment.KEY_INPUT);
        }
        Intent mLocService = new Intent(this, LocationService.class);
        mLocService.putExtra(this.KEY_ACTIVITY_TYPE,mActType);
        mLocService.putExtra(this.KEY_INPUT_TYPE,mInputType);
        startService(mLocService); //TODO: here, pass these values to the LocationService so we can move EE declaration and not have a race condition.
//        LocationService.sCurEE = new ExerciseEntry();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //TODO: Make sure that this solves the update issue when it's been in the background.
        if(LocationService.sCurEE != null
                && LocationService.sCurEE.getmLocationList()!= null
                && LocationService.sCurEE.getmLocationList().size() != 0){
            //Update the screen with the newest information
            this.handleNewLocationInfo();
        }
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .build();
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
    }


    /**
     * Updates the map and the displayed information
     */
    void handleNewLocationInfo(){
        this.updateDisplayWithNewLocation(LocationService.sCurEE);
        this.updateMapWithNewLocation(LocationService.sCurEE);
    }

    /**
     * Moves the map to the last location in ee
     * Updates polygons with all locations in ee
     * @param ee - ExerciseEntry
     */
    private void updateMapWithNewLocation(ExerciseEntry ee){
        //TODO: Implement this!
        //TODO: Add in the logic for resume! We could have more than 1 update to process! -> We can replace this with one method that moves and draws the polygons! Store the drawn locations in this class so that we can diff against the locationlist that we get from ee
        //Get the last element in the list
        int mNumLoc = ee.getmLocationList().size();
//        Log.d(TAG,"Total locations received = "+mNumLoc);
//        Log.d(TAG,"sCurEEList[0] = "+LocationService.sCurEE.getmLocationList().toArray()[0]);
        //If there are locations, update the location of the object by gliding to new location
        if(mNumLoc >= 1) {
            if(this.mStartMarker == null){
                this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        ee.getmLocationList().get(0).toLatLng(),(float)18.0));
            }
            if (this.mStartMarker != null) {
                this.mStartMarker.remove();
            }
            //Add a start marker at the initial location
            this.mStartMarker = mMap.addMarker(new MarkerOptions()
                    .position(ee.getmLocationList().get(0).toLatLng())
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            if(this.mEndMarker != null){
                //Remove the old end marker
                this.mEndMarker.remove();
            }
            //Add an end marker at the last known location
            int mEnd = ee.getmLocationList().size()-1;
            LatLng endLocation = ee.getmLocationList()
                    .get(mEnd).toLatLng();
            this.mEndMarker = mMap.addMarker(new MarkerOptions()
                    .position(endLocation)
                    .title("End")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //Move the camera to the current location
            this.mMap.animateCamera(CameraUpdateFactory.newLatLng(endLocation));

            //Remove the old polyline
            if(this.mRouteTrace != null){
                this.mRouteTrace.remove();
            }
            //Construct a polyline through all of the points up to mEnd
            PolylineOptions mPO = new PolylineOptions().width(5);
            for(int i=0; i<=mEnd;i++){
                //Add the point to the polyline
                mPO.add(ee.getmLocationList().get(i).toLatLng() );
            }
            //Write the new polyline to the map
            this.mRouteTrace = this.mMap.addPolyline(mPO);
        }
    }

//    /**
//     * Draw a polyline between all of the locations.
//     * @param ee - ExerciseEntry
//     */
//    private void updateRouteTrace(ExerciseEntry ee){
//
//    }

    /**
     * Updates the status display with the new location in ee
     *
     * @param ee - ExerciseEntry
     */
    private void updateDisplayWithNewLocation(ExerciseEntry ee){
        //Set the current type
        String mCurType = "";
//        if(this.mInputType == StartFragment.AUTOMATIC){
//            mCurType = "Type: "
//                    +this.mAsyncActivityDecision.getClassification();
//        }
        if(this.mInputType == StartFragment.GPS) {
            mCurType = "Type: " + getResources()
                    .getStringArray(R.array.ui_activityType_spinner_entries)[ee.getmActivityType()];
        }

        //Ensure the units are displayed correctly
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mKey = this.getString(R.string.list_unit_preferences);
        String mCurrentUnits = mPrefs.getString(mKey, "Metric");

        //Set the values based on what unit preference the user has
        String mAvgSpeed, mCurSpeed, mDistance, mClimb;
        if(mCurrentUnits.equals("Metric")) {
            mAvgSpeed = "Avg Speed: "
                    + UnitConverter.convertImperialToMetric(ee.getmAvgSpeed())
                    +" km/h";
            mCurSpeed = "Cur Speed: "
                    + UnitConverter.convertImperialToMetric(ee.getmSpeed())
                    +" km/h";
            mDistance = "Distance: "
                    + UnitConverter.convertImperialToMetric(ee.getmDistance())
                    +" km";
            mClimb = "Climb: "
                    + UnitConverter.convertImperialToMetric(ee.getmClimb())
                    +" km";
        }
        else{
            mAvgSpeed = "Avg Speed: "+ ee.getmAvgSpeed()+" mph";
            mCurSpeed = "Cur Speed: "+ee.getmSpeed()+" mph";
            mDistance = "Distance: "+ee.getmDistance()+" miles";
            mClimb = "Climb: "+ee.getmClimb()+" miles";
        }

        //Set the calories
        String mCalorie = "Calorie: "+ee.getmCalorie();

        // Construct the entire string:
        String total = mCurType+"\n"+mAvgSpeed+"\n"
                +mCurSpeed+"\n"+mClimb+"\n"+mCalorie+"\n"+mDistance;
        //Set the text in the display
        ((TextView) findViewById(R.id.type_stats)).setText(total);
    }



    /*** UI Button Callbacks **/
    public void onSaveClicked(View v){
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
        //TODO: Re-implement database handling how we want it
//        //Save the exercise to the database
//        new ExerciseEntryDbHelper(this).insertEntry(LocationService.sCurEE);
//        //Stop the LocationService
////        this.stopLocationService();
        //Exit the activity
        finish();

        //TODO: Implement saving information to database here!
    }

    public void onCancelClicked(View v){
        Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT);
        //End the activity
        finish();
    }

    /**
     * Finish the LocationService as well as killing the application
     */
    @Override
    public void onBackPressed(){
        //Stop the LocationService
//        this.stopLocationService();
        super.onBackPressed();
    }

    /**
     * Stops the LocationService
     */
    private void stopLocationService() {
        mMap.setMyLocationEnabled(false);
        Intent broadcast = new Intent(LocationService.ACTION_NOTIFYSERVICESTOP);
        Log.d(TAG, "Sending Stop Service!");
        sendBroadcast(broadcast);
    }

/*    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // Get the movement values
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            Log.d(TAG,"Accelerometer reads: ("+x+","+y+","+z+")");

            //Calculate magnitude
            double m = Math.sqrt(x*x+y*y+z*z);

            //Add the reading to the accelerometer block
            this.accBlock[this.blockSize++] = m;

            //If the block is full, process the readings
            if(this.blockSize >= 64){
                Log.d(TAG,"Reading calculated!");
                //Start filling a new array from 0
                blockSize = 0;

                //Calculate and set the maximum
                double max = .0;
                for (int i=0;i<this.blockSize;i++) {
                    double val = this.accBlock[i];
                    if (max < val) {
                        max = val;
                    }
                }

                //Calculate the FFT
                double[] re = accBlock;
                double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
                FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
                fft.fft(re,im);

                //Generate the feature vector from the FFT output
                ArrayList<Double> featVect = new ArrayList<Double>();
                for (int i = 0; i < re.length; i++) {
                    // Compute each coefficient
                    double mag = Math.sqrt(re[i] * re[i] + im[i]* im[i]);
                    // Adding the computed FFT coefficient to the
                    // featVect
                    featVect.add(mag);
                    // Clear the field
                    im[i] = .0;
                    Log.d(TAG,"FeatureVect i = "+i);
                }

                // Finally, append max after frequency components
                featVect.add(max);
                Log.d(TAG,"Just added all the features, now max");

                //Pass the feature vector to the classifier
                try{
                    this.classifiedType = WekaClassifier.classify(featVect.toArray());
                }catch(Exception e){
                    Log.d(TAG,"Exception thrown during classification!");
                }

                Log.d(TAG, "The classifier type is: "+this.labelItems.get((int)this.classifiedType));

                //Set the activity type to the classified type
                if(this.labelItems.get((int)this.classifiedType) == Globals.CLASS_LABEL_OTHER){
                    LocationService.sCurEE.setmActivityType(this.getResources()
                            .getStringArray(R.array.ui_activityType_spinner_entries).length-1);
                }
                else if(this.labelItems.get((int)this.classifiedType) == Globals.CLASS_LABEL_RUNNING){
                    LocationService.sCurEE.setmActivityType(0);
                }
                else if(this.labelItems.get((int)this.classifiedType) == Globals.CLASS_LABEL_WALKING){
                    LocationService.sCurEE.setmActivityType(1);
                }
                else if(this.labelItems.get((int)this.classifiedType) == Globals.CLASS_LABEL_STANDING){
                    LocationService.sCurEE.setmActivityType(2);
                }

        }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        ; //Do nothing
    }
    */

    /**
     * Inner class that notifies MapsActivity when the location is updated.
     */
    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Triggers on ACTION_NOTIFY_LOC_UPDATE
                Log.d(TAG, "Just received a location update!");
                handleNewLocationInfo();
        }
    }
}
