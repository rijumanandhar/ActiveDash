package com.example.activedash.quest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activedash.R;
import com.google.firebase.database.DataSnapshot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestScoreCalculationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestScoreCalculationFragment extends Fragment implements SensorEventListener {
    private static final String ARG_TITLE = "QUEST TITLE";
    private static final String ARG_DESC = "QUEST DESCRIPTION";
    private static final String ARG_STEP = "QUEST STEP";
    private static final String ARG_COIN = "QUEST COIN";
    private static final String ARG_ID = "QUEST ID";

    QuestViewModel questViewModel;

    private static final String TAG = "ScoreCalculatorFragment";

    private TextView tv_steps, tv_steps_goal, tv_points_goal;

    private Button start_button,stop_button;

    private SensorManager sensorManager;

    private Chronometer chronometer;

    GifImageView imageView;

    private View rootView;

    public final ExecutorService timerExecutor =
            Executors.newFixedThreadPool(1); //creates a different thread for timer

    public QuestScoreCalculationFragment() {
        // Required empty public constructor
    }

    public static QuestScoreCalculationFragment newInstance(String questID,String title, String description, int step, int coin) {
        QuestScoreCalculationFragment fragment = new QuestScoreCalculationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, questID);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_STEP, step);
        args.putInt(ARG_COIN, coin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //viewmodel
            questViewModel = ViewModelProviders.of(getActivity()).get(QuestViewModel.class);
            questViewModel.setTitleCal(getArguments().getString(ARG_TITLE));
            questViewModel.setDescriptionCal(getArguments().getString(ARG_DESC));
            questViewModel.setStepGoalCal(getArguments().getInt(ARG_STEP));
            questViewModel.setPointRewardedCal(getArguments().getInt(ARG_COIN));
            questViewModel.setQuestId(getArguments().getString(ARG_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_quest_score_calculation, container, false);
        tv_steps = rootView.findViewById(R.id.quest_tv_steps);
        start_button = rootView.findViewById(R.id.quest_button_start);
        stop_button = rootView.findViewById(R.id.quest_button_stop);
        chronometer = rootView.findViewById(R.id.quest_chronometer_timer);
        tv_steps_goal = rootView.findViewById(R.id.quest_step_goal);
        tv_points_goal = rootView.findViewById(R.id.quest_points);
        imageView = rootView.findViewById(R.id.run_gif);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else {
            Toast.makeText(getActivity(),"Sensor not found",Toast.LENGTH_LONG).show();
        }

        setupViewModel();
        return rootView;
    }

    private void setupViewModel() {
        tv_steps.setText(questViewModel.getCount()+"");
        tv_steps_goal.setText(questViewModel.getStepGoalCal()+"");
        tv_points_goal.setText(questViewModel.getPointRewardedCal()+"");
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = questViewModel.getElapsedMillis();
                chronometer.setBase(SystemClock.elapsedRealtime()-elapsedMillis);
                if (questViewModel.isRunning()){
                    chronometer.start();
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });

        try{
            LiveData<DataSnapshot> liveUserData = questViewModel.getUserDataSnapshotLiveData(QuestViewModel.userid);
            liveUserData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String level = dataSnapshot.child("level").getValue().toString();
                    String higeststep = dataSnapshot.child("higheststep").getValue().toString();
                    String pointEarned = dataSnapshot.child("point").getValue().toString();
                    String exp = dataSnapshot.child("exp").getValue().toString();
                    String expCap = dataSnapshot.child("expcap").getValue().toString();
                    String height = dataSnapshot.child("height").getValue().toString();
                    questViewModel.setUserData(username,level,higeststep,pointEarned,exp,expCap,height);
                }
            });
        }catch (NullPointerException e){
            Log.d(TAG,"userid is null");
        }
        LiveData<DataSnapshot> questBadgeData = questViewModel.getBadgeSnapshotLiveData(questViewModel.getQuestId());
        Log.d("sad","score calc quest id "+questViewModel.getQuestId());
        questBadgeData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    QuestViewModel.batchid = snapshot.getKey();
                    String icon =snapshot.child("icon").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                    questViewModel.setBadgeData(QuestViewModel.batchid,icon,name);
                }
                LiveData<DataSnapshot> userBadgeData = questViewModel.checkUserBadgeSnapshotLiveData(QuestViewModel.batchid);
                userBadgeData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String user = snapshot.child("userid").getValue().toString();
                            if (user.equals(QuestViewModel.userid)){
                                QuestViewModel.setBadge = false;
                            }
                        }
                    }
                });
            }
        });
    }

    public void start() {
        questViewModel.setRunning(true);
        imageView.setVisibility(View.VISIBLE);
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                    }
                });
            }
        });

    }

    public void stop() {
        questViewModel.setRunning(false);
        imageView.setVisibility(View.INVISIBLE);
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chronometer.stop();
            }
        });
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        questViewModel.setElapsedMillis(elapsedMillis);
        calculateScore();
    }

    public void calculateScore(){
        if (questViewModel.getCount()>=questViewModel.getStepGoalCal()){
            questViewModel.setStatus("complete");
        }
        questViewModel.calculateDistance(questViewModel.getCount());
        long newExp = questViewModel.calculateNewExp(questViewModel.getCount(),questViewModel.getElapsedMillis());
        long currentExp = questViewModel.calculateCurrentExp();
        if (currentExp>=questViewModel.getExpCap()){
            questViewModel.setLevel(questViewModel.getLevel()+1);
            long expDiff = currentExp-questViewModel.getExpCap();
            questViewModel.setExp(expDiff);
            questViewModel.setExpCap(questViewModel.getLevel());
        }else{
            questViewModel.setExp(currentExp);
        }
        if(questViewModel.getStatus().equals("complete")){
            questViewModel.setOldPoint();
        }else{
            questViewModel.setPointRewardedCal(0);
        }
        if (questViewModel.getCount() > questViewModel.getHighestep()){
            questViewModel.setHighestep(questViewModel.getCount());
            questViewModel.insertLeaderBoard();
        }
        questViewModel.updatePlayerData(QuestViewModel.userid, questViewModel.getLevel(),
                questViewModel.getHighestep(),questViewModel.getOldPoint(),questViewModel.getExp(),questViewModel.getExpCap());
        questViewModel.insertUserQuestData();
        if(questViewModel.getStatus().equals("complete")  && questViewModel.setBadge){
            questViewModel.insertUserBadge();
        }
        resetViewModel();
        replaceFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();
        Log.d(TAG,"onPause");
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        questViewModel.setElapsedMillis(elapsedMillis);
    }

    public void resetViewModel(){
        //the sensor will stop detecting steps
        //sensorManager.unregisterListener(this);
        //questViewModel.setCount(0);
        //questViewModel.setElapsedMillis(0);
    }

    public void replaceFragment(){
        //replace fragment
        QuestScoreDisplayFragment questScoreDisplayFragment = QuestScoreDisplayFragment.newInstance(
                questViewModel.getStatus(),
                questViewModel.getTitleCal(),
                questViewModel.getPointRewardedCal(),
                questViewModel.getNewExp(),
                questViewModel.getElapsedMillis(),
                questViewModel.getDistance()
        );
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.questContainerFragment, questScoreDisplayFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (questViewModel.isRunning()){
            float x_acceleration = event.values[0];
            float y_acceleration = event.values[1];
            float z_acceleration = event.values[2];
            questViewModel.setupMagnitude(x_acceleration,y_acceleration,z_acceleration);
            if (questViewModel.getMagnitudeDelta() > 10){
                int count = questViewModel.getCount()+1;
                questViewModel.setCount(count);
                tv_steps.setText(questViewModel.getCount()+"");
                if (questViewModel.getCount()>=questViewModel.getStepGoalCal()){
                    questViewModel.setStatus("complete");
                    stop();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
