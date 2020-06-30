package com.example.activedash.run;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.activedash.R;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_calculator);
        Intent intent = getIntent();
        if (intent.getExtras()!=null){
            ScoreActivityViewModel.userid=intent.getStringExtra("userid");
        }

        Fragment scoreCalculaterFragment = new ScoreCalculaterFragment();
        Fragment scoreDisplayFragment = new ScoreDisplayFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (ScoreActivityViewModel.scoreCalculatorDisplay){
            fragmentTransaction.replace(R.id.scoreContainer, scoreCalculaterFragment);
        }else{
            fragmentTransaction.replace(R.id.scoreContainer, scoreDisplayFragment);
        }
        fragmentTransaction.commit();
    }
}
