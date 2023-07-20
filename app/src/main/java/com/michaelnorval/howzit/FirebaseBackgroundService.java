package com.michaelnorval.howzit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseBackgroundService extends Service implements ValueEventListener {


    private String setting_1 = "false";
    private String setting_2 = "false";
    private String setting_3 = "false";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    //private DatabaseReference mHeadingReference = mRootReference.child("Howzit").child("+27829040797");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Context mContext;
    String temp_my_reg_number="";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO write your own code
        //return Service.START_NOT_STICKY;

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();





        //FirebaseDatabase.getInstance().goOffline();
        //FirebaseDatabase.getInstance().goOnline();
        //mDatabase.keepSynced(true);
        String my_local_number = Get_My_Registered_Number(getApplicationContext());
        //Toast.makeText(this, "my_local_number: "+my_local_number, Toast.LENGTH_LONG).show();
        //collection("users").whereGreaterThanOrEqualTo("username", "al")
        //DatabaseReference mHeadingReference = mRootReference.child("Howzit").child("+27829040797");
        DatabaseReference mHeadingReference = mRootReference.child("Howzit").child(my_local_number);
        mHeadingReference.addValueEventListener(this);
        //Toast.makeText(this, "Firebase Service Started", Toast.LENGTH_SHORT).show();

    }

    boolean Check_Setting_1(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Setting_1", "false");
        if (tempvalue.equals("false")) return false;
        else return true;
    }


    String Get_My_Registered_Number(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        temp_my_reg_number = prefs.getString("Registered_Number", "false");
        return temp_my_reg_number;
    }

//   1. "Howzit! - English",
//   2. "Howzit! - Spanish",
//   3. "Howzit! - Portuguese",
//   4. "Howzit! - Polish",
//   5. "Howzit! - Italian",
//   6. "Howzit! - French",
//   7. "Howzit! - Dutch",
//   8. "Howzit! - Chinese"};

    //@Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //if (dataSnapshot.getValue(String.class) != null) {
            String key = dataSnapshot.getKey();
            String msg_text = dataSnapshot.getValue().toString();


             SharedPreferences prefs = this.getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
             setting_1 = prefs.getString("Setting_1", "false");
             setting_2 = prefs.getString("Setting_2", "false");
             //setting_3 = prefs.getString("Setting_3", "false");
            //new MainActivity().showToast(this, "key: "+ key+ "  data:  "+text);
            //new MainActivity().play_greeting_1(this);
            //Toast.makeText(this, "Data Received key:  "+key , Toast.LENGTH_SHORT).show();
            if ((msg_text.contains("READ")) || (msg_text.contains("Registration")))
            {

            }
            else
            {
                //Toast.makeText(this, "Data Received text:  " +text, Toast.LENGTH_SHORT).show();
                //CreateNotification("Title",text);

                Acknowledge_Howzit();
                String[] split_response = msg_text.split(">>");
                String[] split_response_ = split_response[3].split(",");
                //new MainActivity().CreateNotification(getApplicationContext(),split_response[0]+" : "+split_response[1]+" : "+split_response[2]+" : "+split_response[3]);
                if (setting_1.contains("true") || setting_1.contains("TRUE")){Toast.makeText(this, "Howzit from:  " +split_response_[0], Toast.LENGTH_SHORT).show();}

                if (setting_2.contains("true") || setting_2.contains("TRUE")) {
                    if (split_response[0].contains("0")) {
                        new MainActivity().play_greeting_1(this);
                    }
                    if (split_response[0].contains("1")) {
                        new MainActivity().play_greeting_2(this);
                    }
                    if (split_response[0].contains("2")) {
                        new MainActivity().play_greeting_3(this);
                    }
                    if (split_response[0].contains("3")) {
                        new MainActivity().play_greeting_4(this);
                    }
                    if (split_response[0].contains("4")) {
                        new MainActivity().play_greeting_5(this);
                    }
                    if (split_response[0].contains("5")) {
                        new MainActivity().play_greeting_6(this);
                    }
                    if (split_response[0].contains("6")) {
                        new MainActivity().play_greeting_7(this);
                    }
                    if (split_response[0].contains("7")) {
                        new MainActivity().play_greeting_8(this);
                    }
                }
            }

        //if (setting_1.contains("true") || setting_1.contains("TRUE")){Setting_1.setChecked(true);} else {Setting_1.setChecked(false);}
        //if (setting_2.contains("true")|| setting_1.contains("TRUE")){Setting_2.setChecked(true);} else {Setting_2.setChecked(false);}
        //if (setting_3.contains("true")|| setting_1.contains("TRUE")){Setting_3.setChecked(true);} else {Setting_3.setChecked(false);}
        //temp_my_reg_number

        //}
    }



    //@Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public void Acknowledge_Howzit() {


        mRootReference.child("Howzit").child(temp_my_reg_number).child("message").setValue("READ");

    }






    }



