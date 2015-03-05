package jog.my.memory.database;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Steve on 3/5/2015.
 */
public class PictureAdapter extends ArrayAdapter<Picture> {
    private static final String TAG = "PictureAdapter";

    /*** FIELDS ***/
    Context mContext;

    /*** Constructors ***/
//    ImageLocationAdapter(Context mContext, int resource){
//        super(mContext, resource);
//        this.mContext = mContext;
//    }
//
//    ImageLocationAdapter(Context mContext, int resource, int textViewResourceId) {
//        super(mContext, resource, textViewResourceId);
//        this.mContext = mContext;
//    }
    public PictureAdapter(Context mContext, int resource, List objects){
        super(mContext, resource, objects);
        this.mContext = mContext;
    }

    PictureAdapter(Context mContext, int resource, int textViewResourceId, List objects){
        super(mContext,resource,textViewResourceId,objects);
        this.mContext = mContext;
    }

    /**
     * Overrides the standard getView to deal with handling our ImageLocation objects
     */
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(this.mContext);
            //Find the screen dimensions
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int orientation = mContext.getResources().getConfiguration().orientation;
            int width = size.x;
            int height = size.y;

            Log.d(TAG, "display is: " + display);
            Log.d(TAG, "Orientation is: " + orientation);
            Log.d(TAG, "Width = " + width);
            Log.d(TAG, "Height = " + height);

            imageView.setLayoutParams(new GridView.LayoutParams(width/3,width/3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//            imageView.setMaxWidth(width/3);
//            imageView.setMaxHeight(height/3);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        // Below, we need to select the image from the Picture entry!
        imageView.setImageBitmap(((Picture)this.getItem(position)).getmImage());
        return imageView;
    }
}
