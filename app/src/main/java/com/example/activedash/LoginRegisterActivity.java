package com.example.activedash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginRegisterActivity extends AppCompatActivity {
    private Fragment fragment;
    private TextView loginSignupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        fragment = new LoginFragment();
        loadFragment(fragment);

        loginSignupText = findViewById(R.id.loginSignUpText);
        loginSignupText.setText("Don't have an account? Sign Up here.");
        loginSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSignupText.setPaintFlags(loginSignupText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                if (fragment instanceof  LoginFragment){
                    fragment = new RegisterFragment();
                    loadFragment(fragment);
                    loginSignupText.setText("Already have an account? Sign in here.");
                }else if (fragment instanceof  RegisterFragment){
                    fragment = new LoginFragment();
                    loadFragment(fragment);
                    loginSignupText.setText("Don't have an account? Sign Up here.");
                }
            }
        });

    }

    public void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

    }
}
