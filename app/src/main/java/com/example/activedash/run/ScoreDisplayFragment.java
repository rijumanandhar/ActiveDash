package com.example.activedash.run;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
    private static final String ARG_DISTANCE = "distance";
    private static final String ARG_POINT = "point";
    private static final String ARG_STEP = "step";
    private static final String ARG_EXP = "exp";
    private static final String ARG_TIME = "time";
    private static final String ARG_LEVEL = "level";

    private final String TAG = ScoreDisplayFragment.class.getSimpleName();
    View rootView;
    private Button backButton;
    private TextView distanceTv, pointTv, stepsTv, timeTv, levelTv,expTv;
    ScoreActivityViewModel viewModel;

    public ScoreDisplayFragment() {
        // Required empty public constructor
    }

    public static ScoreDisplayFragment newInstance(double distance, int point, int steps, long time, long exp,int level){
        ScoreDisplayFragment fragment = new ScoreDisplayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LEVEL, level);
        args.putInt(ARG_STEP, steps);
        args.putInt(ARG_POINT,point);
        args.putLong(ARG_EXP,exp);
        args.putLong(ARG_TIME,time);
        args.putDouble(ARG_DISTANCE,distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("offset scoreDisplay","onCreated");
        ScoreActivityViewModel.scoreCalculatorDisplay = false;
        rootView = inflater.inflate(R.layout.fragment_score_display, container, false);
        viewModel = ViewModelProviders.of(getActivity()).get(ScoreActivityViewModel.class);
        if (getArguments() != null) {
            viewModel.setLevel(getArguments().getInt(ARG_LEVEL));
            viewModel.setCount(getArguments().getInt(ARG_STEP));
            viewModel.setNewPoint(getArguments().getInt(ARG_POINT));
            viewModel.setElapsedMillis(getArguments().getLong(ARG_TIME));
            viewModel.setDistance(getArguments().getDouble(ARG_DISTANCE));
            viewModel.setNewExp(getArguments().getLong(ARG_EXP));
        }
        intViewComponent();
        return rootView;
    }

    private void intViewComponent(){
        distanceTv = rootView.findViewById(R.id.distanceTextView);
        pointTv = rootView.findViewById(R.id.pointsEarnedTextView);
        stepsTv = rootView.findViewById(R.id.stepCountTextView);
        timeTv = rootView.findViewById(R.id.timeTakenTextView);
        levelTv = rootView.findViewById(R.id.levelTextView);
        expTv = rootView.findViewById(R.id.expTextView);

        backButton = rootView.findViewById(R.id.backButton);

        distanceTv.setText(viewModel.getDistance()+"");
        pointTv.setText(viewModel.getNewPoint()+"");
        stepsTv.setText(viewModel.getCount()+"");
        timeTv.setText(viewModel.getElapsedMillis()+"");
        levelTv.setText(viewModel.getLevel()+"");
        expTv.setText(viewModel.getNewExp()+"");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });



    }

    public void goToMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
