package com.example.activedash.run;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.activedash.R;
import com.example.activedash.leaderboard.LeaderBoardActivity;
import com.example.activedash.quest.QuestActivity;
import com.example.activedash.quest.QuestViewModel;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends Fragment {

    ImageButton runButton;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private String userid;
    private TextView leaderBoardText;
    private TextView questText;

    public RunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    userid = firebaseAuth.getCurrentUser().getUid();
                }
            }
        };
        runButton = rootView.findViewById(R.id.runBtn);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to ScoreCalculator
                Intent intent = new Intent(getActivity(), ScoreActivity.class);
                ScoreActivityViewModel.scoreCalculatorDisplay = true;
                if (userid != null){
                    intent.putExtra("userid", userid);
                }
                startActivity(intent);
            }
        });
        questText = rootView.findViewById(R.id.questTextView);
        leaderBoardText = rootView.findViewById(R.id.leaderboardTextView);

        questText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuestActivity.class);
                QuestViewModel.displayedFragment =  QuestViewModel.LIST_DISPLAY;
                if (userid != null){
                    intent.putExtra("userid", userid);
                }
                startActivity(intent);
            }
        });

        leaderBoardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaderBoardActivity.class);
                startActivity(intent);
            }
        });


        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
