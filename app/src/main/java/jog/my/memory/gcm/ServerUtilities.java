/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jog.my.memory.gcm;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Config;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import jog.my.memory.R;
import jog.my.memory.database.MyLatLng;
import jog.my.memory.database.Picture;

/**
 * Helper class used to communicate with the AppEngine server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "ServerUtilities";


    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    public static void sendRegistrationIdToBackend(Context context, String regId) {
        String serverUrl = context.getString(R.string.server_addr)
                + "/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);

        // Once GCM returns a registration id, we need to register it in the
        // server. As the server might be down, we will retry it a couple
        // times.
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= ServerUtilities.MAX_ATTEMPTS; i++) {
            try {
                ServerUtilities.post(serverUrl, params);
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Thread.currentThread().interrupt();
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }

    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint
     *            POST address.
     * @param params
     *            request parameters.
     *
     * @throws java.io.IOException
     *             propagated from POST.
     */
    public static String post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            // Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();
            return response.toString();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Gets the real path of the URI returned from the camera
     * @param contentUri - apparent URI of resource
     * @return - actual URI of resource
     */
    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = new String[] { MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filename = cursor.getString(column_index);
        cursor.close();

        return filename;
    }


    /**
     * Uploads a file to the server.
     * @param context - Context of the app where the file is
     * @param serverUrl - URL of the server
     * @param uri - URI of the file
     * @return - response of posting the file to server
     */
    public static String postFile(Context context, String serverUrl, Uri uri){ //TODO: Get the location sent up in here too!

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(serverUrl + "/upload");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


//        //Create the FileBody
//        final File file = new File(uri.getPath());
//        FileBody fb = new FileBody(file);

        // deal with the file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromURI(uri, context));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] byteData = byteArrayOutputStream.toByteArray();
        //String strData = Base64.encodeToString(data, Base64.DEFAULT); // I have no idea why Im doing this
        ByteArrayBody byteArrayBody = new ByteArrayBody(byteData, "image"); // second parameter is the name of the image (//TODO HOW DO I MAKE IT USE THE IMAGE FILENAME?)

        builder.addPart("myFile",byteArrayBody);
        builder.addTextBody("foo","test text");

        HttpEntity entity = builder.build();
        post.setEntity(entity);

        try {
            HttpResponse response = client.execute(post);
            Log.d(TAG,"The response code was: "+response.getStatusLine().getStatusCode());
        }
        catch(Exception e){
            Log.d(TAG,"The image was not successfully uploaded.");
        }

        return null;





//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost(serverUrl+"/postFile.do");
//
//        InputStream stream = null;
//        try {
//            stream = context.getContentResolver().openInputStream(uri);
//
//            InputStreamEntity reqEntity = new InputStreamEntity(stream, -1);
//
//            httppost.setEntity(reqEntity);
//
//            HttpResponse response = httpclient.execute(httppost);
//            Log.d(TAG,"The response code was: "+response.getStatusLine().getStatusCode());
//            if (response.getStatusLine().getStatusCode() == 200) {
//                // file uploaded successfully!
//            } else {
//                throw new RuntimeException("server couldn't handle request");
//            }
//            return response.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            // handle error
//        } finally {
//            try {
//                stream.close();
//            }catch(IOException ioe){
//                ioe.printStackTrace();
//            }
//        }
//        return null;
    }

    /**
     * Return a string representation of the arraylist
     * @param amll arraylist of MyLatLng objects
     * @return String representation
     */
    public static String parseLocationString(ArrayList<MyLatLng> amll){
        String locationString = "";
        Log.d(TAG,"amll:size: "+amll.size());
        for(int i=0;i< amll.size();i++) {
            locationString = locationString + "("+amll.get(i).getLatitude()+", "+amll.get(i).getLongitude()+")";
            if(i < amll.size()-1 && locationString.length() < 495){
                locationString += ", ";
                if(locationString.length() > 468){
                    return locationString;
                }
            }
        }
        return locationString;
    }

    /**
     * Convert images to the string representation
     * @param pics - pictures to send
     * @return arraylist of string representation of pictures
     */
    public static ArrayList<String> convertPhotosToStringRepresentation(ArrayList<Picture> pics) {
        ArrayList<String> listPhotos = new ArrayList<>();
        for(int i=0;i<pics.size();i++){
//            String stringEncodedImage =
//                    Base64.encodeToString(pics.get(i).getmImageAsByteArray(), Base64.DEFAULT);
//            listPhotos.add(stringEncodedImage);
            Bitmap bitmap = pics.get(i).getmImage();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bitmapByte = outputStream.toByteArray();
            listPhotos.add(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
//            try {
//                listPhotos.add(new String(bitmapByte));
//            }
//            catch(Exception e){
//                Log.d(TAG,"The encoding didn't work");
//            }
        }
        return listPhotos;
    }

    /**
     * Convert images to the string representation
     * @param pics - pictures to send
     * @return arraylist of string representation of pictures
     */
    public static byte[] convertPhotosToByteRepresentation(ArrayList<Picture> pics) {
        ArrayList<byte[]> listPhotos = new ArrayList<>();
        for(int i=0;i<pics.size();i++){
            String stringEncodedImage =
                    Base64.encodeToString(pics.get(i).getmImageAsByteArray(), Base64.DEFAULT);
            listPhotos.add(pics.get(i).getmImageAsByteArray());
        }
        return listPhotos.get(0);
    }

    public static Bitmap convertStringRepToPhoto(String s) {
        byte[] b = Base64.decode(s, Base64.DEFAULT);
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        Bitmap bmp = BitmapFactory.decodeStream(bis);
        return bmp;
    }
}
