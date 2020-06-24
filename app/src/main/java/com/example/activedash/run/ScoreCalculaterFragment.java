package com.example.activedash.run;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreCalculaterFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "ScoreCalculatorFragment";

    private TextView tv_steps, tv_distance;

    private Button reset_button,start_button,stop_button;

    private SensorManager sensorManager;

    private Chronometer chronometer;

    private ScoreActivityViewModel viewModel;

    private View rootView;


    public final ExecutorService timerExecutor =
            Executors.newFixedThreadPool(1); //creates a different thread for timer

    public ScoreCalculaterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScoreActivityViewModel.scoreCalculatorDisplay = true;
        rootView = inflater.inflate(R.layout.fragment_score_calculater, container, false);
        tv_steps = rootView.findViewById(R.id.tv_steps);
        reset_button = rootView.findViewById(R.id.button_reset);
        start_button = rootView.findViewById(R.id.button_start);
        stop_button = rootView.findViewById(R.id.button_stop);
        tv_distance = rootView.findViewById(R.id.tv_distance);
        chronometer = rootView.findViewById(R.id.chronometer_timer);

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

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (viewModel.isRunning()){
            float x_acceleration = event.values[0];
            float y_acceleration = event.values[1];
            float z_acceleration = event.values[2];

            viewModel.setupMagnitude(x_acceleration,y_acceleration,z_acceleration);

            if (viewModel.getMagnitudeDelta() > 10){
                int count = viewModel.getCount()+1;
                viewModel.setCount(count);
                tv_steps.setText(viewModel.getCount()+"");
                //tv_distance.setText(String.format("%.2f", viewModel.getDistance())+" m.");
            }
            //tv_steps.setText(String.valueOf(event.values[0]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        viewModel.setRunning(true);
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
        viewModel.setRunning(false);
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chronometer.stop();
            }
        });
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        viewModel.setElapsedMillis(elapsedMillis);
        calculateScore();
    }

    public void calculateScore(){

            viewModel.calculateDistance(viewModel.getCount());
            Log.d("offset","Distance is "+viewModel.getDistance());
            long newExp = viewModel.calculateNewExp(viewModel.getCount(),viewModel.getElapsedMillis());
            Log.d("offset","New Exp is "+newExp);
            long currentExp = viewModel.calculateCurrentExp();
            Log.d("offset","Current Exp is "+currentExp);
            if (currentExp>=viewModel.getExpCap()){
                Log.d("offset","Current Exp is greate than ExpCap "+currentExp+" > than "+viewModel.getExpCap());
                viewModel.setLevel(viewModel.getLevel()+1);
                long expDiff = currentExp-viewModel.getExpCap();
                viewModel.setExp(expDiff);
                viewModel.setExpCap(viewModel.getLevel());
            }else{
                Log.d("offset","Current Exp is less than ExpCap"+currentExp+" "+viewModel.getExpCap());
                viewModel.setExp(currentExp);
            }

            if (viewModel.getCount() > viewModel.getHighestep()){
                viewModel.setHighestep(viewModel.getCount());
            }

            viewModel.setPoint(newExp);
            viewModel.updatePlayerData(ScoreActivityViewModel.userid, viewModel.getLevel(),
                    viewModel.getHighestep(),viewModel.getPoint(),viewModel.getExp(),viewModel.getExpCap());
            viewModel.insertRunData();
            //replaceFragment();
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(ScoreActivityViewModel.class);
        tv_steps.setText(viewModel.getCount()+"");
        //tv_distance.setText(String.format("%.2f", viewModel.getDistance())+" m.");
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = viewModel.getElapsedMillis();
                chronometer.setBase(SystemClock.elapsedRealtime()-elapsedMillis);
                if (viewModel.isRunning()){
                    chronometer.start();
                }
            }
        });

        try{
            LiveData<DataSnapshot> liveUserData = viewModel.getUserDataSnapshotLiveData(ScoreActivityViewModel.userid);
            liveUserData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    String level = dataSnapshot.child("level").getValue().toString();
                    String higeststep = dataSnapshot.child("higheststep").getValue().toString();
                    String pointEarned = dataSnapshot.child("point").getValue().toString();
                    String exp = dataSnapshot.child("exp").getValue().toString();
                    String expCap = dataSnapshot.child("expcap").getValue().toString();
                    String height = dataSnapshot.child("height").getValue().toString();
                    viewModel.setUserData(level,higeststep,pointEarned,exp,expCap,height);
                }
            });
        }catch (NullPointerException e){
            Log.d(TAG,"userid is null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();
        Log.d(TAG,"onPause");
        //the sensor will stop detecting steps
        //sensorManager.unregisterListener(this);
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        viewModel.setElapsedMillis(elapsedMillis);
    }

    public void reset(){
        //sensorManager.unregisterListener(this);
        tv_steps.setText("0");
        viewModel.setCount(0);
//        information = information + " reset";
//        info.setText(information);
        tv_distance.setText("0");
        timerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });
    }

    public void replaceFragment(){
        //replace fragment
        Fragment fragment = new ScoreDisplayFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scoreContainer, fragment);
        fragmentTransaction.commit();
    }
}
