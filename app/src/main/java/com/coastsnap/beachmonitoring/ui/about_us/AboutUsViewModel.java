package com.coastsnap.beachmonitoring.ui.about_us;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutUsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is about us fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}