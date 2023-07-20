package com.michaelnorval.howzit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String notification_channel_id="0x7f0c0031c";
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM1";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;
    private String setting_1 = "false";
    private String setting_2 = "false";
    private String setting_3 = "false";



    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Log.d("FROM", remoteMessage.getFrom());

        SharedPreferences prefs = this.getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
        //setting_1 = prefs.getString("Setting_1", "false");
        //setting_2 = prefs.getString("Setting_2", "false");
        setting_3 = prefs.getString("Setting_3", "false");

        //sendNotification(notification, data);
        if (setting_3.contains("true") || setting_2.contains("TRUE")) {
            sendNotificationTest("Howzit!", data.put("body", ""));
        }
    }


    //
    private void sendNotificationTest(String title, String messageBody) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //you can use your launcher Activity insted of SplashActivity, But if the Activity you used here is not launcher Activty than its not work when App is in background.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Add Any key-value to pass extras to intent
        intent.putExtra("PUSH", "yes");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //For Android Version Orio and greater than orio.
        //AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(notification_channel_id, CHANNEL_NAME, importance);
            mChannel.setDescription(messageBody);
            //mChannel.setSound(defaultSoundUri);
            mChannel.setSound((defaultSoundUri),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
            mChannel.setLockscreenVisibility(1);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setVibrationPattern(new long[]{100, 200});

            mNotifyManager.createNotificationChannel(mChannel);
        } //else {

            //For Android Version lower than oreo.
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_NAME);
            mBuilder.setContentTitle(title)
                    .setContentText(messageBody)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setColor(Color.parseColor("#FFD600"))
                    .setContentIntent(pendingIntent)
                    .setChannelId(notification_channel_id)
                    .setVibrate(new long[]{100, 200})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            mNotifyManager.notify(1, mBuilder.build());
            //count++;
        //}
    }

}