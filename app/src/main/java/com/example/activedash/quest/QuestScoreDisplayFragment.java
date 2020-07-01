package com.example.activedash.quest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.activedash.R;
import com.example.activedash.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestScoreDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestScoreDisplayFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    private static final String ARG_POINT = "point";
    private static final String ARG_TITLE = "title";
    private static final String ARG_EXP = "exp";
    private static final String ARG_TIME = "time";
    private static final String ARG_DISTANCE = "distance";

    Button homepagebtn;
    TextView distanceTv, timeTakenTv, pointEarnedTv, expTv, successTv, titleTv;

    QuestViewModel questViewModel;
    View rootView;

    public QuestScoreDisplayFragment() {
        // Required empty public constructor
    }

    public static QuestScoreDisplayFragment newInstance(String status, String title,int point, long exp, long timetaken, double distance) {
        QuestScoreDisplayFragment fragment = new QuestScoreDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_POINT,point);
        args.putLong(ARG_EXP,exp);
        args.putLong(ARG_TIME,timetaken);
        args.putDouble(ARG_DISTANCE,distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuestViewModel.displayedFragment = QuestViewModel.CALC_DISPLAY;
        questViewModel = ViewModelProviders.of(getActivity()).get(QuestViewModel.class);
        if (getArguments() != null) {
            questViewModel.setStatus(getArguments().getString(ARG_STATUS));
            questViewModel.setTitleCal(getArguments().getString(ARG_TITLE));
            questViewModel.setPointRewardedCal(getArguments().getInt(ARG_POINT));
            questViewModel.setElapsedMillis(getArguments().getLong(ARG_TIME));
            questViewModel.setDistanceDis(getArguments().getDouble(ARG_DISTANCE));
            questViewModel.setExpDis(getArguments().getLong(ARG_EXP));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_quest_score_display, container, false);
        homepagebtn = rootView.findViewById(R.id.backButton);
        successTv = rootView.findViewById(R.id.statusTextView);
        titleTv = rootView.findViewById(R.id.titleTextView);
        distanceTv = rootView.findViewById(R.id.distanceTextView);
        timeTakenTv = rootView.findViewById(R.id.timeTakenTextView);
        pointEarnedTv = rootView.findViewById(R.id.pointsEarnedTextView);
        expTv = rootView.findViewById(R.id.expEarnedTextView);

        homepagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        setUpViewModel();
        return rootView;
    }

    private void setUpViewModel() {
        titleTv.setText(questViewModel.getTitleCal());
        if (questViewModel.getStatus().equals("complete")){
            successTv.setText("Quest Completed!");
        }else{
            successTv.setText("Quest Failed!");
        }
        distanceTv.setText(String.format("%.2f", questViewModel.getDistanceDis())+"cm");
        long time = questViewModel.getElapsedMillis() / 1000;
        timeTakenTv.setText(time+" seconds");
        pointEarnedTv.setText(questViewModel.getPointRewardedCal()+"");
        expTv.setText(questViewModel.getExpDis()+"");
    }
}
