package jog.my.memory.Profile;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import jog.my.memory.Home.HomeActivity;
import jog.my.memory.R;

/**
* Created by Kyrie on 2/1/15.
*/
public class PrefsFragment extends PreferenceFragment {

    private static final String TAG = "PrefsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)super.getActivity()).setMapVisible(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
    }
}
