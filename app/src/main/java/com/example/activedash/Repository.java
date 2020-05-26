package com.example.activedash;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Repository {
    private static final String TAG = "This is "+Repository.class.getSimpleName();

    private DatabaseReference dbUser;

    private StorageReference userPicStorage;

    public Repository(){
        Log.d(TAG,"Repository Created");
        dbUser = FirebaseDatabase.getInstance().getReference().child("user");
        userPicStorage = FirebaseStorage.getInstance().getReference().child("user_profile");
    }

    public void insertUserData(String uid,String name, String email, String username, String dob, String picture){
        DatabaseReference mRef = dbUser.child(uid);
        mRef.child("name").setValue(name);
        mRef.child("username").setValue(username);
        mRef.child("email").setValue(email);
        mRef.child("dob").setValue(dob);
        mRef.child("picture").setValue(picture);
    }

    public void insertUserPhoto(final String uid, Uri imageUri){
        Log.d(TAG, "inside userphoto ");
        Log.d(TAG, "uid"+uid);
        final StorageReference userPicFilepath = userPicStorage.child(uid);
        Log.d(TAG, "reference put ");
        UploadTask uploadTask = userPicFilepath.putFile(imageUri);
        Log.d(TAG, "image put ");

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return userPicFilepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    //System.out.println("Upload " + downloadUri);
                    //Toast.makeText(, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                    if (downloadUri != null) {
                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        Log.d(TAG, "downloadurl " + photoStringLink);
                        dbUser.child(uid).child("picture").setValue(photoStringLink);
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


}
