package jog.my.memory;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ProfileActivity extends Activity {
    private static final int GALLERY_PICTURE_REQUEST = 0;
    private static final int CAMERA_PICTURE_REQUEST =1;
    private static final int CROP_REQUEST=2;
    private static final String TAG = "Settings Activity";
    private static final String IMAGE_TYPE = "image/*";
    //Key for the packaged URI when saved
    private static final String URI_SAVED_KEY = "saved_URI";
    //Key for the packaged bitmap when saved
    private static final String PROFILE_PHOTO_KEY = "pro_photo_key";

    private Uri mImageCaptureUri = null;

    //The ImageView that displays the profile photo
    private ImageView mProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setImageCaptureURI();
        this.mProfilePhoto = (ImageView)findViewById(R.id.imageViewProfilePhoto);
        if(savedInstanceState == null){
            //Load the profile from memory
            this.loadProfile();
        }
        else{
            // Get the mImageCaptureURI
            this.mImageCaptureUri = savedInstanceState.getParcelable(this.URI_SAVED_KEY);
            Log.d(TAG, "TempImage URI in: " + this.mImageCaptureUri.getPath().toString());
            //Get the profile picture
            ((ImageView) findViewById(R.id.imageViewProfilePhoto)).setImageBitmap(
                    (Bitmap)savedInstanceState.getParcelable(this.PROFILE_PHOTO_KEY));
            Log.d(TAG, "Just reset the bitmap!");
            //Get the profile information.
            this.loadProfileInformation();
        }
    }

    private void setImageCaptureURI() {
        //Creates the temporary image capture URI
        this.mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Save the current photo's location for recall after orientation change.
        savedInstanceState.putParcelable(this.URI_SAVED_KEY, this.mImageCaptureUri);
        Log.d(TAG, "Saved the URI as: " + this.mImageCaptureUri.getPath().toString());
        //Save the current profile picture for reloading
        ImageView image = ((ImageView) findViewById(R.id.imageViewProfilePhoto));
        savedInstanceState.putParcelable(this.PROFILE_PHOTO_KEY,
                ((BitmapDrawable) image.getDrawable()).getBitmap());
    }

    public void setNewPicture(View view){

        //Fire up a Dialog to ask the user if they want to take the picture or
        //select from one already taken. Uses MyDialogFragment.
        DialogFragment newFragment
                = new MyDialogFragment().newInstance(MyDialogFragment.CAMERA_CHOOSE_DIALOG);
        newFragment.show(this.getFragmentManager(),"dialogfrag");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check the request is for taking a photo and OK
        switch (requestCode){

            case GALLERY_PICTURE_REQUEST:

                if(data != null) {
                    //Get the URI of the picture from the Gallery
                    Uri pictureURI = data.getData();

                    //Copy the file into our temporary file location
                    String realURI = this.getRealPathFromURI(pictureURI); //Get the real path
                    this.copyfile(realURI, this.mImageCaptureUri.getPath().toString());

                    //Crop the file in the temporary location.
                    this.cropImage();
                }
                break;

            case CAMERA_PICTURE_REQUEST:
                if (resultCode == RESULT_OK) {

                    //Crop the image in the temporary file location
                    this.cropImage();
                    break;
                }
            case CROP_REQUEST:
                if(resultCode == RESULT_OK){
                    // Update image view after image crop
                    Bundle extras = data.getExtras();
                    // Set the picture image in UI
                    if (extras != null) {
                        mProfilePhoto.setImageBitmap((Bitmap) extras.getParcelable("data"));
                    }
                    else{
                        Log.d(TAG, "THE PHOTO DATA WAS NULL!!!");}

                    break;
                }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = new String[] { android.provider.MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filename = cursor.getString(column_index);
        cursor.close();

        return filename;
    }

    private boolean copyfile(String sourceFilename, String destinationFilename) {
        FileInputStream bis = null;
        FileOutputStream bos = null;

        try {
            // bis -> buf -> bos
            bis = new FileInputStream(sourceFilename);
            bos = new FileOutputStream(destinationFilename, false);
            byte[] buf = new byte[4 * 1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
            }
        }

        return true;
    }

    /**
     * cropImage:
     *
     * Crops the image in the temporary file.
     *
     */
    private void cropImage() {
        Log.d(TAG, "Setting up crop intent from camera!");
        Log.d(TAG, "Loading the URI as: " + this.mImageCaptureUri.getPath().toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        //Set image location and unspecified type
        intent.setDataAndType(this.mImageCaptureUri,this.IMAGE_TYPE);
        // Specify image size
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        // Specify aspect ratio, 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        Log.d(TAG, "Entering crop intent!");
        startActivityForResult(intent, this.CROP_REQUEST);
    }

    public void onSaveClicked(View view){
        //Saves all data and notifies user when the save button is clicked
        this.saveProfile();
//        Toast.makeText(getApplicationContext(), getString(R.string.toast_saved_test),
//                Toast.LENGTH_SHORT).show();
        //Exits the Settings menu
        finish();
    }

    //Data saving helper functions

    public void saveProfile(){
        //Saves the inputted user data,using a SharedPreferences object

        //Saves the profile picture
        this.saveProfilePhoto();
        this.saveProfileInformation();
    }

    private void saveProfileInformation() {
        // Get the SharedPreferences instance using the name as a key
        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);
        //Create the SharedPreferences Editor object.
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        //Save information from each of the objects on the page:
        //Name
        mKey = getString(R.string.prefs_Name);
        String mValue = (String) ((EditText) findViewById(R.id.editTextName)).getText().toString();
        mEditor.putString(mKey, mValue);

        //Email
        mKey = getString(R.string.prefs_Email);
        mValue = (String) ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        mEditor.putString(mKey, mValue);

        //Phone Number
        mKey = getString(R.string.prefs_Phone);
        mValue = (String) ((EditText) findViewById(R.id.editTextPhone)).getText().toString();
        mEditor.putString(mKey, mValue);

        //Gender
        mKey = getString(R.string.prefs_Gender);
        RadioGroup mFRadioGroup = (RadioGroup) findViewById(R.id.mFRadio);
        int mIntValue = mFRadioGroup.indexOfChild(findViewById(mFRadioGroup.getCheckedRadioButtonId()));
        mEditor.putInt(mKey, mIntValue);

        //Class
        mKey = getString(R.string.prefs_Class);
        mValue = (String)((EditText)findViewById(R.id.editTextClass)).getText().toString();
        mEditor.putString(mKey,mValue);

        //Major
        mKey = getString(R.string.prefs_Major);
        mValue = (String) ((EditText) findViewById(R.id.editTextMajor)).getText().toString();
        mEditor.putString(mKey, mValue);

        //Commit all of the changes in the editor.
        mEditor.commit();

        Log.d(TAG, "Saved user data.");
    }

    private void saveProfilePhoto() {
        this.mProfilePhoto.buildDrawingCache();
        Bitmap bmap = this.mProfilePhoto.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d(TAG, ioe.getStackTrace().toString());
        }
    }

    public void onCancelClicked(View view){
        //Exits the activity without saving anything
        finish();
    }

    public void loadProfile(){
        //Loads the profile of the user
        this.loadProfilePhoto();
        this.loadProfileInformation();

    }

    private void loadProfileInformation() {
        //Get the SharedPreferences object.
        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

        //Load the user data into the widgets.
        //Name
        mKey = getString(R.string.prefs_Name);
        String mValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.editTextName)).setText(mValue);

        //Email
        mKey = getString(R.string.prefs_Email);
        mValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.editTextEmail)).setText(mValue);

        //Phone
        mKey = getString(R.string.prefs_Phone);
        mValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.editTextPhone)).setText(mValue);

        //Gender
        mKey = getString(R.string.prefs_Gender);
        int mIntValue = mPrefs.getInt(mKey, -1);
        //Check if there was one saved before
        if(mIntValue > -1){ //there was a value saved
            //Figure out which button should be checked.
            RadioButton radBtn = (RadioButton) ((RadioGroup)findViewById(R.id.mFRadio))
                    .getChildAt(mIntValue);
            radBtn.setChecked(true);
        }

        //Class
        mKey = getString(R.string.prefs_Class);
        mValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.editTextClass)).setText(mValue);

        //Major
        mKey = getString(R.string.prefs_Major);
        mValue = mPrefs.getString(mKey, "");
        ((EditText) findViewById(R.id.editTextMajor)).setText(mValue);

    }

    private void loadProfilePhoto() {
        // Load profile photo from internal storage
        try {
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mProfilePhoto.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProfilePhoto.setImageResource(R.drawable.ic_home); //TODO
            Log.d(TAG, "ERROR: The profile photo was not loaded correctly.");
        }
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case MyDialogFragment.PICK_FROM_CAMERA:
                Log.d(TAG, "Picking from the camera");
                // Take photo from camera
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    startActivityForResult(intent, this.CAMERA_PICTURE_REQUEST);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case MyDialogFragment.PICK_FROM_GALLERY:
                Log.d(TAG, "Picking from the gallery!");
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICTURE_REQUEST);

            default:
                return;
        }
    }
}
