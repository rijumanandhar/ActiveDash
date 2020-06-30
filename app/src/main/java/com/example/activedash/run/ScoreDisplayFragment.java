package com.example.activedash.run;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.activedash.R;
import com.example.activedash.main.MainActivity;
import com.google.firebase.database.DataSnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreDisplayFragment extends Fragment {
    private final String TAG = ScoreDisplayFragment.class.getSimpleName();
    View rootView;
    private Button backButton, runButton;
    private TextView distanceTv, coinsTv, stepsTv, timeTv;

    public ScoreDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("offset scoreDisplay","onCreated");
        ScoreActivityViewModel.scoreCalculatorDisplay = false;
        rootView = inflater.inflate(R.layout.fragment_score_display, container, false);
        intViewComponent();
        return rootView;
    }

    private void intViewComponent(){
        distanceTv = rootView.findViewById(R.id.distanceTextView);
        coinsTv = rootView.findViewById(R.id.pointsEarnedTextView);
        stepsTv = rootView.findViewById(R.id.stepCountTextView);
        timeTv = rootView.findViewById(R.id.timeTakenTextView);

        backButton = rootView.findViewById(R.id.backButton);
        runButton = rootView.findViewById(R.id.runButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment();
            }
        });

        ScoreActivityViewModel viewModel= ViewModelProviders.of(getActivity()).get(ScoreActivityViewModel.class);
        try{
            LiveData<DataSnapshot> liveData = viewModel.getRunDataSnapshotLiveData( ScoreActivityViewModel.runID);

            liveData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                   distanceTv.setText(dataSnapshot.child("distance").getValue().toString());
                   coinsTv.setText(dataSnapshot.child("coins").getValue().toString());
                    stepsTv.setText(dataSnapshot.child("stepCount").getValue().toString());
                   timeTv.setText(dataSnapshot.child("timetaken").getValue().toString() +"  runid:"+dataSnapshot.getKey());
                }
            });
        }catch (NullPointerException e){
            Log.d(TAG,"runId is empty");
        }

    }

    public void goToMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    public void replaceFragment(){
        //replace fragment
        Fragment fragment = new ScoreCalculaterFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scoreContainer, fragment);
        fragmentTransaction.commit();
    }
}
