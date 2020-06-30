package com.example.activedash;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        query.addValueEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
