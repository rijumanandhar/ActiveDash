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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Repository {
    private static final String TAG = "This is "+Repository.class.getSimpleName();

    private DatabaseReference dbUser;
    private DatabaseReference dbRun;
    private DatabaseReference dbLeaderBoard;
    private DatabaseReference dbQuestUser;
    private DatabaseReference dbBadgeUser;
    private StorageReference userPicStorage;

    public Repository(){
        Log.d(TAG,"Repository Created");
        dbUser = FirebaseDatabase.getInstance().getReference().child("user");
        dbRun =  FirebaseDatabase.getInstance().getReference().child("run");
        userPicStorage = FirebaseStorage.getInstance().getReference().child("user_profile");
    }

    public String insertRunData(String userid, Date date, double distance, long timetaken, int stepCount, int coins){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String updatedAt = dateFormat.format(date);
        DatabaseReference mRef = dbRun.push();
        String runid = mRef.getKey();
        Log.d("offset",runid);
        mRef.child("userid").setValue(userid);
        mRef.child("date").setValue(updatedAt);
        mRef.child("distance").setValue(distance);
        mRef.child("timetaken").setValue(timetaken);
        mRef.child("stepCount").setValue(stepCount);
        mRef.child("coins").setValue(coins);
        return runid;
    }

    public void insertUserData(String uid,String name, String email, String username,
                               String dob, String picture, double height, int level,
                               int stepCount, int points, long exp, long expCap){
        DatabaseReference mRef = dbUser.child(uid);
        mRef.child("name").setValue(name);
        mRef.child("username").setValue(username);
        mRef.child("email").setValue(email);
        mRef.child("dob").setValue(dob);
        mRef.child("picture").setValue(picture);
        mRef.child("height").setValue(height);
        mRef.child("level").setValue(level);
        mRef.child("higheststep").setValue(stepCount);
        mRef.child("point").setValue(points);
        mRef.child("exp").setValue(exp);
        mRef.child("expcap").setValue(expCap);
    }

    public void updatePlayerData(String uid,int level,
                                 int stepCount, int points, long exp, long expCap){
        DatabaseReference mRef = dbUser.child(uid);
        mRef.child("level").setValue(level);
        mRef.child("higheststep").setValue(stepCount);
        mRef.child("point").setValue(points);
        mRef.child("exp").setValue(exp);
        mRef.child("expcap").setValue(expCap);
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

    public void updateUserInfo(String uid, String editedName, String username, String dob, double height){
        DatabaseReference mRef = dbUser.child(uid);
        mRef.child("name").setValue(editedName);
        mRef.child("username").setValue(username);
        mRef.child("dob").setValue(dob);
        mRef.child("height").setValue(height);
    }

    public void insertToLeaderBoard(String uid, int stepcount, int coins){
        dbLeaderBoard = FirebaseDatabase.getInstance().getReference().child("leaderboard").child(uid);
        dbLeaderBoard.child("stepcount").setValue(stepcount);
        dbLeaderBoard.child("coins").setValue(coins);
    }
}
