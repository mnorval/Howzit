package com.michaelnorval.howzit.ui.Settings;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.michaelnorval.howzit.MainActivity;
import com.michaelnorval.howzit.NavDrawerActivity;
import com.michaelnorval.howzit.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel slideshowViewModel;
    private TextView MyNameText;
    private TextView MyNumberText;
    private ToggleButton Setting_1;
    private ToggleButton Setting_2;
    private ToggleButton Setting_3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Setting_1 = (ToggleButton)root.findViewById(R.id.setting_popup_toggleButton);
        Setting_2 = (ToggleButton)root.findViewById(R.id.setting_sound_toggleButton);
        Setting_3 = (ToggleButton)root.findViewById(R.id.setting_push_toggleButton);
        MyNameText = (TextView)root.findViewById(R.id.Settings_my_name_TextView);
        MyNumberText = (TextView)root.findViewById(R.id.Settings_my_number_textView);

        MyNameText.setText(Get_Registered_Name(getContext()));
        MyNumberText.setText(Get_Registered_Number(getContext()));

        /*
        View view = inflater.inflate(R.layout.fragment_settings,
                container, false);
         */


        SharedPreferences prefs = getContext().getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
        String setting_1 = prefs.getString("Setting_1", "false");
        String setting_2 = prefs.getString("Setting_2", "false");
        String setting_3 = prefs.getString("Setting_3", "false");
        if (setting_1.contains("true") || setting_1.contains("TRUE")){Setting_1.setChecked(true);} else {Setting_1.setChecked(false);}
        if (setting_2.contains("true")|| setting_1.contains("TRUE")){Setting_2.setChecked(true);} else {Setting_2.setChecked(false);}
        if (setting_3.contains("true")|| setting_1.contains("TRUE")){Setting_3.setChecked(true);} else {Setting_3.setChecked(false);}




        final Button clearbutton = root.findViewById(R.id.clear_button);
        //final Button testbutton = root.findViewById(R.id.settings_test_button);

        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.left_hand_menu_settings);
                builder.setTitle("Confirm Reset?");
                //builder.setMessage("Are you sure?");

                builder.setPositiveButton(Html.fromHtml("<font color='#0000ff'>YES</font>"), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences prefs1 = getContext().getSharedPreferences("HOWZIT_CONTACTS_001", 0);
                        prefs1.edit().clear().commit();
                        Log.e("All Contacts Deleted", "");

                        SharedPreferences prefs2 = getContext().getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
                        prefs2.edit().clear().commit();
                        Log.e("Registration Deleted", "");

                        SharedPreferences prefs3 = getContext().getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
                        prefs2.edit().clear().commit();
                        Log.e("User Settings Deleted", "");

                        Toast.makeText(getContext(), "All Settings Deleted", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton(Html.fromHtml("<font color='#0000ff'>NO</font>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog fMapTypeDialog = builder.create();


                if(!((getActivity())).isFinishing())
                {
                    //customBuilder.show();
                    fMapTypeDialog.show();
                }


            }
        });

        Setting_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SharedPreferences prefs = getContext().getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("Setting_1", String.valueOf(Setting_1.isChecked()));
                    editor.commit();
                    //Toast.makeText(getContext(), "Value Stored:  "+String.valueOf(Setting_1.isChecked()), Toast.LENGTH_SHORT).show();
            }
        });

        Setting_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Setting_2", String.valueOf(Setting_2.isChecked()));
                editor.commit();
            }
        });

        Setting_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences("HOWZIT_USER_SETTINGS_001", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Setting_3", String.valueOf(Setting_3.isChecked()));
                editor.commit();
            }
        });

        /*testbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                new MainActivity().SendSMS("+27829040797","Test");
            }
        });
        */



        return root;
    }


    private String Get_Registered_Name(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Registered_Name", "false");
        if (tempvalue.equals("false")) return "";
        else return tempvalue;
    }



    private String Get_Registered_Number(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Registered_Number", "false");
        if (tempvalue.equals("false")) return "";
        else return tempvalue;
    }
    /*
    private String Get_Registered_Number_country_code(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Registered_Number_country_code", "false");
        if (tempvalue.equals("false")) return "";
        else return tempvalue;
    }
    */

}
