package jog.my.memory.Home;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;

import jog.my.memory.GPS.TraceFragment;
import jog.my.memory.HomeActivity;
import jog.my.memory.Profile.ProfileFragment;
import jog.my.memory.R;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public HomeFragment(){}

    private Context context;

    private TextView mTvFavorites;
    private TextView mTvExcursions;
    private TextView mTvGallery;
    private ImageView mProfilePhoto;
    private TextView mProfileName;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        this.context = inflater.getContext();

        Button mQuickStart = (Button)rootView.findViewById(R.id.button_quick_start);
        mQuickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked!");
//                TODO: THIS BLOCK WAS FROM STEVE AND MAYA, WE NEED TO GO OVER THIS AS A GROUP
//                //TODO: Make this start up the adventure activity!!
//                //Todo: At the moment, it just adds a canned excursion to the DB
//                Excursion exc;
//                ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
//                ArrayList<Long> picids = new ArrayList<Long>();
//
//                exc = new Excursion(new GregorianCalendar(), 0 , 0 , latlngs, picids, "EXCURSION NAME");
//                ExcursionDBHelper mDBHelper = new ExcursionDBHelper(context);
//                mDBHelper.open();
//                mDBHelper.insertEntry(exc);
//                mDBHelper.close();
                onQuickStartClicked(v);
            }
        });

        //Get the text resources displaying the user's information
        this.mTvExcursions = (TextView)rootView.findViewById(R.id.num_excursions);
        this.mTvFavorites = (TextView)rootView.findViewById(R.id.num_likes);

        //Set the user's information on the home screen.
        this.updateDisplayedInformation();

        this.mProfilePhoto = (ImageView)rootView.findViewById(R.id.home_profile_photo);
        this.mProfilePhoto.setClickable(true);
        this.mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfilePhotoClicked(v);
            }
        });
        this.updateProfilePhoto();

        this.mProfileName = (TextView)rootView.findViewById(R.id.profileLabel);
        this.updateProfileName();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
        //Ensure that the keyboard is hidden
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        HomeActivity.hideKeyboard(context);
        Log.d(TAG,"Hid it!");

    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
    }

    /**
     * Update the displayed information on the user's homepage
     */
    public void updateDisplayedInformation(){

        //TODO: Get the information from somewhere...
        int mNumExcursions = 142;
        int mNumLikes = 15400;
        int mNumGalleries = 22;

        //this.mTvGallery.setText(prettyPrint(mNumGalleries));
        this.mTvFavorites.setText(prettyPrint(mNumLikes));
        this.mTvExcursions.setText(prettyPrint(mNumExcursions));

    }

    /**
     * Converts num into a pretty format like 15,400 = 15.4K if it is large
     * @param num - integer
     * @return - String representation of num
     */
    private String prettyPrint(int num){
        String mNum = ""+num;
        if(num > 1000){
            mNum = ""+num/1000+"."+num%1000/100+"K";
        }
        return mNum;
    }

    /**
     * Updates the photo with a photo from memory
     */
    private void updateProfilePhoto(){
        this.loadProfilePhoto();
        this.mProfilePhoto.setRight(View.FOCUS_RIGHT);
        Log.d(TAG,"Photo size is: "+this.mProfilePhoto.getDrawable().getMinimumHeight()+", "+this.mProfilePhoto.getDrawable().getMinimumWidth());
    }

    /**
     * Loads the profile photo from memory
     */
    private void loadProfilePhoto() {
        // Load profile photo from internal storage
        try {
            //Get the bitmap from file
            FileInputStream fis = context.openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
//            //Clip the bitmap so it is a circle
//            bmap = BitmapHelpers.GetBitmapClippedCircle(bmap);
            //Set the bitmap as the profile photo
            mProfilePhoto.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProfilePhoto.setImageResource(R.drawable.profile_default); //TODO
            Log.d(TAG, "ERROR: The profile photo was not loaded correctly.");
        }
    }

    /**
     * Updates the name of the profile from settings
     */
    private void updateProfileName(){
        //Get the SharedPreferences object.
        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = super.getActivity().getSharedPreferences(mKey,
                super.getActivity().MODE_PRIVATE);

        //Load the user data into the widgets.
        //Name
        mKey = getString(R.string.prefs_Name);
        String mValue = mPrefs.getString(mKey, "");
        if(mValue == null || mValue.equals("")){
            mValue = "Profile";
        }
        this.mProfileName.setText(mValue);
        Log.d(TAG,"Name is: "+mValue);
    }

    public void onProfilePhotoClicked(View v){
        Fragment fragment = new ProfileFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment, "last")
                .addToBackStack("prev").commit();
        HomeActivity mHomeActivity = ((HomeActivity)getActivity());
        Log.d(TAG,"NavDrawerItems: "+mHomeActivity.navDrawerItems);
        int position = 5; //ProfileFragment
        Log.d(TAG,"ProfileFragment is position: "+position);
        mHomeActivity.mDrawerList.setItemChecked(position, true);
        mHomeActivity.mDrawerList.setSelection(position);
        mHomeActivity.setTitle(mHomeActivity.navMenuTitles[position]);
    }

    public void onQuickStartClicked(View v){
        Fragment fragment = new TraceFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment, "last")
                .addToBackStack("prev").commit();
        HomeActivity mHomeActivity = ((HomeActivity)getActivity());
        Log.d(TAG,"NavDrawerItems: "+mHomeActivity.navDrawerItems);
        int position = 4; //TraceFragment
        Log.d(TAG,"ProfileFragment is position: "+position);
        mHomeActivity.mDrawerList.setItemChecked(position, true);
        mHomeActivity.mDrawerList.setSelection(position);
        mHomeActivity.setTitle(mHomeActivity.navMenuTitles[position]);
    }
}
