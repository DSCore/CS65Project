package jog.my.memory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jog.my.memory.Excursions.ExcursionsFragment;
import jog.my.memory.GPS.TraceFragment;
import jog.my.memory.Gallery.GalleryFragment;
import jog.my.memory.Helpers.MyDialogFragment;
import jog.my.memory.Home.HomeFragment;
import jog.my.memory.Profile.ProfileFragment;
import jog.my.memory.Slideshow.SlideshowGalleryFragment;
import jog.my.memory.adapter.NavDrawerListAdapter;
import jog.my.memory.database.Excursion;
import jog.my.memory.database.ExcursionDBHelper;
import jog.my.memory.database.MyLatLng;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;
import jog.my.memory.gcm.ServerUtilities;
import jog.my.memory.model.NavDrawerItem;

public class HomeActivity extends FragmentActivity implements TraceFragment.onTraceFragmentClickedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final String TAG = "MainActivity";
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public static boolean mDrawTrace = false;
    public static boolean savePhoto = false;
    Polyline mRouteTrace;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    public String[] navMenuTitles;
    private TypedArray navMenuIcons;

    public ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private long excursionID;

    //Excursion and GPS information
    private ArrayList<Location> updates = new ArrayList<Location>();
    private Excursion currentExcursion;
    private long nextDBID;

    private Timer mTimer;
    private TimerTask mUpdateMapView;



    //images on the map.
    private ArrayList<Picture> mShownImages = new ArrayList<Picture>();
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private double imageScale = 0.2;

    public Excursion getCurrentExcursion(){
        return currentExcursion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_home);

        context = this;

        getWindow().setFeatureInt(Window.FEATURE_ACTION_BAR, R.layout.window_title);
        setUpMapIfNeeded();

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array TODO: Update these comments to match our app
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22")); //These are for counters if we want them.
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            updates.add(location);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude()+.0008, location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Add the new location to the updates list
                Log.d(TAG,"Add location to updates: ("+location.getLatitude()+", "+location.getLongitude()+")");
                updates.add(location);
                //Remove the polyline
                if(mRouteTrace != null){
                    mRouteTrace.remove();
                }
                //Draw the polyline if we are tracing
                if(mDrawTrace) {
                    Log.d(TAG, "Drawing polyline");
                    PolylineOptions mPO = new PolylineOptions().width(10).color(Color.RED).geodesic(true);
                    for (Location ll : updates) {
                        mPO.add(new LatLng(ll.getLatitude(), ll.getLongitude()));
                    }
                    mRouteTrace = mMap.addPolyline(mPO);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(location.getLatitude(),location.getLongitude())));
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //Start updating the map's location every five seconds
        this.startUpdateMapView();
    }

    /**
     * Updates the location of the map every 5 seconds. Threaded.
     */
    public void startUpdateMapView(){
        //Define a TimerTask to update the location of the data points
        this.mTimer = new Timer();
        this.mUpdateMapView = new TimerTask() {
            @Override
            public void run() {
                if(updates != null && updates.size() > 0) {
                    Location location = updates.get(updates.size() - 1);
                    final CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude() + .0008, location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });
                }
            }
        };
        this.mTimer.scheduleAtFixedRate(mUpdateMapView,0,5*1000);
    }

    /**
     * @return the list of location updates the phone has received
     */
    public ArrayList<Location> getUpdates(){
        return this.updates;
    }

    /**
     * Clears the list of location updates
     */
    public void clearUpdates(){
        this.updates.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMapVisible(true);
        setUpMapIfNeeded();
        if( this.mDrawTrace ){
            displayView(4);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setMapVisible(false);
    }

    @Override
    protected void onDestroy(){
        try {
            ((NotificationManager) getSystemService(this.NOTIFICATION_SERVICE))
                    .cancel(TraceFragment.TRACING_NOTIFICATION);
        }catch(Exception e) {Log.e(TAG,"Couldn't stop tracking notification");}
        super.onDestroy();
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
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
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
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker")); //Commented since we don't need an origin marker
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            if(mDrawTrace){
                if(!savePhoto){
                    displayView(position);
                }
            }else{
                displayView(position);
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                //TODO: Open the profile activity or user preferences in edit mode
                Log.d(TAG, "This will open user prefs!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        //Pop back to the home screen
            getFragmentManager().popBackStack();
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0: //Home
                fragment = new HomeFragment();
                break;
            case 1: //Excursions
                fragment = new ExcursionsFragment();
                break;
            case 2: //Photo Gallery
                fragment = new GalleryFragment();
                break;
            case 3: //Slideshows
                fragment = new SlideshowGalleryFragment();
                break;
            case 4: //Get Going!
                fragment = new TraceFragment();
                break;
            case 5: //User Profile
                fragment = new ProfileFragment();
                break;
            default:
                break;
        }

        if (fragment != null && fragment.getClass() != HomeFragment.class) {
            //Open the fragment and add it to the backstack
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, "last")
                    .addToBackStack("prev").commit();
            Log.d(TAG,"Added: Back stack contains: "+getFragmentManager().getBackStackEntryCount());

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        }
        else if(fragment != null && fragment.getClass() == HomeFragment.class){ //TODO: Check
            //Open the fragment but don't add it to the backstack
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, "last").commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else {
            // error in creating fragment
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        if(title.toString().equalsIgnoreCase("home"))
            getActionBar().setIcon(R.drawable.ic_home);
        else if(title.toString().equalsIgnoreCase("excursions"))
            getActionBar().setIcon(R.drawable.ic_list);
        else if(title.toString().equalsIgnoreCase("photo gallery"))
            getActionBar().setIcon(R.drawable.ic_photos);
        else if(title.toString().equalsIgnoreCase("slideshows"))
            getActionBar().setIcon(R.drawable.ic_gallery);
        else if(title.toString().equalsIgnoreCase("get going!"))
            getActionBar().setIcon(R.drawable.ic_time);
        else if(title.toString().equalsIgnoreCase("user profile"))
            getActionBar().setIcon(R.drawable.ic_profile);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Changes the map visibility
     */
    public void setMapVisible(boolean visible){
        if(visible){
            findViewById(R.id.map).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.map).setVisibility(View.INVISIBLE);
        }
    }

    public void setDrawTrace(boolean drawTrace){
        //Turn on or off the HomeFragment updates
        if(drawTrace){
            this.mTimer.cancel();
        }
        else{
            this.startUpdateMapView();
        }
        this.mDrawTrace = drawTrace;
    }

    @Override
    /**
     * Allows navigation back through the fragments that the user has accessed.
     */
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() != 0) {
            //Get the last entry in the fragment manager
            getFragmentManager().popBackStack();
            HomeActivity mHomeActivity = this;
            Log.d(TAG,"NavDrawerItems: "+mHomeActivity.navDrawerItems);
            int position = 0; //Go back to the HomeFragment
            Log.d(TAG,"ProfileFragment is position: "+position);
            mHomeActivity.mDrawerList.setItemChecked(position, true);
            mHomeActivity.mDrawerList.setSelection(position);
            mHomeActivity.setTitle(mHomeActivity.navMenuTitles[position]);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Start up a new excursion
     */
    private String m_Text = "";
    private MyDialogFragment mDialogFrag;
    public void startNewExcursion(){
        ArrayList<MyLatLng> mLocationList = new ArrayList<MyLatLng>();
        MyLatLng latLng = new MyLatLng(0,0);
        mLocationList.add(latLng);
        ArrayList<Long> mPictureIDs = new ArrayList<Long>(0);
        Excursion dummy = new Excursion(new GregorianCalendar(), 0, 0,  mLocationList,  mPictureIDs, "Dummy");
        ExcursionDBHelper dbHelp = new ExcursionDBHelper(this);
        dbHelp.open();
        long dummyID = (dbHelp.insertEntry(dummy));
        dbHelp.removeEntry(dummyID);
        nextDBID = dummyID + 1;
        dbHelp.close();

        final Excursion nCurrentExcursion = new Excursion();
        this.currentExcursion= new Excursion();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excursion name:");
        builder.setMessage("Enter excursion name here");
        final EditText mInput = new EditText(this);

        mInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(mInput);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mInput.getText().toString().length()==0) {
                    m_Text = "Excursion number: " + getNextDBID();;
                } else {
                    m_Text = mInput.getText().toString();

                }
                nCurrentExcursion.setmName(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = "Excursion number: "+ getNextDBID();
                nCurrentExcursion.setmName(m_Text);
                dialog.cancel();

            }
        });

        builder.show();

        this.currentExcursion = nCurrentExcursion;

        //TODO: Trigger a DialogFragment to get this!
        Date date = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFormatted = formatter.format(date);
        this.currentExcursion.setmTimeStamp(dateFormatted);
        Log.d(TAG,"Created new Excursion"+this.currentExcursion);
    }

    public long getNextDBID(){
        return nextDBID;
    }

    /**
     * Stop the excursion and save it to the database
     */
    public void stopCurrentExcursion(){
        //Populate the database with locations
        this.currentExcursion.setmLocationList(fromLocationListToLatLngList(this.updates));
        //TODO: distance
        //TODO: duration
        //Add the Excursion to the database
        (new ExcursionDBHelper(this)).insertEntry(this.currentExcursion);
        Log.d(TAG,"Added Excursion to the database "+this.currentExcursion);
    }

    /**
     * Convert from list of Locations to list of LatLng
     * @param locationList - list of Locations
     * @return list of LatLng from list of locations
     */
    private ArrayList<MyLatLng> fromLocationListToLatLngList(ArrayList<Location> locationList){
        ArrayList<MyLatLng> latLngList = new ArrayList<MyLatLng>();
        for( Location entry : locationList ){
            latLngList.add(new MyLatLng(entry.getLatitude(),entry.getLongitude()));
        }
        return latLngList;
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

    public static void hideKeyboard(Context context){
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        //Check if there is a view in focus
        View view = ((Activity) context).getCurrentFocus();
        if(view != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

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

    public void clearAllMarkers(){
        //Clear all markers from the map
        this.mMap.clear();
        //Clear all markers from the list
        this.mMarkers.clear();
    }



    /*** Server upload code ***/

    /**
     * Updates the server with the database
     */
    private void updateServer(){
        //Get the Arraylist of Exercise entries currently in the database
        ArrayList<Excursion> listEE = new ExcursionDBHelper(this).fetchEntries();

        //Build a JSONArray of JSONObjects that contains the ArrayList of ExerciseEntries.
        String jsonString = this.buildJsonToSend(listEE); //TODO: This needs to be changed to upload the files

        //Post message and then refresh
        postMsg(jsonString);
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
                jsonObject.put("duration", ee.getmDuration());
                jsonObject.put("distance", ee.getmDistance());
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(HomeActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */

    private GoogleCloudMessaging gcm;
    private String regid;
    private Context context;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private String SENDER_ID = "1069779036848"; //TODO: This needs to be changed every time you register with a new app!

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    ServerUtilities.sendRegistrationIdToBackend(getBaseContext(), regid);


                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, "gcm register msg: " + msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
