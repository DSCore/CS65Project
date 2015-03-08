package jog.my.memory.Gallery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;

import jog.my.memory.R;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;


public class SlideshowActivity extends Activity {

    private Button start;
    private Button exit;
    private Button prev;
    private ImageView img;
    private static final String TAG = "Chachaslide";
    int moveOver= 3;
    private Button next;
    private final Handler mHandler = new Handler();
    private Timer timer;
    private static final String KEY_INDEX = "index";
    private  int currentIndex=0;
    private PicturesDBHelper mDbHelper;
    private ArrayList<Picture> mPicList = new ArrayList<Picture>();

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState() called");
        outState.putInt(KEY_INDEX, currentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        this.mDbHelper = new PicturesDBHelper(this);
        this.mPicList = this.mDbHelper.fetchEntries();

       // if(mPicList.size() > 0) {
            next = (Button) findViewById(R.id.next_bt);
            exit = (Button) findViewById(R.id.exit_btn);
            prev = (Button) findViewById(R.id.previous_btn);
            img = (ImageView) findViewById(R.id.img_view);
            img.setImageBitmap(mPicList.get(0).getmImage());
            mHandler.postDelayed(slideOver, 2500);
       // }
    }

    private final Runnable slideOver = new Runnable()
    {
        public void run()
        {
            i= (i+1) % mPicList.size();
            changeImage();

        }

    };

    private int i=0;

    public void onClickNext(View v){
        i= (i+1) % mPicList.size();
        mHandler.removeCallbacks(slideOver);
        changeImage();
    }

    public void onExitClicked(View v){
        finish();
    }

    public void onPrevClicked(View v){
        i = (i - 1) % mPicList.size();
        i = (i < 0) ? i + mPicList.size() : i;
        mHandler.removeCallbacks(slideOver);
        changeImage();
    }

    public void changeImage(){

        img.setImageBitmap(mPicList.get(i).getmImage());
        currentIndex=i;
        mHandler.postDelayed(slideOver, 5000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slideshow, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener
            = new GestureDetector.SimpleOnGestureListener(){


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float sensitvity = 50;

            // TODO Auto-generated method stub
            if((e1.getX() - e2.getX()) > sensitvity){
                i= (i+1) % mPicList.size();
                mHandler.removeCallbacks(slideOver);
                changeImage();
            }else if((e2.getX() - e1.getX()) < sensitvity) {
                i = (i - 1) % mPicList.size();
                i = (i < 0) ? i + mPicList.size() : i;
                mHandler.removeCallbacks(slideOver);
                changeImage();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);
}

/*

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;

import jog.my.memory.R;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;


public class SlideshowActivity extends Activity {

    private Button start;
    private Button exit;
    private Button prev;
    private ImageView img;
    private static final String TAG = "Chachaslide";
    int moveOver= 3;
    private Button next;
    private final Handler mHandler = new Handler();
    private Timer timer;
    private static final String KEY_INDEX = "index";
    private  int currentIndex=0;
    private PicturesDBHelper mDbHelper;
    private ArrayList<Picture> mPicList = new ArrayList<Picture>();

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState() called");
        outState.putInt(KEY_INDEX, currentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        this.mDbHelper = new PicturesDBHelper(this);
        this.mPicList = this.mDbHelper.fetchEntries();

        next= (Button)findViewById(R.id.next_bt);
        exit= (Button)findViewById(R.id.exit_btn);
        prev= (Button)findViewById(R.id.previous_btn);
        img= (ImageView)findViewById(R.id.img_view);

        img.setImageBitmap(mPicList.get(0).getmImage());
        mHandler.postDelayed(slideOver, 2500);

    }

    private final Runnable slideOver = new Runnable()
    {
        public void run()
        {
            i= (i+1) % mPicList.size();
            changeImage();

        }

    };

    private int i=0;

    public void onClickNext(View v){
        i= (i+1) % mPicList.size();
        mHandler.removeCallbacks(slideOver);
        changeImage();

    }

    public void onExitClicked(View v){
        finish();
    }

    public void onPrevClicked(View v){
        i= (i-1) % mPicList.size();
        i= (i<0)? i+mPicList.size(): i;
        mHandler.removeCallbacks(slideOver);

        changeImage();
    }

    public void changeImage(){

        img.setImageBitmap(mPicList.get(i).getmImage());
        currentIndex=i;
        mHandler.postDelayed(slideOver, 2500);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slideshow, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

*/