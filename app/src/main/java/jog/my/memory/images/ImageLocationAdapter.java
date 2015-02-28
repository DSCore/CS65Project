package jog.my.memory.images;

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
 * Created by Devon Cormack and Steven Muenzen on 2/3/15.
 */
public class ImageLocationAdapter extends ArrayAdapter<ImageLocation> {

    private static final String TAG = "ImageLocationAdapter";

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

    ImageLocationAdapter(Context mContext, int resource, List objects){
        super(mContext, resource, objects);
        this.mContext = mContext;
    }

    ImageLocationAdapter(Context mContext, int resource, int textViewResourceId, List objects){
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

        //TODO: Below, we need to select the image from the ImageLocation entry!
        imageView.setImageBitmap(((ImageLocation)this.getItem(position)).getmImageAsBitmap());
        return imageView;
    }

}

