package jog.my.memory;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import jog.my.memory.Debug.BlankFragment;
import jog.my.memory.Excursions.ExcursionsFragment;
import jog.my.memory.GPS.TraceFragment;
import jog.my.memory.Home.HomeFragment;
import jog.my.memory.Profile.ProfileFragment;
import jog.my.memory.adapter.NavDrawerListAdapter;
import jog.my.memory.images.GalleryFragment;
import jog.my.memory.model.NavDrawerItem;

public class HomeActivity extends FragmentActivity implements TraceFragment.onTraceFragmentClickedListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final String TAG = "MainActivity";
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public static boolean mDrawTrace = false;
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

    private ArrayList<Location> updates = new ArrayList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        updates.add(location);
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude()+.0008, location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
            displayView(position);
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
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
        ListFragment listfragment = null;
        switch (position) {
            case 0: //Home

                fragment = new HomeFragment();
                break;
            case 1: //Excursions
                listfragment = new ExcursionsFragment();
                //fragment = new ExcursionsFragment();
                //    fragment = new BlankFragment();
                break;
            case 2: //Photo Gallery
                fragment = new GalleryFragment();
                break;
            case 3: //Slideshows
                fragment = new BlankFragment();
                break;
            case 4: //Get Going!
//                fragment = new StartFragment();
                fragment = new TraceFragment();
                break;
            case 5: //User Profile
//                Intent i = new Intent(this, ProfileActivity.class);
//                startActivity(i);
                fragment = new ProfileFragment();

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
        else if(fragment.getClass() == HomeFragment.class){ //TODO: Check
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
}
