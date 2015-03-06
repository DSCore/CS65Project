package jog.my.memory.Helpers;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import jog.my.memory.Profile.ProfileFragment;
import jog.my.memory.R;

public class MyDialogFragment extends DialogFragment {

    private static final String TAG = "MyDialogFragment";
    private static final String CURRENT_TEXT = "current_text";

    /** Constants to choose between types of Dialogs **/
    public static final int INPUT_DIALOG = 1;
    public static final int CAMERA_CHOOSE_DIALOG = 2;

    /** Constants to use with CAMERA_CHOOSE_DIALOG **/
    public static final int PICK_FROM_CAMERA = 0;
    public static final int PICK_FROM_GALLERY = 1;

    /**Constants to use to select between standard and numeric keyboard **/
    public static final int STANDARD_KEYBOARD = 0;
    public static final int NUMERIC_KEYBOARD = 1;

    EditText input = null;

    //Default constructor of dialogs in MyRuns.
    public static MyDialogFragment newInstance(int dialogType){
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_type", dialogType);
        frag.setArguments(args);
        return frag;
    }
    //An optional constructor of a new input instance with title, message, and keybaord type
    public static MyDialogFragment newInstance(String title, String message, int inputType) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_type",MyDialogFragment.INPUT_DIALOG);
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("InputType", inputType);
        frag.setArguments(args);
        return frag;
    }

    //An optional constructor of a new input instance with title and message
    public static MyDialogFragment newInstance(String title, String message ) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_type",MyDialogFragment.INPUT_DIALOG);
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("InputType",MyDialogFragment.STANDARD_KEYBOARD);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int dialogType = getArguments().getInt("dialog_type");
        //Switch the type of dialog that is constructed based on
        // the dialog_type argument.
        switch(dialogType) {
            //Standard input dialog with an optional title and message.
            case MyDialogFragment.INPUT_DIALOG:
                final String title = getArguments().getString("title");
                final String message = getArguments().getString("message");
                final String currentText = getArguments().getString(this.CURRENT_TEXT);
                final int inputType = getArguments().getInt("InputType");

                /** Note: This could all be written in one line, but is unrolled
                 * for readability and future maintenance
                 */
                AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
                if (title != null) {
                    alert.setTitle(title);
                }
                if (message != null) {
                    alert.setMessage(message);
                }

                // Get user input using an EditText view
                this.input = new EditText(this.getActivity());
                this.input.setText(currentText);
                if(inputType == this.NUMERIC_KEYBOARD){
                    this.input.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else if(inputType == this.STANDARD_KEYBOARD){
                    this.input.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Log.d(TAG, "The user just entered: " + value + " into " + title);
                        //Pass the inputted value to the calling activity.
//                        ****LEGACY CODE***
//                        ManualEntryActivity callingActivity = (ManualEntryActivity)getActivity();
//                        callingActivity.onUserInput(value); //This is in a fragment.
//                        callingActivity.setExerciseEntryValue(value,title);
//                        ****LEGACY CODE***
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                return alert.create();

            case MyDialogFragment.CAMERA_CHOOSE_DIALOG:
                // Build picture picker dialog for choosing from camera or gallery
                Log.d(TAG, "Building Camera choose dialog!");
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setTitle(R.string.ui_profile_photo_picker_title);
                // Set up click listener, firing intents open camera
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //Call the onPhotoPicker method with the correct item.
                        try {
                            ((ProfileFragment) getFragmentManager()
                                    .findFragmentByTag("last"))
                                    .onPhotoPickerItemSelected(item);
                        }
                        catch (ClassCastException cce){
                            Log.e(TAG, "The last fragment was not ProfileFragment. " +
                                    "This should only be accessed from ProfileFragment");
                        }
                    }
                };
                // Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
                return builder.create();
        }
        return null;
    }

    /**
     * Saves the state of the dialog box.
     * @param savedInstanceState - instance bundle
     */
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        //Get the text that is in the EditText object.
        if(input != null) {
            String currentText = input.getText().toString();
            Log.d(TAG, "Current text is: " + currentText);
            savedInstanceState.putString(this.CURRENT_TEXT, currentText);
        }
    }

}
