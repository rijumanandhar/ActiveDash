package com.example.activedash.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.activedash.loginregister.LoginRegisterActivity;
import com.example.activedash.R;
import com.example.activedash.Repository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "This is "+ProfileFragment.class.getSimpleName();
    private final int GALLERYPICK =2;
    private String userId;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView logoutText;
    private ImageButton profilePicBtn;

    private TextView nameText, emailText, usernameText, heightText, levelText, highStepCount, pointEarned, expText;

    DatabaseReference db_user;

    Repository repository;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"Profile Fragment On Create");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeComponents(rootView);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent loginRegisterIntent = new Intent(getContext(), LoginRegisterActivity.class);
                    loginRegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //user can't go back
                    startActivity(loginRegisterIntent );
                }else{
                    userId = firebaseAuth.getCurrentUser().getUid();
                    updateUI();
                }
            }
        };

        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERYPICK);
            }
        });

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                repository.insertUserPhoto(userId,resultUri);
                profilePicBtn.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void logout(){
        mAuth.signOut(); //mAuthStateListener should be added and if there are any users online
    }

    private void updateUI(){
        Log.d(TAG,"inside updateUI");
        ProfileViewModelFactory factory = new ProfileViewModelFactory(this,userId);
        ProfileViewModel viewModel= ViewModelProviders.of(this,factory).get(ProfileViewModel.class);

        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                nameText.setText(dataSnapshot.child("name").getValue().toString());
                emailText.setText(dataSnapshot.child("email").getValue().toString());
                usernameText.setText(dataSnapshot.child("username").getValue().toString());
                levelText.setText(dataSnapshot.child("level").getValue().toString());
                highStepCount.setText(dataSnapshot.child("higheststep").getValue().toString());
                pointEarned.setText(dataSnapshot.child("point").getValue().toString());
                int exp = Integer.parseInt(dataSnapshot.child("exp").getValue().toString());
                int expCap = Integer.parseInt(dataSnapshot.child("expcap").getValue().toString());
                float expPer = (float) exp / expCap*100 ;
                Log.d("offset profile","exp: "+exp+" expCap: "+expCap+" exp/expCap: "+exp/expCap+" (exp/expCap)*100: "+(exp/expCap)*100+" expPer: "+expPer);
                expText.setText(expPer+"%");
                String height = dataSnapshot.child("height").getValue().toString();
                if (!height.equals("0")){
                    heightText.setText(height+" cm");
                }
                String picture = dataSnapshot.child("picture").getValue().toString();
                if (!picture.equals("default")){
                    Picasso.get().load(picture).into(profilePicBtn);
                }
            }
        });
    }

    private void initializeComponents(View rootView){
        profilePicBtn = rootView.findViewById(R.id.imageButton);
        nameText = rootView.findViewById(R.id.nameText);
        emailText = rootView.findViewById(R.id.emailText);
        usernameText = rootView.findViewById(R.id.usernameTextView);
        profilePicBtn = rootView.findViewById(R.id.imageButton);
        logoutText = rootView.findViewById(R.id.logoutText);
        heightText = rootView.findViewById(R.id.heightTextView);
        levelText = rootView.findViewById(R.id.levelTextView);
        highStepCount = rootView.findViewById(R.id.stepTextView);
        pointEarned = rootView.findViewById(R.id.coinsTextView);
        expText = rootView.findViewById(R.id.expTextView);

        usernameText.setText("0");

        repository = new Repository();

        mAuth = FirebaseAuth.getInstance();
    }
}
