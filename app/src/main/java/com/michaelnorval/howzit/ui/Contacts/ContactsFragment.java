package com.michaelnorval.howzit.ui.Contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.renderscript.Element;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.michaelnorval.howzit.MainActivity;
import com.michaelnorval.howzit.NavDrawerActivity;
import com.michaelnorval.howzit.R;
import com.michaelnorval.howzit.ui.Home.HomeFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContactsFragment extends Fragment {

    private ContactsViewModel addcontactsViewModel;
    private boolean number_exist;

    private ArrayAdapter aAdapter;
    //ArrayList<String> alContacts = new ArrayList<String>();
    //ArrayList<String> CheckedContacts = new ArrayList<String>();
    List<String> phone_Contacts_possible_duplicates = new ArrayList<>();
    List<String> phone_Contacts = new ArrayList<>();
    List<String> phoneBookNumberList = new ArrayList<>();
    List<String> finalList = new ArrayList<>();
    String myPhoneNumber = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addcontactsViewModel =
                ViewModelProviders.of(this).get(ContactsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_add_contacts, container, false);

        myPhoneNumber = Get_Registered_Number(getContext());
        GetAllContacts();
        CheckContacts();

        return root;
    }




    public void GetAllContacts(){

        //to store name-number pair


        try {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (!phoneNumber.contains(myPhoneNumber))
                {  phone_Contacts_possible_duplicates.add(name +"->"+ phoneNumber.replace(" ",""));}

                //Log.e("Contact list with name & numbers", " "+contacts);
            }
            phones.close();

            phone_Contacts = removeDuplicates((ArrayList<String>) phone_Contacts_possible_duplicates);

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    public void CheckContacts(){

        for(int tempcount = 0; tempcount < phone_Contacts.size(); tempcount++)
        {
            boolean Check;
            String[] separated =phone_Contacts.get(tempcount).split("->");
            String CheckString = separated[1].replace("*","")
                    .replace("#","").replace("(", "")
                    .replace(")", "").replace("-","")
                    .replace(" ","");

            phoneBookNumberList.add( separated[0]+ " -> " + CheckString);


        }
        CheckifTagsExist();

        //ArrayList<String> alContacts = new ArrayList<String>();
        //ArrayList<String> CheckedContacts = new ArrayList<String>();
    }

    private String Get_Registered_Number(Context mContext){
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_USER_REGISTERED_001", 0);
        String tempvalue = prefs.getString("Registered_Number", "false");
        if (tempvalue.equals("false")) return "";
        else return tempvalue;
    }

    public String removeLeadingZeroes(String digits) {
        //String.format("%.0f", Double.parseDouble(digits)) //Alternate Solution
        String regex = "^0+";
        return digits.replaceAll(regex, "");
    }


    private void CheckifTagsExist() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference firebaseDatabasePhoneNumbersRef = rootRef.child("Howzit");
        ValueEventListener valueEventListener = new ValueEventListener() {
            //@RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> firebaseDatabaseList = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String firebaseDatabasePhoneNumber = ds.getKey();
                    firebaseDatabaseList.add(firebaseDatabasePhoneNumber);
                }

                //phoneBookNumberList.clear();
                //phoneBookNumberList.add("Test 1 -> +27111");
                //phoneBookNumberList.add("Test 2 -> 27111");
                //phoneBookNumberList.add("Test 3 -> 07111");

                for(String phoneBookNumber : phoneBookNumberList) {
                    String[] separated = phoneBookNumber.split("->");
                    String to_be_checked = removeLeadingZeroes(separated[1].replace(" ", ""));
                    for (int tempcpountx_ = 0; tempcpountx_ < firebaseDatabaseList.size(); tempcpountx_++)
                    {

                        if (String.valueOf(firebaseDatabaseList.get(tempcpountx_)).contains(to_be_checked))
                        {
                            //finalList.add("Available: " + phoneBookNumber);
                            finalList.add("Available: " + separated[0].replace(" ", "")+" -> "+firebaseDatabaseList.get(tempcpountx_));
                        }
                        else
                        {
                            finalList.add("Invite: "+ phoneBookNumber);
                        }

                    }


                }


                Collections.sort(finalList,String.CASE_INSENSITIVE_ORDER);

                ListAdapter exAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, finalList);
                final ListView listView = getActivity().findViewById(R.id.core_list);
                listView.setAdapter(exAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                        if (listView.getItemAtPosition(position).toString().contains("Available"))
                        {
                            /*
                            AlertDialog.Builder builder_ = new AlertDialog.Builder(getContext());
                            String temp_to_check[] = listView.getItemAtPosition(position).toString().split("->");
                            //phoneBookNumber.split("->");
                            ArrayList<String> TempContacts_x = new ArrayList<String>();
                            TempContacts_x = loadArray("Temp", getContext());
                            if (String.valueOf(TempContacts_x).contains(temp_to_check[1]))
                            {
                                builder_.setTitle(Html.fromHtml("<font color='#0000ff'>Contact Exists!"+ temp_to_check[1]+"</font>"));
                                builder_.setNeutralButton(Html.fromHtml("<font color='#0000ff'>OK</font>"), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        /*
                                        Toast.makeText(getContext(), "Adding number to favorites!", Toast.LENGTH_LONG).show();
                                        ArrayList<String> TempContacts = new ArrayList<String>();
                                        TempContacts = loadArray("Temp", getContext());
                                        TempContacts.add(listView.getItemAtPosition(position).toString());
                                        saveArray(TempContacts , "Temp" , getContext());
                                        dialog.dismiss();
                                    }
                                });
                            }
                            else
                            {

                            }
*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            final String[] ItemToBeAddedToFavourites = listView.getItemAtPosition(position).toString().split(":");
                            //if (ItemToBeAddedToFavourites[1].startsWith());
                            builder.setTitle(Html.fromHtml("<font color='#0000ff'>Add to Contacts?"+ ItemToBeAddedToFavourites[1]+"</font>"));
                            builder.setMessage("Are you sure?");
                            builder.setIcon(R.drawable.add_to_contacts_dialog);
                            builder.setPositiveButton(Html.fromHtml("<font color='#0000ff'>YES</font>"), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {


                                    ArrayList<String> TempContacts = new ArrayList<String>();
                                    TempContacts = loadArray("Temp", getContext());

                                    if (String.valueOf(TempContacts).contains(listView.getItemAtPosition(position).toString()))
                                    {
                                        AlertDialog.Builder small_dialog=new AlertDialog.Builder(getContext());
                                        small_dialog.setMessage("Name already in list");
                                        small_dialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog small_dialogalertDialog=small_dialog.create();
                                        small_dialogalertDialog.show();

                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(), "Adding number to favorites!", Toast.LENGTH_LONG).show();
                                        TempContacts.add(listView.getItemAtPosition(position).toString());
                                        saveArray(TempContacts , "Temp" , getContext());
                                        dialog.dismiss();
                                    }


                                }
                                });

                                builder.setNegativeButton(Html.fromHtml("<font color='#0000ff'>NO</font>"), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                if(!((getActivity())).isFinishing())
                                {
                                    //customBuilder.show();
                                    alert.show();
                                }
                                }
                                else
                                {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                final String[] ItemToBeAddedToFavourites = listView.getItemAtPosition(position).toString().split(":");
                                builder.setIcon(R.drawable.share_dialog);
                                builder.setTitle("Invite "+ ItemToBeAddedToFavourites[1]);
                                builder.setMessage("Are you sure?");

                                builder.setPositiveButton(Html.fromHtml("<font color='#0000ff'>YES</font>"), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                    //Toast.makeText(getContext(), "Adding number to favorites!", Toast.LENGTH_LONG).show();
                                    //TempContacts.add(listView.getItemAtPosition(position).toString());
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Share the Howzit! app");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "You have been invited to start using the Howzit! app. ");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.michaelnorval.howzit");
                                    shareIntent.setType("text/plain");
                                    startActivity(shareIntent);
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton(Html.fromHtml("<font color='#0000ff'>NO</font>"), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            if(!((getActivity())).isFinishing())
                            {
                                //customBuilder.show();
                                alert.show();
                            }
                        }


                    }
                });
                //*****************************************
                //*****************************************
                Button Contacts_All = getView().findViewById(R.id.Contacts_Button_All);
                Button Contacts_Registered = getView().findViewById(R.id.Contacts_Button_Registered);
                Button Contacts_NotRegistered = getView().findViewById(R.id.Contacts_Button_Not_Registered);
                EditText Contacts_Filter = getView().findViewById(R.id.Contacts_Filter_EditText);


                Contacts_All.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> display_list = new ArrayList<>();
                        for(String s: finalList){
                                display_list.add(s);
                        }
                        //display_list = finalList;
                        ListAdapter exAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, display_list);
                        ListView listView = getActivity().findViewById(R.id.core_list);
                        listView.setAdapter(exAdapter2);
                    }
                });


                Contacts_Registered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> display_list = new ArrayList<>();
                        //display_list = finalList;
                        for(String s: finalList){
                            if(s.startsWith("Available")){
                                display_list.add(s);
                            }
                        }


                        ListAdapter exAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, display_list);
                        ListView listView = getActivity().findViewById(R.id.core_list);
                        listView.setAdapter(exAdapter2);

                        //Toast.makeText(getContext(), "Registered Contacts Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                Contacts_NotRegistered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> display_list = new ArrayList<>();
                        for(String s: finalList){
                            if(s.startsWith("Invite")){
                                display_list.add(s);
                            }
                        }

                        ListAdapter exAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, display_list);
                        ListView listView = getActivity().findViewById(R.id.core_list);
                        listView.setAdapter(exAdapter2);

                    }
                });

                Contacts_Filter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        List<String> display_list = new ArrayList<>();
                        for(String sss: finalList){
                            if(sss.contains(s.toString())){
                                display_list.add(sss);
                            }
                        }

                        ListAdapter exAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, display_list);
                        ListView listView = getActivity().findViewById(R.id.core_list);
                        listView.setAdapter(exAdapter2);

                    }
                });


                //*****************************************
                //*****************************************



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        firebaseDatabasePhoneNumbersRef.addListenerForSingleValueEvent(valueEventListener);

    }



    public boolean saveArray(ArrayList<String> array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_CONTACTS_001", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        try {
            for(int i=0;i<array.size();i++)
            {
                //String temp_string[] =  array.get(i).split(":");
                editor.putString(arrayName + "_" + i, array.get(i));
            }
        }catch(Exception e){
            Log.e("",e.getMessage());
        }


        return editor.commit();
    }




    public ArrayList<String> loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("HOWZIT_CONTACTS_001", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<String>();
        for(int i=0;i<size;i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }










}
