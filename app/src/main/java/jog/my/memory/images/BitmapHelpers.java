package jog.my.memory.images;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Devon Cormack on 2/4/15.
 */
public class BitmapHelpers {

    public static Bitmap LoadAndResizeBitmap(String fileName, int width, int height)
    {
        //Get the BitmapFactory options object
        BitmapFactory.Options bmpFactOptns = new BitmapFactory.Options();

        //Set the bitmap to be returned, but not stored in memory directly
        bmpFactOptns.inJustDecodeBounds = true;

        //Decode the bitmap from the file
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmpFactOptns);

        //Determine whether the picture needs to be resized
        int heightRatio = (int) Math.ceil(bmpFactOptns.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactOptns.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            //If it does, resize it
            if (heightRatio > widthRatio)
            {
                bmpFactOptns.inSampleSize = heightRatio;
            } else {
                bmpFactOptns.inSampleSize = widthRatio;
            }
        }

        //Store the now small bitmap in memory
        bmpFactOptns.inJustDecodeBounds = false;

        //Get the file as a bitmap
        bitmap = BitmapFactory.decodeFile(fileName, bmpFactOptns);

        //Return the smaller bitmap
        return bitmap;
    }

}

