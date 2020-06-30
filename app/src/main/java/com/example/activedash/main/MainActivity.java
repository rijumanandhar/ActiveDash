package com.example.activedash.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.activedash.R;
import com.example.activedash.loginregister.LoginRegisterActivity;
import com.example.activedash.profile.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent loginRegisterIntent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    loginRegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //user can't go back
                    startActivity(loginRegisterIntent );
                }
            }
        };

        ViewPager vp = findViewById(R.id.ViewPager);
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(pagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

}
