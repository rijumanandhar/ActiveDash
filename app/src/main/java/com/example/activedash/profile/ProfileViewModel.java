package com.example.activedash.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.activedash.FirebaseQueryLiveData;
import com.example.activedash.Repository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileViewModel extends ViewModel {
    private DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child("user");
    private final FirebaseQueryLiveData liveData;
    String uid;

    public ProfileViewModel(Fragment fragment, String uid){
        this.uid = uid;
        liveData = new FirebaseQueryLiveData(dbUser.child(uid));
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public void updateUserInfo(String editedName, String username, String dob, double height){
        Repository repository = new Repository();
        repository.updateUserInfo(uid, editedName, username, dob, height);
    }
}
