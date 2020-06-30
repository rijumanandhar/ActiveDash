package com.example.activedash.quest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.activedash.R;

public class QuestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        Intent intent = getIntent();
        if (intent.getExtras()!=null){
            QuestViewModel.userid=intent.getStringExtra("userid");
        }

        if (QuestViewModel.displayedFragment == QuestViewModel.LIST_DISPLAY){
            SelectQuestFragment selectQuestFragment = SelectQuestFragment.newInstance();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.questContainerFragment, selectQuestFragment);
            fragmentTransaction.commit();

        }else if (QuestViewModel.displayedFragment == QuestViewModel.SCORE_DISPLAY){


        }else if (QuestViewModel.displayedFragment == QuestViewModel.CALC_DISPLAY){
            Fragment displayQuestFragment = new QuestScoreDisplayFragment();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.questContainerFragment, displayQuestFragment);
            fragmentTransaction.commit();
        }

    }
}
