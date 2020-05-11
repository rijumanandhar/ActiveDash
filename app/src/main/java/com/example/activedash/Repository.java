package com.example.activedash;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Repository {
    private DatabaseReference user;

    private StorageReference userPicStorage;

    Repository(){
        user = FirebaseDatabase.getInstance().getReference().child("user");
        userPicStorage = FirebaseStorage.getInstance().getReference().child("user_profile");

    }

    public void insertUserData(String uid,String name, String email, String username, String dob, String picture){
        DatabaseReference mRef = user.child(uid);
        mRef.child("name").setValue(name);
        mRef.child("username").setValue(username);
        mRef.child("email").setValue(email);
        mRef.child("dob").setValue(dob);
        mRef.child("picture").setValue(picture);
    }

    public String inserUserPhoto(String uid, Uri imageUri){
        final StorageReference userPicFilepath = userPicStorage.child(uid);
        UploadTask uploadTask = userPicFilepath.putFile(imageUri);
        Uri downloadUri=null;

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
                        //System.out.println("Upload " + photoStringLink);
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        return downloadUri.toString();
    }
}
