package jog.my.memory.images;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import jog.my.memory.R;
import jog.my.memory.database.PicturesDBHelper;


public class DisplayActivity extends Activity {

    public static final String IMAGE = "IMAGE";
    public static final String GPS = "GPS";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String ID = "ID";
    private ImageView mIvDisplayImage;
    private TextView mTvTimeStamp;
    private TextView mTvGPS;
    private long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        this.mTvTimeStamp = (TextView)this.findViewById(R.id.TextView_displayGPS);
        this.mTvGPS = (TextView)this.findViewById(R.id.TextView_displayTimeStamp);
        this.mIvDisplayImage = (ImageView)this.findViewById(R.id.ImageView_displayImage);

        if(savedInstanceState != null){
            //Get the image out of the savedInstanceState
//            this.mIvDisplayImage.setImageBitmap(((BitmapDrawable)savedInstanceState.getParcelable(IMAGE)).getBitmap()); //TODO: This line may need to be the same as in ProfileActivity to work!
            byte[] mBa = (byte[])savedInstanceState.get(this.IMAGE);
            this.mIvDisplayImage.setImageBitmap(BitmapFactory.decodeByteArray(mBa, 0, mBa.length));
            this.mTvGPS.setText((String)savedInstanceState.get(GPS));
            this.mTvTimeStamp.setText((String)savedInstanceState.get(TIMESTAMP));
            this.mId = (Long)savedInstanceState.get(this.ID);
        }
        else{
            Intent intent = this.getIntent();
            Bundle data = intent.getExtras();
            byte[] mBA = (byte[])data.get(IMAGE);
            this.mIvDisplayImage.setImageBitmap(BitmapFactory.decodeByteArray(mBA, 0, mBA.length)); //TODO: This line may need to be the same as in ProfileActivity to work!
            this.mTvGPS.setText((String)data.get(GPS));
            this.mTvTimeStamp.setText((String)data.get(TIMESTAMP));
            this.mId = (Long)data.get(this.ID);
        }
    }

    /**
     * Preserves the state of the application on orientation change.
     * @param outState - the state of the outgoing application
     */
    public void onSavedInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //Save the image
        Bitmap bmp = ((BitmapDrawable) this.mIvDisplayImage.getDrawable()).getBitmap();
        ByteArrayOutputStream mBos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,mBos);
        outState.putByteArray(this.IMAGE,mBos.toByteArray());
        outState.putString(this.GPS, this.mTvGPS.getText().toString());
        outState.putString(this.TIMESTAMP, this.mTvTimeStamp.getText().toString());
        outState.putLong(this.ID,this.mId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteEntryButton) {
            new PicturesDBHelper(this).removeEntry(this.mId);
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDeleteButtonClicked(View view){
        new PicturesDBHelper(this).removeEntry(this.mId);
        setResult(RESULT_OK);
        finish();
    }
}
