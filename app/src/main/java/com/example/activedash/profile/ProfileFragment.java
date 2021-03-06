package com.example.activedash.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activedash.loginregister.LoginRegisterActivity;
import com.example.activedash.R;
import com.example.activedash.Repository;
import com.example.activedash.main.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "This is "+ProfileFragment.class.getSimpleName();
    private final int GALLERYPICK =2;
    private String userId;

    public Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView logoutText;
    private ImageButton profilePicBtn;
    private Button editProfileBtn;
    private Dialog dialogWithMessage;
    GraphView graph;
    RecyclerView badges;
    Query badgeRef;

    ProfileViewModel viewModel;

    private TextView nameText, emailText, usernameText, heightText, levelText, highStepCount, pointEarned, expText, dobText;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    LineGraphSeries series;

    Repository repository;
    FirebaseRecyclerAdapter<UserBadge, BadgeViewHolder> firebaseRecyclerAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"Profile Fragment On Create");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent loginRegisterIntent = new Intent(mContext, LoginRegisterActivity.class);
                    loginRegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //user can't go back
                    startActivity(loginRegisterIntent );
                }else{
                    userId = firebaseAuth.getCurrentUser().getUid();
                    updateUI();
                }
            }
        };


        initializeComponents(rootView);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

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
        viewModel= ViewModelProviders.of(this,factory).get(ProfileViewModel.class);

        LiveData<DataSnapshot> liveData = viewModel.getDataSnapshotLiveData();

        Log.d("sad","inside update ui "+userId);
        badgeRef = FirebaseDatabase.getInstance().getReference().child("user_badge").orderByChild("userid").equalTo(userId);
       firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserBadge, BadgeViewHolder>(
                UserBadge.class,
                R.layout.single_badge,
                BadgeViewHolder.class,
                badgeRef
        ) {
            @Override
            protected void populateViewHolder(BadgeViewHolder badgeViewHolder, UserBadge badge, int i) {
                badgeViewHolder.setBadgeTitle(badge.getBadgename());
                badgeViewHolder.setIcon(badge.getIcon());
            }
        };
        badges.setAdapter(firebaseRecyclerAdapter);

        liveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                nameText.setText(dataSnapshot.child("name").getValue().toString());
                emailText.setText(dataSnapshot.child("email").getValue().toString());
                usernameText.setText(dataSnapshot.child("username").getValue().toString());
                dobText.setText(dataSnapshot.child("dob").getValue().toString());
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
                    heightText.setText(height+"");
                }
                String picture = dataSnapshot.child("picture").getValue().toString();
                if (!picture.equals("default")){
                    Picasso.get().load(picture).into(profilePicBtn);
                }
            }
        });

        LiveData<DataSnapshot> runLiveData = viewModel.getRunDataSnapshotLiveData(userId);
        runLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int)dataSnapshot.getChildrenCount()];
                int index = 0;
                for (DataSnapshot myData:dataSnapshot.getChildren()){
                    try{
                        String dateString = myData.child("date").getValue().toString();
                        int stepCount = Integer.parseInt(myData.child("stepCount").getValue().toString());
                        Date date = sdf.parse(dateString);
                        long longDate=date.getTime();
                        dp[index] = new DataPoint(longDate,stepCount);
                        index ++;
                    }catch (ParseException e){
                        Log.d(TAG,"date upparsable");
                    }
                }
                series.resetData(dp);
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
        dobText = rootView.findViewById(R.id.dobTextView);
        editProfileBtn = rootView.findViewById(R.id.editProfileButton);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        series = new LineGraphSeries();
        graph.addSeries(series);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return new SimpleDateFormat("yyyy/MM/dd").format(new Date((long) value));
                }else
                return super.formatLabel(value, isValueX);
            }
        });
        badges = rootView.findViewById(R.id.badgesRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        badges.setHasFixedSize(true);
        badges.setLayoutManager(gridLayoutManager);


        usernameText.setText("0");

        repository = new Repository();

        mAuth = FirebaseAuth.getInstance();
    }

    public void editProfile(){
        dialogWithMessage = new Dialog(getActivity());
        dialogWithMessage.setContentView(R.layout.floating_edit_profile);

        final EditText nameEt, usernameEt, dobEt, heightEt;
        Button editBtn;

        nameEt = dialogWithMessage.findViewById(R.id.nameTextFloat);
        usernameEt = dialogWithMessage.findViewById(R.id.userNameTextFloat);
        dobEt = dialogWithMessage.findViewById(R.id.dobTextFloat);
        heightEt = dialogWithMessage.findViewById(R.id.weightTextFloat);
        editBtn = dialogWithMessage.findViewById(R.id.editButtonFloat);

        nameEt.setText(nameText.getText());
        usernameEt.setText(usernameText.getText());
        dobEt.setText(dobText.getText());
        if (heightText.getText().toString().equals("--")){
            heightEt.setText("0");
        }else{
            heightEt.setText(heightText.getText());
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedName = nameEt.getText().toString();
                String username = usernameEt.getText().toString();
                String dob = dobEt.getText().toString();
                try{
                    double height =Double.parseDouble(heightEt.getText().toString());
                    //validate username
                    if (validateUsername(username)){
                        viewModel.updateUserInfo(editedName, username, dob, height);
                        dialogWithMessage.dismiss();
                    }else{
                        Log.d(TAG,"username not valid");
                    }
                }catch (NumberFormatException e){
                    Log.d(TAG,"height isn't in number");
                }
            }
        });

        dialogWithMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWithMessage.show();
    }

    public boolean validateUsername(String username){
        if (username.equals("")){
            Toast.makeText(getActivity(),"Username cannnot be empty",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView badgeIcon;
        TextView badgeTv ;
        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            badgeIcon = itemView.findViewById(R.id.badgeImage);
            badgeTv = itemView.findViewById(R.id.badgeTitle);
        }

        public void setBadgeTitle(String title){
            badgeTv.setText(title);
        }

        public void setIcon(String icon){
            Picasso.get().load(icon).into(badgeIcon);
        }
    }
}
