package jog.my.memory.GPS;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jog.my.memory.HomeActivity;
import jog.my.memory.R;

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
    private onTraceFragmentClickedListener mListener;
    private Context context;

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
        ((HomeActivity)super.getActivity()).setDrawTrace(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(false);
        ((HomeActivity)super.getActivity()).setDrawTrace(false);
    }

    public void onDestroy(){
        //Cancel the notification
        ((NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE))
                .cancel(TraceFragment.TRACING_NOTIFICATION);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Start the tracking service
//        this.startTrackingLocation();
        this.displayTrackingNotification();
        this.context = inflater.getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trace, container, false);
    }

    // Display tracking location
    private void displayTrackingNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle("Jog My Memory")
                .setContentText("Making Memories!");
        Intent resultIntent = new Intent(getActivity(), TraceFragment.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
            PendingIntent.getActivity(
                getActivity(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        // Sets an ID for the notification
        int mNotificationId = TraceFragment.TRACING_NOTIFICATION;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
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

}
