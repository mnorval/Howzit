package com.michaelnorval.howzit.ui.Contacts;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.michaelnorval.howzit.R;

public class ContactsViewModel extends ViewModel {

    private MutableLiveData<String> mText;



    public ContactsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }




    public LiveData<String> getText() {
        return mText;
    }
}

