package com.michaelnorval.howzit.ui.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michaelnorval.howzit.Contact_Structure;
import com.michaelnorval.howzit.Contacts_Adapter;
import com.michaelnorval.howzit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;


public class HomeFragment extends Fragment {
//   1. "Howzit! - English",
//   2. "Howzit! - Spanish",
//   3. "Howzit! - Portuguese",
//   4. "Howzit! - Polish",
//   5. "Howzit! - Italian",
//   6. "Howzit! - French",
//   7. "Howzit! - Dutch",
//   8. "Howzit! - Chinese"};

    private HomeViewModel homeViewModel;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    //private DatabaseReference mHeadingReference = mRootReference.child("Howzit").child("+27829040797");
    //private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private WebView webview;

    ArrayList<String> HomeContactsTemp = new ArrayList<String>();
    ArrayList<String> HomeContacts = new ArrayList<String>();
    private List<Contact_Structure> ContactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Contacts_Adapter mAdapter;
    String language_selected = "1";
    private String token_ = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        GifImageView arrow_image = (GifImageView)root.findViewById(R.id.home_arrow);
        WebView webView = (WebView)root.findViewById(R.id.webview_favourites);
        webView.setVisibility(View.GONE);
        arrow_image.setVisibility(View.GONE);
        /*recyclerview
        recyclerView = root.findViewById(R.id.recycler_view);
        mAdapter = new Contacts_Adapter(ContactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerview*/
        GetFavContactData();

        final ListAdapter exAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, HomeContacts);
        final ListView listView = root.findViewById(R.id.listview_favourites);
        if (HomeContacts.size() > 0) {

            arrow_image.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            listView.setAdapter(exAdapter2);
        }

        else
        {
            arrow_image.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);

            String videoStr = "<html><body>Promo video<br><iframe width=\"360\" height=\"315\" src=\"https://www.youtube.com/embed/sH3dwCnYSek\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
            webView.setWebViewClient(new WebViewClient()
            {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });


            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadData(videoStr , "text/html", "utf-8");

            //webview.setBackgroundColor(Color.rgb(100,100,100));
            //webView.loadUrl("https://youtu.be/sH3dwCnYSek");


        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //AlertDialog.Builder alertdialogbuilder;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                /*
                String[] items = { "Howzit! - English", "Howzit! - Spanish", "Howzit! - Portuguese", "Howzit! - Polish",
                        "Howzit! - Italian", "Howzit! - French", "Howzit! - Dutch", "Howzit! - Chinese" };
                //Boolean[] boolArray = new Boolean[5];
                final boolean[] Selectedtruefalse = new boolean[]{ false, false, false, false,false, false, false, false};

                builder.setMultiChoiceItems(items, Selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Selectedtruefalse[which] = true;
                        //satelite = !satelite;
                        //mapView.setSatellite(satelite);
                    }
                });
                */
                final String[] SendToNameNumber = listView.getItemAtPosition(position).toString().split("->");
                builder.setTitle(Html.fromHtml("<font color='#0000ff'>Send Howzit to: "+HomeContacts.get(position)+"</font>"));
                //builder.setTitle("Send Howzit to: "+ HomeContacts.get(position));
                builder.setMessage("Are you sure?");

                        builder.setNeutralButton(Html.fromHtml("<font color='#FF0000'>DELETE</font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "DELETE", Toast.LENGTH_SHORT).show();
                                HomeContacts.remove(HomeContacts.get(position));
                                HomeContactsTemp.remove(HomeContactsTemp.get(position));

                                saveArray(HomeContactsTemp, "Temp", getContext());
                                listView.setAdapter(exAdapter2);

                            }
                        });


                        builder.setPositiveButton(Html.fromHtml("<font color='#0000ff'>YES</font>"), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SingleChoice(SendToNameNumber[1].replace(" ",""));
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


        return root;
    }

    public static String removeLeadingZeroes(String value) {
        return new Integer(value).toString();
    }

    private void SingleChoice(final String ReceiverNumber) {
        //final CharSequence[] LANG_TYPE_ITEMS = {"Howzit! - English",
        // "Howzit! - Spanish", "Howzit! - Portuguese", "Howzit! - Polish", "Howzit! - Italian", "Howzit! - French", "Howzit! - Dutch","Howzit! - Chinese"};

        final String[] strArray1 = new String[] {"Howzit! - English",
                 "Howzit! - Spanish", "Howzit! - Portuguese", "Howzit! - Polish",
                "Howzit! - Italian", "Howzit! - French", "Howzit! - Dutch","Howzit! - Chinese"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Greeting");
        builder.setItems(strArray1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                language_selected= Integer.toString(which);
                //Toast.makeText(getContext(),strArray1[which] + " Selected", Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(), "Sending Howzit! to:  "+SendToNameNumber[0] + " > " + SendToNameNumber[1], Toast.LENGTH_SHORT).show();
                //SendHowzit(String to_number, String from_number, String from_name,String message)
                String My_Phone_Number = Get_My_Registered_Number(getContext());
                String My_Nick_Name = Get_My_Registered_NickName(getContext());
                SendHowzit(ReceiverNumber,My_Phone_Number,My_Nick_Name,language_selected);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void SendHowzit(String to_number, String from_number, String from_name,String selected_greeting) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        mRootReference.child("Howzit").child(to_number.replace(" ",""))
                .child("message").setValue(selected_greeting +" >> "+currentDate + ">>"+ currentTime+ ">> "+from_name+" : "+from_number);
                final String from_name_ = from_name;
                final String from_number_ = from_number;
        mRootReference.child("Howzit").child(to_number.replace(" ","")).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token_ = dataSnapshot.getValue(String.class);
                //Toast.makeText(getContext(), "Token: "+token_, Toast.LENGTH_SHORT).show();
                new SendPushTask().execute();
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }


    private class SendPushTask extends AsyncTask<Void, Void, Void> {
        String result;
        @Override
        protected Void doInBackground(Void... voids) {
            String authKey = "AAAAcnosvdo:APA91bETuAGtSQELIDC8m66sDdFFfsO1ZQaqa9Rgk0d9TWekl6dPRAiglDvyUI60KbP5BetnKFM2NuiwDEER6eyGqCRYwFBzV3FHRSQV8lSVFvLIcTTjelKWC_sPkevrhf-x93W0DxBT"; // You FCM AUTH key
            String FMCurl = "https://fcm.googleapis.com/fcm/send";
            String My_Phone_Number = Get_My_Registered_Number(getContext());
            String My_Nick_Name = Get_My_Registered_NickName(getContext());
            try {
                URL url = new URL(FMCurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization","key="+authKey);
                conn.setRequestProperty("Content-Type","application/json");

                JSONObject json = new JSONObject();
                json.put("to",token_);
                JSONObject info = new JSONObject();
                info.put("title", "Title");
                info.put("body",My_Nick_Name+" : "+My_Phone_Number );
                info.put("content_available", true);
                info.put("priority", "high");
                json.put("data", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                Log.d("JSON: ", json.toString());
                wr.flush();
                conn.getInputStream();
                //Toast.makeText(getContext(), "Sending PUSH", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                e.printStackTrace();
                //Toast.makeText(getContext(), "PUSH error: "+e.toString(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            //textMessage.setText(result);
            //textLoad.setText("Finished");
            //super.onPostExecute(aVoid);
            Toast.makeText(getContext(), "Sending PUSH message", Toast.LENGTH_SHORT).show();
        }
    }



    private void GetFavContactData() {


        HomeContactsTemp = loadArray("Temp", getContext());
        HomeContacts.clear();

        for (int temp_count = 0; temp_count < HomeContactsTemp.size(); temp_count++)
        {
            String[] tempString = HomeContactsTemp.get(temp_count).split(":");
            HomeContacts.add(tempString[1]);
        }

        /*
        for (int tempcount = 0; tempcount < HomeContacts.size(); tempcount++) {
            Contact_Structure Contact_Item = new Contact_Structure(HomeContacts.get(tempcount), "Number", "Extra");
            ContactList.add(Contact_Item);

            //movieList.add(movie);
        }
        */

        /*recyclerview
        mAdapter.notifyDataSetChanged();
        recyclerview*/
    }




    public boolean saveArray(ArrayList<String> array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_CONTACTS_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        return editor.commit();
    }




    public ArrayList<String> loadArray(String arrayName, Context mContext) {

            SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_CONTACTS_001", 0);
            int size = prefs.getInt(arrayName + "_size", 0);
            ArrayList<String> array = new ArrayList<String>();
            for(int i=0;i<size;i++)
                array.add(prefs.getString(arrayName + "_" + i, null));
            Log.e("Loadarray loop: ", "Array Size:  "+ array.size());
        return array;

    }

    String Get_My_Registered_Number(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String temp_my_reg_number = prefs.getString("Registered_Number", "false");
        return temp_my_reg_number;
    }

    String Get_My_Registered_NickName(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String temp_my_reg_number = prefs.getString("Registered_Name", "false");
        return temp_my_reg_number;
    }


}
