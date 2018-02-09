package com.tusadmin.trackurspot_admin.FcmServices;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tusadmin.trackurspot_admin.DatabaseHandler;
import com.tusadmin.trackurspot_admin.Databases.OverSpeedDatabase;
import com.tusadmin.trackurspot_admin.Databases.SOSDatabase;
import com.tusadmin.trackurspot_admin.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KishoreKumar on 29-Jul-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private DatabaseHandler dbHandler;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional

        dbHandler = new DatabaseHandler(getApplicationContext());

        Map<String, String> data =  remoteMessage.getData();

        try {
            if (remoteMessage.getNotification().getBody() != null) {
                Log.d(TAG, "From: " + remoteMessage.getFrom());
                Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
                //Calling method to generate notification
                sendNotification(remoteMessage.getNotification().getBody());
            }
        }
        catch (NullPointerException e){
            Log.w(TAG,"no notification");
        }

        if(data != null)
        {
            HashMap<String, String> hashMap = new HashMap<String, String>(data);

            if(hashMap.get("title").equals("sos")){
                Log.w(TAG,"sos="+ hashMap.get("message") );
                Intent i = new Intent("com.trackurspot.action.sos");
                dbHandler.addS0S(new SOSDatabase(hashMap.get("message"),hashMap.get("time")));
                broadCast("sos", i, hashMap);
            }
            if(hashMap.get("title").equals("overspeed")){
                Log.w(TAG,"overspeed="+ hashMap.get("message") +":"+ hashMap.get("time"));
                Intent i = new Intent("com.trackurspot.action.overspeed");
                dbHandler.addOverSpeed(new OverSpeedDatabase(hashMap.get("message"),hashMap.get("time")));
                broadCast("overspeed", i, hashMap);
            }
            if(hashMap.get("title").equals("location")){
                String[] tokens = hashMap.get("message").split("\"");
                Log.w(TAG,"location =" + "lat =" + tokens[3] + "lng =" + tokens[7]);
                Intent i = new Intent("com.trackurspot.action.location");
                broadCast("location", i, hashMap);
            }
            if( hashMap.get("title").equals("AllBus")) {

            }
        }
    }

    private void broadCast(String key, Intent i, HashMap<String, String> hashMap) {
        Bundle extras = new Bundle();
        extras.putSerializable(key, hashMap);
        i.putExtras(extras);
        this.sendBroadcast(i);
        Log.w(TAG,"Broadcast sent");
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TrackUrSpot-Admin")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());


    }


}
