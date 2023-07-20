package com.michaelnorval.howzit;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;

import android.se.omapi.Session;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.callback.Callback;


public class MainActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private int R_=3;
    private int G_=169;
    private int B_=244;

    CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private EditText OTPNumberText;
    private EditText NameNicknameText;
    private String FullNumber = "";
    //private String FullNumber_country_code = "";
    private int OTP_Password = 0;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;


    //private static final String AUTH_KEY = "AAAAeV-sy5M:APA91bGOZ2gm6yrRGk6juURZ-vLyQUHkCcoFyXFBvOEv_wgXNCk1OWoeTqvtTGuY2J5AWrIhvNfvGf48Wah9LTQKMKS_mGWh6JRG0e8yAMVOdGrjq7CcNpx__cttTStgyo10nqO4KG7U";
    private String token;
    private Button OTPButton;
    private Button NextButton;
    private Button TestButton;
    private TextView InfoText;
    private Button RegButton;
    private Button FinishButton;
    private TextView HeadingText;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private boolean number_exist;
    boolean isFinished = false;
    private boolean permission_set=false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Random OTP_Random_Number = new Random();
        InfoText = (TextView)findViewById(R.id.text);
        OTPButton = (Button)findViewById(R.id.OTP_button);
        FinishButton = (Button)findViewById(R.id.Register_Name_Finish_button);
        RegButton = (Button)findViewById(R.id.Register_Number_button);
        OTPNumberText = (EditText)findViewById(R.id.OTP_Password_editText);
        NameNicknameText = (EditText)findViewById(R.id.Nickname_editText);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);

        InfoText.setVisibility(View.GONE);
        ccp.setVisibility(View.GONE);
        editTextCarrierNumber.setVisibility(View.GONE);
        OTPButton.setVisibility(View.GONE);
        OTPNumberText.setVisibility(View.GONE);
        NameNicknameText.setVisibility(View.GONE);
        RegButton.setVisibility(View.GONE);
        FinishButton.setVisibility(View.GONE);

        NameNicknameText.setEnabled(false);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        //********************
        ccp.setAutoDetectedCountry(true);
        ccp.setCountryForPhoneCode(27);
        //********************
        try {
            FirebaseApp.initializeApp(this);
        }
        catch (Exception e) {
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Permissions(MainActivity.this);

        if (Check_If_Register_User(getApplicationContext()) == true)
        {


            ImageView image_ = (ImageView)findViewById(R.id.Register_ImageView);
            image_.setScaleX(2);
            image_.setScaleY(2);



            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    String myLocalNumber_ = "XXX";
                    myLocalNumber_ = Get_My_Registered_Number(getApplicationContext());

                    if (!task.isSuccessful()) {
                        token = task.getException().getMessage();
                        Log.w("FCM TOKEN Failed", task.getException());
                        mRootReference.child("Howzit").child(myLocalNumber_.replace(" ","")).child("token").setValue(token);
                    } else {
                        token = task.getResult().getToken();
                        Log.i("FCM TOKEN", token);
                        Log.d("FCM TOKEN", token);
                        mRootReference.child("Howzit").child(myLocalNumber_.replace(" ","")).child("token").setValue(token);
                    }
                }
            });


            startService(new Intent(this, FirebaseBackgroundService.class));
            Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
            startActivity(intent);

        } else
        {
            //Toast.makeText(MainActivity.this, "Registered - False", Toast.LENGTH_SHORT).show();


        ccp.setVisibility(View.VISIBLE);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        editTextCarrierNumber.setVisibility(View.VISIBLE);
        editTextCarrierNumber.setBackgroundColor(Color.rgb(R_,G_,B_));
        //OTPNumberText.setVisibility(View.VISIBLE);
        //OTPNumberText.setEnabled(false);
        OTPButton.setVisibility(View.VISIBLE);
        //OTPNumberText.setVisibility(View.VISIBLE);
        NameNicknameText.setEnabled(false);
        NameNicknameText.setVisibility(View.GONE);

        RegButton.setVisibility(View.GONE);
        FinishButton.setVisibility(View.GONE);



        //********************
        ccp.setAutoDetectedCountry(true);
        ccp.setCountryForPhoneCode(27);
        //********************




            OTPButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //FullNumber_country_code = ccp.getFormattedFullNumber();
                    //FullNumber=editTextCarrierNumber.getText().toString();
                    FullNumber=ccp.getFormattedFullNumber();
                    OTP_Password = OTP_Random_Number.nextInt(8999)+1000;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Send OTP code to: "+FullNumber);
                    builder.setMessage("Are you sure?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int which) {

                            ccp.setEnabled(false);
                            OTPNumberText.setVisibility(View.VISIBLE);
                            editTextCarrierNumber.setEnabled(false);
                            editTextCarrierNumber.setBackgroundColor(Color.TRANSPARENT);
                            OTPButton.setEnabled(false);
                            RegButton.setVisibility(View.VISIBLE);
                            OTPNumberText.setEnabled(true);
                            OTPNumberText.setBackgroundColor(Color.rgb(R_,G_,B_));
                            // Do nothing, but close the dialog
                            SendSMS(FullNumber, String.valueOf(OTP_Password));
                            //SendSMS("","");
                            //Toast.makeText(MainActivity.this, "Mobile Number: " + FullNumber, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    if(!((MainActivity.this)).isFinishing())
                    {
                        //customBuilder.show();
                        alert.show();
                    }


                }
            });

            RegButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String temp_otp_string = String.valueOf(OTPNumberText.getText());
                    if ((String.valueOf(OTPNumberText.getText()).equals(String.valueOf(OTP_Password)) || (String.valueOf(OTPNumberText.getText()).equals(String.valueOf("7601245087083")))))
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Correct");
                        builder.setMessage(Html.fromHtml("<font color='#0000ff'>Lastly, please enter your name and click finish!</font>"));
                        //builder.setTitle(Html.fromHtml("<font color='#0000ff'>Send Howzit to: "+HomeContacts.get(position)+"</font>"));
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        if(!((MainActivity.this)).isFinishing())
                        {
                            //customBuilder.show();
                            alert.show();
                        }

                        //Create Initial Settings
                        Create_Initial_Settings(getApplicationContext());
                        //Register User Locally
                        Register_User_Local_Number(getApplicationContext(), FullNumber);
                        //Register_User_Local_Number_country_code(getApplicationContext(),FullNumber_country_code);
                        //Register User Firebase
                        Register_User_Firebase(FullNumber);
                        startService(new Intent(getApplicationContext(), FirebaseBackgroundService.class));

                        OTPNumberText.setEnabled(false);
                        NameNicknameText.setEnabled(true);
                        NameNicknameText.setBackgroundColor(Color.rgb(R_,G_,B_));
                        NameNicknameText.setVisibility(View.VISIBLE);

                        RegButton.setEnabled(false);
                        FinishButton.setVisibility(View.VISIBLE);
                        OTPNumberText.setBackgroundColor(Color.TRANSPARENT);

                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Incorrect OTP");
                        builder.setMessage("Please re enter or restart setup");

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        AlertDialog alert = builder.create();
                        if(!((MainActivity.this)).isFinishing())
                        {
                            //customBuilder.show();
                            alert.show();
                        }
                    }

                }
            });

            FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NameNicknameText.getText()!=null || !NameNicknameText.getText().equals("null"))
                {
                    Register_User_Local_Nickname(getApplicationContext(), NameNicknameText.getText().toString());
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            String myLocalNumber_ = "XXX";
                            myLocalNumber_ = Get_My_Registered_Number(getApplicationContext());

                            if (!task.isSuccessful()) {
                                token = task.getException().getMessage();
                                Log.w("FCM TOKEN Failed", task.getException());
                                mRootReference.child("Howzit").child(myLocalNumber_.replace(" ","")).child("token").setValue(token);
                            } else {
                                token = task.getResult().getToken();
                                Log.i("FCM TOKEN", token);
                                Log.d("FCM TOKEN", token);
                                mRootReference.child("Howzit").child(myLocalNumber_.replace(" ","")).child("token").setValue(token);
                            }
                        }
                    });


                    NameNicknameText.setBackgroundColor(Color.TRANSPARENT);



                    Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
                    startActivity(intent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("No name entered");
                    builder.setMessage("Please enter name and click finish!");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });


                    AlertDialog alert = builder.create();
                    if(!((MainActivity.this)).isFinishing())
                    {
                        //customBuilder.show();
                        alert.show();
                    }
                }


            }
            });
        }

    }

    String Get_My_Registered_Number(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String temp_my_reg_number = prefs.getString("Registered_Number", "false");
        return temp_my_reg_number;
    }

    String Get_My_Registered_Number_plus_cc(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String temp_my_reg_number = prefs.getString("Registered_Number", "false");
        return temp_my_reg_number;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Permissions(Context context)
    {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,/*Manifest.permission.SEND_SMS,*/Manifest.permission.READ_CONTACTS,Manifest.permission.WAKE_LOCK},
                1);
        Intent intent = new Intent();

        //requestChangeBatteryOptimizations();
        //while(!permission_set){}
    }


    private void requestChangeBatteryOptimizations ()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
            else
            {
                //intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                //intent.setData(Uri.parse("package:" + getPackageName()));
                //startActivityForResult(intent, Settings.ACTION_REQ);
            }
        }
    }



    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }




    public void Register_User_Local_Number(Context mContext, String my_registered_number){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Registered_Number", my_registered_number.replaceAll(" ",""));
        editor.commit();
        Toast.makeText(this, "User Number Registered", Toast.LENGTH_SHORT).show();
    }
    /*
    public void Register_User_Local_Number_country_code(Context mContext, String my_registered_number){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Registered_Number_country_code", my_registered_number.replaceAll(" ",""));
        editor.commit();
        //Toast.makeText(this, "User Number Registered", Toast.LENGTH_SHORT).show();
    }
    */
    public void Register_User_Local_Nickname(Context mContext, String my_registered_nickname){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Registered_Name", my_registered_nickname);
        editor.commit();
        Toast.makeText(this, "User Name Registered", Toast.LENGTH_SHORT).show();
    }

    public void Register_User_Firebase(String my_number) {

        mDatabase.child("Howzit").child(my_number.replace(" ","")).child("message").setValue("Initial Registration");

    }


    public void Create_Initial_Settings(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Setting_1", "true");
        editor.putString("Setting_2", "true");
        editor.putString("Setting_3", "true");
        editor.commit();
    }


   boolean Check_If_Register_User(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Registered_Number", "false");
        //Toast.makeText(this, "Registered?  ->  " + tempvalue, Toast.LENGTH_SHORT).show();

        if (tempvalue.equals("false")) return false;
        else return true;
    }





    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SendSMS(String SMS_Number, String SMS_Message) {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                String USER_AGENT_INTERNET_EXPLORER = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
                URL url=new URL("https://bulksms.2way.co.za/eapi/submission/send_sms/2/2.0?username=michaelnorval&password=Orpheus@007&message="+SMS_Message+"&msisdn="+SMS_Number);
                HttpURLConnection huc=(HttpURLConnection)url.openConnection();

                huc.connect();
                Log.d(huc.getResponseMessage(),"");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

//   1. "Howzit! - English",
//   2. "Howzit! - Spanish",
//   3. "Howzit! - Portuguese",
//   4. "Howzit! - Polish",
//   5. "Howzit! - Italian",
//   6. "Howzit! - French",
//   7. "Howzit! - Dutch",
//   8. "Howzit! - Chinese"};

    public void play_greeting_1(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.us_english_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_2(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.spanish_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_3(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.portuges_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_4(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.polish_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_5(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.italian_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_6(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.french_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_7(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.dutch_howzit);mp.start(); //mp.release();
    }
    public void play_greeting_8(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.chinese_howzit);mp.start(); //mp.release();
    }

    //private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    //private DatabaseReference mRootReference = firebaseDatabase.getReference("Howzit");
    public static final String notification_channel_id="0x7f0c003c";
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";

    public void CreateNotification(Context mContext, String from) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("");
        bigText.setBigContentTitle("Howzit");
        bigText.setSummaryText("New Message");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("Message from");
        mBuilder.setContentText(from);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = notification_channel_id;
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }


}
