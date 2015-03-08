package jog.my.memory.Excursions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jog.my.memory.R;
import jog.my.memory.database.Excursion;
import jog.my.memory.database.ExcursionDBHelper;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;
import jog.my.memory.gcm.ServerUtilities;

public class MapDisplayActivity extends FragmentActivity {

    private static final String TAG = "MapDisplayActivity";
    final static String ACTION_NOTIFY_LOC_UPDATE = "NotifyMapOfUpdate";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mEndMarker;
    private Marker mStartMarker;
    private Polyline mRouteTrace;

    private Excursion mEeToDisplay;
    private int position;
    private int inputType;
    private long id;
    private ArrayList<Picture> mShownImages = new ArrayList<Picture>();
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private double imageScale = 0.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);

        //Get the data to display out of the bundle.
        Bundle extras = this.getIntent().getExtras();
//        //Get the position of the activity in the database
//        this.position = extras.getInt("position");

//        this.mEeToDisplay = (new ExerciseEntryDbHelper(this)).fetchEntryByIndex(this.position);
        this.mEeToDisplay = (Excursion)extras.get("ee");
        this.id = extras.getLong("row");

        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_display, menu);
        return true;
    }

    /**
     * Handles the delete button to delete entries
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteEntryButton) {
            Intent data = new Intent();
            data.putExtra("deleteEntry",true);
            data.putExtra("position",this.position);
            setResult(Activity.RESULT_OK, data);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Unregister the receiver when the application is not in focus
     */
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
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
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        this.setupMapWithStoredLocation();
    }


    /**
     * Updates the map and the displayed information
     */
    void setupMapWithStoredLocation(){
//        this.updateDisplayWithNewLocation(this.mEeToDisplay); //TODO: Make this method display our location, then call it.
        this.updateMapWithNewLocation(this.mEeToDisplay);
    }

    /**
     * Moves the map to the last location in ee
     * Updates polygons with all locations in ee
     * @param ee - ExerciseEntry
     */
    private void updateMapWithNewLocation(Excursion ee){
        //Get the last element in the list
        int mNumLoc = ee.getmLocationList().size();
        //If there are locations, update the location of the object by gliding to new location
        Log.d(TAG,"Number of locations is: "+mNumLoc);
        if(mNumLoc >= 1) {
            if (this.mStartMarker == null) {
                //Add a start marker at the initial location, and go to the location
                this.mStartMarker = mMap.addMarker(new MarkerOptions()
                        .position(ee.getmLocationList().get(0).toLatLng())
                        .title("Start")
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));
                this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        ee.getmLocationList().get(ee.getmLocationList().size() - 1).toLatLng(),
                        (float) 18.0));

            }
            if(this.mEndMarker != null){
                //Remove the old end marker
                this.mEndMarker.remove();
            }
            //Add an end marker at the last known location
            this.mEndMarker = mMap.addMarker(new MarkerOptions()
                    .position(ee.getmLocationList().get(ee.getmLocationList().size()-1).toLatLng())
                    .title("End")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //Move the camera to the current location
            this.mMap.animateCamera(CameraUpdateFactory.newLatLng(
                    ee.getmLocationList().get(ee.getmLocationList().size() - 1).toLatLng()));
            //Update the route trace.
            this.updateRouteTrace(ee);
            //Update all of the markers on the map
            this.updateShownImages();
        }
    }

    /**
     * Draw a polyline between all of the locations.
     * @param ee - ExerciseEntry
     */
    private void updateRouteTrace(Excursion ee){
        //Construct a polyline through all of the points.
        PolylineOptions mPO = new PolylineOptions().width(5);
        for(int i=0; i<ee.getmLocationList().size();i++){
            //Add the point to the polyline
            mPO.add(ee.getmLocationList().get(i).toLatLng());
        }
        this.mMap.addPolyline(mPO);
    }

    public void onDeleteExcursionButtonPress(View v) {
        ExcursionDBHelper mydbHelper = new ExcursionDBHelper(this);
        mydbHelper.open();
        mydbHelper.removeEntry(this.id);
        mydbHelper.close();

        //Delete all pictures associated with excursion
        PicturesDBHelper picdbHelper = new PicturesDBHelper(this);
        picdbHelper.open();
        ArrayList<Picture> pics = picdbHelper.fetchEntriesByExcursionID(this.id);
        Log.d("DisplayExcursion", "" + pics);

        for (Picture pic : pics) {
            picdbHelper.removeEntry(pic.getId());
            Log.d("DisplayExcursion", "A picture was removed!");
        }
        picdbHelper.close();

        finish();
    }

    private void updateShownImages(){
        this.updateShownImages(this.mEeToDisplay.getmID());
    }

    /**
     * Update the map with the images shown
     */
    public void updateShownImages(long excursionID){
        //Clear all of the markers on the map
        this.clearAllMarkers();
        //Update the images that are shown on the map
        this.mShownImages = (new PicturesDBHelper(this)).fetchEntriesByExcursionID(excursionID);
        //Show all of the images as icons at the locations that they were taken
        for(Picture pic : this.mShownImages){
            this.addPictureToMap(pic);
        }
    }

    public void addPictureToMap(Picture pic){
        Location location = pic.getmLocation();
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        Bitmap bmp = pic.getmImage();
        bmp = Bitmap.createScaledBitmap(bmp,new Double(this.imageScale*bmp.getWidth()).intValue(),new Double(this.imageScale*bmp.getHeight()).intValue(),false);
        this.mMarkers.add(
                this.mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                ));
        Log.d(TAG,"Created new marker!");
    }

    public void clearAllMarkers(){
        //Clear all markers from the map
        this.mMap.clear();
        //Clear all markers from the list
        this.mMarkers.clear();
    }

    public void onShareClicked(View v){
        updateServer();
    }

    /*** Server upload code ***/

    /**
     * Updates the server with the database
     */
    private void updateServer(){
        //Get the Arraylist of Exercise entries currently in the database
//        ArrayList<Excursion> listEE = new ExcursionDBHelper(this).fetchEntries();

        ArrayList<Excursion> listEE = new ArrayList<>();
        listEE.add(this.mEeToDisplay);
        //Build a JSONArray of JSONObjects that contains the ArrayList of ExerciseEntries.
        String jsonString = this.buildJsonToSend(listEE); //TODO: This needs to be changed to upload the files

        //Post message and then refresh
        postMsg(jsonString);

//        //Post the images to the server
//        this.postImage(this.mShownImages);
    }

    private void postImage(ArrayList<Picture> listPics){
        for(Picture pic : listPics){
            //Get the picture
            Bitmap bmp = pic.getmImage();
            //Save it to a file and open that file
            String picFileLoc = PictureUtilities.saveToFile(bmp);
            File picFile = new File(picFileLoc);
            //Post the file to the server
            ServerUtilities.postFile(
                    getBaseContext(),
                    getResources().getString(R.string.server_addr),
                    Uri.fromFile(picFile));

            //Get the location
            Location location = pic.getmLocation();
        }
    }

    /**
     * Builds a JsonArray of JsonObjects to send to the server.
     * @param listEE - the list of Exercise Entries
     * @return a JSON entity appropriate for sending to a server
     */
    private String buildJsonToSend(ArrayList<Excursion> listEE){
        JSONArray jsonArray = new JSONArray();

        for(Excursion ee : listEE){
            try{
                //Create the JSON object
                JSONObject jsonObject = new JSONObject();
                //Populate it with all the fields in ExerciseEntity (server-side object)
                jsonObject.put("id", (long)ee.getmID());
//                jsonObject.put("inputType", ee.getmInputType());
//                jsonObject.put("activityType", ee.getmActivityType());
                jsonObject.put("timeStamp", ee.getmTimeStamp());
//                jsonObject.put("duration", ee.getmDuration());
//                jsonObject.put("distance", ee.getmDistance());
//                jsonObject.put("avgSpeed", ee.getmAvgSpeed());
//                jsonObject.put("calories", ee.getmCalorie());
//                jsonObject.put("climb", ee.getmClimb());
//                jsonObject.put("heartRate", ee.getmHeartRate());
                jsonObject.put("name", ee.getmName());
                //Add the object to the JSONArray
                jsonArray.put(jsonObject);
            }
            catch(Exception e){
                Log.i(TAG,"Exception in JSON creation, it failed.");
            }

        }
        //Return the array of JSON objects in compact JSON String form.
        try{Log.d(TAG, "Just build JSON array: "+jsonArray.toString(4));}catch(Exception e){};
        return jsonArray.toString();
    }

    private void postMsg(String msg) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String url = getString(R.string.server_addr) + "/post.do";
                String res = "";
                Map<String, String> params = new HashMap<String, String>();

                Log.d(TAG,"post_text mapped to arg0[0] = "+arg0[0]);
                params.put("post_text", arg0[0]);
                params.put("from", "phone");

//                params.put(GregorianCalendar to milliseconds.)
                try {
                    res = ServerUtilities.post(url, params);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String res) {
                //Refresh the history list for the HistoryFragment
                refreshPostHistory();
            }

        }.execute(msg);
    }

    /**
     * Supports refreshing the post history by spinning up
     * a background process that posts to the server.
     */
    private void refreshPostHistory() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {
                String url = getString(R.string.server_addr)
                        + "/get_history.do";
                String res = "";
                Map<String, String> params = new HashMap<String, String>();
                try {
                    res = ServerUtilities.post(url, params);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String res) {
                if (!res.equals("")) {
                    Log.d(TAG,"Successfully received something! It was:");
                    Log.d(TAG,""+res);
                }
            }

        }.execute();
    }

//    /**
//     * Check the device to make sure it has the Google Play Services APK. If it
//     * doesn't, display a dialog that allows users to download the APK from the
//     * Google Play Store or enable it in the device's system settings.
//     */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Gets the current registration ID for application on GCM service.
//     * <p>
//     * If result is empty, the app needs to register.
//     *
//     * @return registration ID, or empty string if there is no existing
//     *         registration ID.
//     */
//    private String getRegistrationId(Context context) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new
//        // app version.
//        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
//                Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            return "";
//        }
//        return registrationId;
//    }
//
//    /**
//     * @return Application's {@code SharedPreferences}.
//     */
//    private SharedPreferences getGCMPreferences(Context context) {
//        // This sample app persists the registration ID in shared preferences,
//        // but
//        // how you store the regID in your app is up to you.
//        return getSharedPreferences(MapDisplayActivity.class.getSimpleName(),
//                Context.MODE_PRIVATE);
//    }
//
//    /**
//     * @return Application's version code from the {@code PackageManager}.
//     */
//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // should never happen
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }
//
//    /**
//     * Registers the application with GCM servers asynchronously.
//     * <p>
//     * Stores the registration ID and app versionCode in the application's
//     * shared preferences.
//     */
//
//    private GoogleCloudMessaging gcm;
//    private String regid;
//    private Context context;
//    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    public static final String PROPERTY_REG_ID = "registration_id";
//    private static final String PROPERTY_APP_VERSION = "appVersion";
//
//    private String SENDER_ID = "1069779036848"; //TODO: This needs to be changed every time you register with a new app!
//
//    private void registerInBackground() {
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                String msg = "";
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
//                    }
//                    regid = gcm.register(SENDER_ID);
//                    msg = "Device registered, registration ID=" + regid;
//
//                    // You should send the registration ID to your server over
//                    // HTTP,
//                    // so it can use GCM/HTTP or CCS to send messages to your
//                    // app.
//                    // The request to your server should be authenticated if
//                    // your app
//                    // is using accounts.
//                    ServerUtilities.sendRegistrationIdToBackend(getBaseContext(), regid);
//
//
//                    // Persist the regID - no need to register again.
//                    storeRegistrationId(context, regid);
//                } catch (IOException ex) {
//                    msg = "Error :" + ex.getMessage();
//                    // If there is an error, don't just keep trying to register.
//                    // Require the user to click a button again, or perform
//                    // exponential back-off.
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                Log.i(TAG, "gcm register msg: " + msg);
//            }
//        }.execute(null, null, null);
//    }
//
//    /**
//     * Stores the registration ID and app versionCode in the application's
//     * {@code SharedPreferences}.
//     *
//     * @param context
//     *            application's context.
//     * @param regId
//     *            registration ID
//     */
//    private void storeRegistrationId(Context context, String regId) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        int appVersion = getAppVersion(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(PROPERTY_REG_ID, regId);
//        editor.putInt(PROPERTY_APP_VERSION, appVersion);
//        editor.commit();
//    }

}
