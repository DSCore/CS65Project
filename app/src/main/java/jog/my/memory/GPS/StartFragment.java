package jog.my.memory.GPS;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import jog.my.memory.HomeActivity;
import jog.my.memory.R;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class StartFragment extends Fragment {


    private static final String TAG = "StartFragment";
    public static final String KEY_ACTIVITY = "activity";
    public static final String KEY_INPUT = "input";
    private static final int NEW_ENTRY = 1;

    /* Type definitions for input type */
    public static final int MANUAL_ENTRY = 0;
    public static final int GPS = 1;
    public static final int AUTOMATIC = 2;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View startFragmentView = inflater.inflate(R.layout.fragment_start, container, false);

        //Add in an onClickListener for the start button
        Button startButton = (Button) startFragmentView.findViewById(R.id.startFragStartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("STARTFRAGMENT", "START");
                onStartClicked(v);
            }
        });

        //Add in an onClickListener() for the sync button.
        Button syncButton = (Button) startFragmentView.findViewById(R.id.startFragSyncButton);
        syncButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSyncClicked(v);
            }
        });

        //Return the inflated view
        return startFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set the map background to non-visible
        ((HomeActivity)super.getActivity()).setMapVisible(false);

    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
    }

    public void onStartClicked(View view) {

        //Get the value of the spinner for selecting the input
        String inputType = ((Spinner) this.getActivity().findViewById(R.id.spinner_InputType))
                .getSelectedItem().toString();
        Log.d(TAG, "The input selected was " + inputType);

        if(inputType.equals("Manual Entry")){
//            //If "Manual Entry" is selected, launch the ManualEntryActivity.
//            Intent intent = new Intent(this.getActivity(), ManualEntryActivity.class);
//            intent.putExtra(StartFragment.KEY_ACTIVITY, (int)((Spinner)this.getActivity().
//                    findViewById(R.id.spinner_ActivityType)).getSelectedItemId());
//            startActivityForResult(intent, StartFragment.NEW_ENTRY);
        }
        else if(inputType.equals("GPS")){
            //If "GPS" is selected, launch the GPS Tracker.
            Intent intent = new Intent(this.getActivity(), /*GPSInputActivity*/MapsActivity.class);

            intent.putExtra(StartFragment.KEY_ACTIVITY, ((Spinner)this.getActivity().
                    findViewById(R.id.spinner_ActivityType)).getSelectedItemPosition());

            intent.putExtra(StartFragment.KEY_INPUT, (((Spinner)this.getActivity()
                    .findViewById(R.id.spinner_InputType)).getSelectedItemPosition()));

            startActivity(intent);
        }
        else if(inputType.equals("Automatic")){
//            //If "Automatic" is selected, launch the GPS Tracker.
//            Intent intent = new Intent(this.getActivity(), /*GPSInputActivity*/MapsActivity.class);
//
//            intent.putExtra(StartFragment.KEY_ACTIVITY, ((Spinner)this.getActivity().
//                    findViewById(R.id.spinner_ActivityType)).getSelectedItemPosition());
//
//            intent.putExtra(StartFragment.KEY_INPUT, (((Spinner)this.getActivity()
//                    .findViewById(R.id.spinner_InputType)).getSelectedItemPosition()));
//
//            startActivity(intent);
        }
        else{
            //Do nothing, this shouldn't even be possible
            Log.d(TAG, "Chose bad option");
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == this.NEW_ENTRY){
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("callUpdateHistory",false)){
                    this.updateHistory();
                }
            }
        }
    }

    /**
     * Communicates with the HistoryFragment to tell it to update.
     */
    private void updateHistory(){
        //Tell the HistoryFragment to update its history
        // by communicating through their shared activity (MainActivity)
//        ((HistoryFragment) //TODO: Reimplement this for updating our version!
//                ((MainActivity)this.getActivity())
//                        .getViewPagerAdapter().getItem(((MainActivity)getActivity()).HISTFRAG))
//                .updateHistory();
    }


    public void onSyncClicked(View view) {
        //TODO: Do we even need / want this button?
//        ((MainActivity)this.getActivity()).onSyncClicked(view);
    }


}
