package jog.my.memory.GPS;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Devon Cormack on 2/9/15.
 */
public class MyLatLng implements Serializable {

    /***
     * Holds latitude and longitude data
     */

    private double latitude;

    private double longitude;

    public MyLatLng(LatLng ll){
        this.latitude = ll.latitude;
        this.longitude = ll.longitude;
    }

    public MyLatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LatLng toLatLng(){
        return new LatLng(this.latitude,this.longitude);
    }


}
