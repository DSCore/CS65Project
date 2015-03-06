package jog.my.memory.Helpers;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;

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


    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

//    public static Bitmap GetBitmapClippedCircle(Bitmap bmp, int radius) {
//        Bitmap sbmp;
//        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
//            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
//        else
//            sbmp = bmp;
//        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
//                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xffa19774;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
//
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(Color.parseColor("#BAB399"));
//        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
//                sbmp.getWidth() / 2+0.1f, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(sbmp, rect, rect, paint);
//
//
//        return output;
//    }

}

