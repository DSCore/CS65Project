package jog.my.memory.GPS;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private onTraceFragmentClickedListener mListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trace, container, false);
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
        try {
            mListener = (onTraceFragmentClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
