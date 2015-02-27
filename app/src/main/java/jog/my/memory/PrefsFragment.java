package jog.my.memory;

import android.os.Bundle;
import android.preference.PreferenceFragment;

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
}
