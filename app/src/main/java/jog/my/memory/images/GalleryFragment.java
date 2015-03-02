package jog.my.memory.images;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import jog.my.memory.R;


/** Created by Devon Cormack and Steven Muenzen 02/03/15 **/

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryActivity";
    private static final int CAMERA_PICTURE_REQUEST = 1;
    private static final int DISPLAY_ACTIVITY_REQUEST = 2;

    private ArrayList<ImageLocation> mILList = new ArrayList<ImageLocation>();
    private Uri mImageCaptureUri; //TODO: currently unused
    private ImageLocationDBHelper mDbHelper;

    private Context context;

    /** Location Listener to get location **/

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };
    private Location mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_gallery, container, false);
//        View view =  super.onCreateView(inflater, container, savedInstanceState);

        super.getActivity().setContentView(R.layout.activity_gallery);

        //TODO:Set up all of the onClickListeners here so they don't need to be in the activity
        Button mShowSlides = (Button)super.getActivity().findViewById(R.id.button_take_pic);
        mShowSlides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSlideshowClicked(view);
            }
        });

        //Update the grid view
        this.updateGridView();

        return view;
    }

    public void updateLocation(Location location){
        this.mLocation = location;
    }

    public void updateGridView(){
        //Get the gridview
        GridView mGV = (GridView)super.getActivity().findViewById(R.id.gridview);

        //Get the data from the database
        this.mDbHelper = new ImageLocationDBHelper(super.getActivity());
        this.mILList = this.mDbHelper.fetchEntries();

        //Set the adapter for the grid view
//        mGV.setAdapter(new ImageLocationAdapter(this,
//                android.R.layout.simple_list_item_1,this.mILList));
        mGV.setAdapter(new ImageLocationAdapter(super.getActivity(),
                R.id.example_list_item,this.mILList));        //Set the listener for clicks on items in the gridview
        mGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(context,DisplayActivity.class);
                ImageLocation mCurrentImageLocation = mILList.get(position);
                intent.putExtra(DisplayActivity.IMAGE, mCurrentImageLocation.getmImage());
                intent.putExtra(DisplayActivity.GPS,
                        Location.convert(mCurrentImageLocation.getmLat(), Location.FORMAT_DEGREES)
                                +", "+ Location.convert(mCurrentImageLocation.getmLong()
                                , Location.FORMAT_DEGREES));
                intent.putExtra(DisplayActivity.TIMESTAMP,mCurrentImageLocation.getmTimeStamp());
                intent.putExtra(DisplayActivity.ID,mCurrentImageLocation.getId());
                startActivityForResult(intent, DISPLAY_ACTIVITY_REQUEST);
            }
        });
    }

    //TODO: Add the savedInstanceState here! If needed!

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        super.getActivity().getMenuInflater().inflate(R.menu.menu_gallery, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Launches the camera application and takes a picture,
     * which is then added to the gallery
     * @param view - the view clicked
     */
    public void onTakePictureClicked(View view){
        Log.d(TAG, "Taking a picture!");
        // Take photo from camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken
        // photo
        mImageCaptureUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "tmp_"
                + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
//        //Just tell it to return the image to us, we'll save it in the database
        intent.putExtra("return-data", true);
        try {
            // Start a camera capturing activity
            startActivityForResult(intent, this.CAMERA_PICTURE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void onSlideshowClicked( View view){
        startActivity(new Intent(super.getActivity(), SlideshowActivity.class));
    }

    /**
     *
     * @param requestCode - What was called?
     * @param resultCode - Was it executed correctly?
     * @param data - data returned
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == this.CAMERA_PICTURE_REQUEST && resultCode == super.getActivity().RESULT_OK) {
            Log.d(TAG, "data is: " + data);
//            if (data != null) {
            //Get the URI of the picture from the Gallery
//                Uri pictureURI = data.getData();
//                Log.d(TAG, "URI was: " + pictureURI.getPath().toString());
            //Get the real location
            //            String realURI = this.getRealPathFromURI(pictureURI);
            Bitmap bmp = null;
//                try {
//                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.mImageCaptureUri);
//                } catch (FileNotFoundException fnfe) {
//                    Log.d(TAG, "File not found: "+fnfe.getStackTrace().toString());
//                } catch (IOException ioe) {
//                    Log.d(TAG, "IOException: "+ioe.getStackTrace().toString());
//                }

//                Bundle extras = data.getExtras();
//                bmp = (Bitmap) extras.get("data");

            ContentResolver cr = super.getActivity().getContentResolver();
            cr.notifyChange(this.mImageCaptureUri,null);
            try{
                bmp = MediaStore.Images.Media.getBitmap(cr, mImageCaptureUri);
                FileOutputStream out = new FileOutputStream(this.mImageCaptureUri.getPath());
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // PNG is a lossless format, the compression factor (100) is ignored, in JPEG it is used.
            }catch(IOException ioe){
                Log.d(TAG, "File was not found.");
            }


            if (bmp == null) {
                Log.d(TAG, "Error: The bitmap was null");
            } else {

                bmp = BitmapHelpers.LoadAndResizeBitmap(this.mImageCaptureUri.getPath(), 500,500);

                //TODO: Make this real code, not test code! i.e. make it store in the database!
                this.mDbHelper.insertEntry(new ImageLocation(new GregorianCalendar(),
                        this.mLocation.getLatitude(), this.mLocation.getLongitude(), bmp));
//                    this.mILList.add(new ImageLocation(new GregorianCalendar(), 16, 24, bmp));
                this.updateGridView();
            }
//            }
        }
        if(requestCode == DISPLAY_ACTIVITY_REQUEST && resultCode == super.getActivity().RESULT_OK){
            Log.d(TAG, "Updating gridview");
            this.updateGridView();
        }
    }

    /**
     * Gets the real path of the URI returned from the camera
     * @param contentUri - apparent URI of resource
     * @return - actual URI of resource
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = new String[] { MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = super.getActivity().getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filename = cursor.getString(column_index);
        cursor.close();

        return filename;
    }

    /**
     * Sets the temporary URI for capturing images
     */
    private void setImageCaptureURI() {
        //Creates the temporary image capture URI
        this.mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }
}

