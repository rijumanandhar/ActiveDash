package com.example.activedash.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    Fragment fragment;
    String uid;

    public ProfileViewModelFactory (Fragment fragment, String uid){
        this.fragment = fragment;
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return  (T) new ProfileViewModel(fragment,uid);
    }
}
