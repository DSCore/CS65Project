package jog.my.memory.Excursions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import jog.my.memory.HomeActivity;
import jog.my.memory.R;
import jog.my.memory.database.Excursion;
import jog.my.memory.database.ExcursionAdapter;
import jog.my.memory.database.ExcursionDBHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link jog.my.memory.Debug.BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ExcursionsFragment extends ListFragment {

    private static final String TAG = "ExcursionsFragment";

    public ExcursionsFragment(){}

    private Context context;

    public ExcursionDBHelper myDBHelper;
    public static ArrayList<Excursion> values;
    public static ExcursionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_excursions, container, false);
        //Open database
        myDBHelper = new ExcursionDBHelper(getActivity());
        myDBHelper.open();
        values = myDBHelper.fetchEntries();
        myDBHelper.close();

        adapter = new ExcursionAdapter(context, R.id.example_list_item, this.values);
        setListAdapter(adapter);
        return rootView;
    }

/*    public static void refresh(){
        ExerciseEntriesDataSource datasourc = new ExerciseEntriesDataSource();
        datasourc.open();
        values = datasourc.fetchEntries();
        datasourc.close();

        ArrayList<ExerciseEntry> adapte = new ActivityEntriesAdapter(values);
        setListAdapter(adapte);
    }*/

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Excursion entry = (Excursion)(getListAdapter()).getItem(position);
        long rowId = entry.getmID();

        Intent intent = new Intent(getActivity(), DisplayExcursionActivity.class);
        intent.putExtra(DisplayExcursionActivity.EXTRA_ROW_ID, rowId);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)super.getActivity()).setMapVisible(false);

        //Open database
        myDBHelper = new ExcursionDBHelper(getActivity());
        myDBHelper.open();
        values = myDBHelper.fetchEntries();
        myDBHelper.close();

        adapter = new ExcursionAdapter(context, R.id.example_list_item, this.values);
        setListAdapter(adapter);
    }

    @Override
    public void onPause(){

        super.onPause();
        ((HomeActivity)super.getActivity()).setMapVisible(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
