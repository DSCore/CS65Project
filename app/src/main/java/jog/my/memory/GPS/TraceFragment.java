package jog.my.memory.GPS;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jog.my.memory.Gallery.GalleryFragment;
import jog.my.memory.Helpers.BitmapHelpers;
import jog.my.memory.HomeActivity;
import jog.my.memory.R;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TraceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TraceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TraceFragment extends Fragment {

    /***
     * NOTE TO GROUP!!!:
     *  In order to get tracing information from this app,
     *  you need to call the getUpdates from the HomeActivity.
     *  The button on this page starts the application tracking,
     *  which involves creating the notification, clearing the current
     *  list of updates in the HomeActivity, then switching the button
     *  to a picture button from a start button (Picture button will
     *  have an icon, like the Google Camera app!).
     */


    public static int TRACING_NOTIFICATION = 1;

    private static final String TAG = "TraceFragment";
    private static final int CAMERA_PICTURE_REQUEST = 1;

    private onTraceFragmentClickedListener mListener;
    private Context context;
    private View view;

    public interface  onTraceFragmentClickedListener{
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TraceFragment.
     */
    public static TraceFragment newInstance(/**TODO: PASS IN PARAMS HERE **/) {
        TraceFragment fragment = new TraceFragment();
        Bundle args = new Bundle();
        //TODO: Any parameters that are needed go here
        fragment.setArguments(args);
        return fragment;
    }

    public TraceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //TODO: Deal with any parameters passed in here
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
        HomeActivity.hideKeyboard(context);
        Button startPhoto = (Button)view.findViewById(R.id.trace_start_photo_btn);
        if(((HomeActivity)super.getActivity()).mDrawTrace){
            startPhoto.setText("");
            startPhoto.setBackgroundResource(R.drawable.camera_bar);
        } else {
            startPhoto.setText("START");
            startPhoto.setBackgroundResource(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(false);
//        ((HomeActivity)super.getActivity()).setDrawTrace(false);
    }

    public void onDestroy(){
        //Cancel the notification
       // ((NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE))
                //.cancel(TraceFragment.TRACING_NOTIFICATION);
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Start the tracking service
//        this.startTrackingLocation();
        //Inflate the view
        this.view = inflater.inflate(R.layout.fragment_trace, container, false);
//        this.displayTrackingNotification();
        //Save the context
        this.context = inflater.getContext();
        Button startPhoto = (Button)view.findViewById(R.id.trace_start_photo_btn);

        startPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoStartClicked(v);
            }
        });

        Button finishPhoto = (Button) view.findViewById(R.id.trace_finish_btn);
        finishPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishExcursionClicked(v);
            }
        });

        //If the activity is tracing, show the finishPhoto button
        if(HomeActivity.mDrawTrace){
            finishPhoto.setVisibility(View.VISIBLE);
            view.findViewById(R.id.trace_finish_btn_background).setVisibility(View.VISIBLE);
        }
        else{
            finishPhoto.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.trace_finish_btn_background).setVisibility(View.INVISIBLE);
        }

        // Inflate the layout for this fragment
        return view;
    }

    // Display tracking location
    private void displayTrackingNotification(){
        Intent intent = new Intent(getActivity(),TrackingService.class);
        getActivity().startService(intent);
        Log.d(TAG,"Launched the service!");
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle("Jog My Memory")
//                .setContentText("Making Memories!");
//        Intent resultIntent = new Intent(getActivity(), TraceFragment.class);
//        // Because clicking the notification opens a new ("special") activity, there's
//        // no need to create an artificial back stack.
//        PendingIntent resultPendingIntent =
//            PendingIntent.getActivity(
//                getActivity(),
//                0,
//                resultIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        // Sets an ID for the notification
//        int mNotificationId = TraceFragment.TRACING_NOTIFICATION;
//        // Gets an instance of the NotificationManager service
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
//        // Builds the notification and issues it.
//        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    /**
     * Stops displaying the tracking notification
     */
    private void stopDisplayTrackingNotification(){
        ((NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE))
                .cancel(TraceFragment.TRACING_NOTIFICATION);
    }

//    // TODO: Rename method, update argument and hook method into UI event - from default frag, probably don't need this
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (onTraceFragmentClickedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void onPhotoStartClicked(View v) {
        Log.d(TAG, "Button clicked, mDrawTrace is " + ((HomeActivity) getActivity()).mDrawTrace);
        Button startPhoto = (Button) v;
        if (((HomeActivity) getActivity()).mDrawTrace) {
            //If we're drawing the trace, then this is the picture button
            // Take photo from camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Construct temporary image path and name to save the taken
            // photo
            GalleryFragment.mImageCaptureUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "tmp_"
                    + String.valueOf(System.currentTimeMillis()) + ".jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    GalleryFragment.mImageCaptureUri);
//        //Just tell it to return the image to us, we'll save it in the database
            intent.putExtra("return-data", true);
            try {
                // Start a camera capturing activity
                startActivityForResult(intent, this.CAMERA_PICTURE_REQUEST);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //Start drawing a new trace
            ((HomeActivity) getActivity()).clearUpdates();
            ((HomeActivity) getActivity()).setDrawTrace(true);
            //Start a new Excursion
            ((HomeActivity) getActivity()).startNewExcursion();
            //Update the button
            startPhoto.setText("");
            startPhoto.setBackgroundResource(R.drawable.camera_bar);
            //Display the tracking notification
            this.displayTrackingNotification();
            //Make the FinishExcursion button visible
            this.view.findViewById(R.id.trace_finish_btn).setVisibility(View.VISIBLE);
            this.view.findViewById(R.id.trace_finish_btn_background).setVisibility(View.VISIBLE);
        }
    }

    public void onFinishExcursionClicked(View v){
        //If we aren't tracing, ignore the click
        if(!((HomeActivity)getActivity()).mDrawTrace){
            //Do nothing
        }
        else {
            //Turn off the trace
            ((HomeActivity) getActivity()).setDrawTrace(false);
            ArrayList<Location> updates = ((HomeActivity) getActivity()).getUpdates();
            //Save the database
            ((HomeActivity) getActivity()).stopCurrentExcursion();

            // Set the camera/start button back to camera mode
            Button startPhoto = (Button)view.findViewById(R.id.trace_start_photo_btn);
            startPhoto.setBackgroundResource(0);
            startPhoto.setText("START");
            this.stopDisplayTrackingNotification();
            //Clear the map of all markers
            ((HomeActivity)getActivity()).clearAllMarkers();
            //Make the FinishExcursion button invisible
            this.view.findViewById(R.id.trace_finish_btn).setVisibility(View.INVISIBLE);
            this.view.findViewById(R.id.trace_finish_btn_background).setVisibility(View.INVISIBLE);
        }
    }
    private HttpAsyncTask mAsyncTask;
    /**
     *
     * @param requestCode - What was called?
     * @param resultCode - Was it executed correctly?
     * @param data - data returned
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == this.CAMERA_PICTURE_REQUEST
                && resultCode == super.getActivity().RESULT_OK) {
            Log.d(TAG, "data is: " + data);
            mAsyncTask = new HttpAsyncTask();
            mAsyncTask.execute();

        }
    }




    private class HttpAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        protected void onPreExecute() {
            Toast.makeText(context, "Saving Photo...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... urls) {
            ((HomeActivity) context).savePhoto=true;
            Bitmap bmp = null;
            ContentResolver cr = context.getContentResolver();
            cr.notifyChange(GalleryFragment.mImageCaptureUri,null);
            try{
                bmp = MediaStore.Images.Media.getBitmap(cr, GalleryFragment.mImageCaptureUri);
                FileOutputStream out = new FileOutputStream(GalleryFragment.mImageCaptureUri.getPath());
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // PNG is a lossless format, the compression factor (100) is ignored, in JPEG it is used.
            }catch(IOException ioe){
                Log.d(TAG, "File was not found.");
            }


            if (bmp == null) {
                Log.d(TAG, "Error: The bitmap was null");
            } else {

                bmp = BitmapHelpers.LoadAndResizeBitmap(GalleryFragment.mImageCaptureUri.getPath(), 500, 500);

                PicturesDBHelper mDbHelper = new PicturesDBHelper(context);
                mDbHelper.open();

                Location mLastLocation = null;
                HomeActivity ha = (HomeActivity) context;

                if(ha.getUpdates().size() > 0)
                    mLastLocation = ha.getUpdates().get(ha.getUpdates().size() - 1);
                final Picture pic = new Picture();
                if( mLastLocation != null) {
                    pic.setmLat(mLastLocation.getLatitude());
                    pic.setmLong(mLastLocation.getLongitude());
                    pic.setmImage(bmp);
                    pic.setmExcursionID(ha.getNextDBID());
                    mDbHelper.insertEntry(pic);
                }else{
                    pic.setmLat(0);
                    pic.setmLong(0);
                    pic.setmImage(bmp);
                    pic.setmExcursionID(ha.getNextDBID());
                    mDbHelper.insertEntry(pic);
                }
                //Update the images that are displayed on the map
                ha.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomeActivity)context).addPictureToMap(pic);
                    }
                });
//                    this.updateGridView();
                mDbHelper.close();
            }
            return "OK";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_LONG).show();
            ((HomeActivity) context).savePhoto=false;
        }
    }
}
