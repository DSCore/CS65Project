package jog.my.memory.GPS;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import jog.my.memory.R;

public class TrackingService extends Service {
    private static final String TAG = "TrackingService";

    private NotificationManager mNotificationManager;
    private PendingIntent mPendingIntent;
    private TrackingLocationReceiver mTLR;

    final static String ACTION_NOTIFYSERVICESTOP = "STOP!";

    public TrackingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Started the service!");
        this.displayTrackingNotification();
        this.mNotificationManager =  (NotificationManager) getApplicationContext()
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        //Register the receiver to end the service
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NOTIFYSERVICESTOP);

        this.mTLR = new TrackingLocationReceiver();
        registerReceiver(this.mTLR, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; //Since this will never be bound.
    }

    // Display tracking location
    private void displayTrackingNotification(){
        Intent resultIntent = new Intent(getBaseContext(), TraceFragment.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        this.mPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Jog My Memory")
                .setContentText("Making Memories!")
                .setContentIntent(mPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = TraceFragment.TRACING_NOTIFICATION;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getApplicationContext()
                        .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    /**
     * Stops displaying the tracking notification
     */
    private void stopDisplayTrackingNotification(){
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getApplicationContext()
                        .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.cancel(TraceFragment.TRACING_NOTIFICATION);
    }


    /**
     * Reads and handles signals from the MapsActivity
     */
    public class TrackingLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            //Triggers on ACTION_NOTIFYSERVICESTOP
            Log.d(TAG, "Received Stop Service!");
            destroySelf();
        }
    }

    private void destroySelf(){
        stopReceiver();
        mPendingIntent.cancel();
        mNotificationManager.cancelAll();
        stopSelf();
    }


    /**
     * Stop the receiver if it is started
     */
    public void stopReceiver(){
        try {
            unregisterReceiver(this.mTLR);
        }catch(IllegalArgumentException iae){
            Log.d(TAG,"The receiver has already been destroyed!");
        }
    }


}
