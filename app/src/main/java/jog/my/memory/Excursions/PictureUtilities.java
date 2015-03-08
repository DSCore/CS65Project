package jog.my.memory.Excursions;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

/**
 * Adapted by Devon Cormack from code at
 * http://www.java2s.com/Code/Android/2D-Graphics/SaveBitmaptoandloadfromExternalStorage.htm
 */

class PictureUtilities {
    public static File getSavePath() {
        File path;
        if (hasSDCard()) { // SD card
            path = new File(getSDCardPath() + "/Tegaky/");
            path.mkdir();
        } else {
            path = Environment.getDataDirectory();
        }
        return path;
    }
    public static String getCacheFilename() {
        File f = getSavePath();
        return f.getAbsolutePath() + "/cache.png";
    }

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;
        } catch (Exception e) {
            return null;
        }
    }
    public static Bitmap loadFromCacheFile() {
        return loadFromFile(getCacheFilename());
    }
    public static void saveToCacheFile(Bitmap bmp) {
        saveToFile(getCacheFilename(),bmp);
    }
    public static void saveToFile(String filename,Bitmap bmp) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            bmp.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch(Exception e) {}
    }

    public static boolean hasSDCard() { // SD????????
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
    public static String getSDCardPath() {
        File path = Environment.getExternalStorageDirectory();
        return path.getAbsolutePath();
    }

    /**
     * Saves bitmap to a location and returns that location
     * @param bmp - bitmap
     * @return - location of saved bitmap file
     */
    public static String saveToFile(Bitmap bmp){
        File saveFile = getSavePath();
        saveToFile(saveFile.getAbsolutePath(),bmp);
        return saveFile.getAbsolutePath();
    }

}