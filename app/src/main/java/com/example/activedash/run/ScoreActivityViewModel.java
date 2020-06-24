package com.example.activedash.run;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.activedash.FirebaseQueryLiveData;
import com.example.activedash.Repository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ScoreActivityViewModel extends AndroidViewModel {
    public static boolean scoreCalculatorDisplay = false;
    private FirebaseQueryLiveData liveData;
    private DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child("user");
    private Repository repository = new Repository();

    private int level, highestep, point;

    public ScoreActivityViewModel(@NonNull Application application) {
        super(application);
    }
    private long elapsedMillis,newExp,exp, expCap,currExp;
    private int count = 0;

    public static String userid;

    private double magnitudePrev =0,magnitude=0,magnitudeDelta=0;

    private boolean running = false;

    private double height, stride, distance = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setStride(double height) {
        if (height == 0){
            height = 5.2;
        }
        this.stride = height * 0.43;
    }

    public double getDistance() {
        return distance;
    }

    public void setupMagnitude(double x_acceleration, double y_acceleration, double z_acceleration){
        this.magnitude = Math.sqrt(x_acceleration*x_acceleration+y_acceleration*y_acceleration+z_acceleration*z_acceleration);
        magnitudeDelta = magnitude - magnitudePrev;
        magnitudePrev = magnitude;
    }

    public double getMagnitudeDelta(){
        return magnitudeDelta;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public void setElapsedMillis(long elapsedMillis) {
        this.elapsedMillis = elapsedMillis;
    }

    public void calculateDistance(int steps){
        double distanceInF = stride * steps;
        distance = distanceInF/3.28;
    }

    @NonNull
    public LiveData<DataSnapshot> getUserDataSnapshotLiveData(String uid) {
        liveData = new FirebaseQueryLiveData(dbUser.child(uid));
        return liveData;
    }

    public void setUserData(String level, String highestep, String point, String exp, String expCap,String height){
        this.level = Integer.parseInt(level);
        this.highestep = Integer.parseInt(highestep);
        this.point = Integer.parseInt(point);
        this.exp = Integer.parseInt(exp);
        this.expCap = Integer.parseInt(expCap);
        this.height = Double.parseDouble(height);
        setStride(this.height);
        Log.d("offset vm","setUserData : level: "+level+" highestep: "+highestep+" point: "+point+" exp: "+exp+" expCap: "+expCap);
    }

    public void setExpCap(int level) {
        int offset =level/10;
        this.expCap = offset * 100 + level * 50;
        Log.d("offset vm",this.expCap+" excap");
    }

    public long calculateNewExp(int stepcount, long timetakeninms){
        long seconds = timetakeninms/1000; //converts from ms to seconds
        newExp = (stepcount)+(stepcount/seconds);
        return newExp;
    }

    public long calculateCurrentExp(){
        currExp = exp + newExp;
        return currExp;
    }

    public long getExpCap() {
        return expCap;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHighestep() {
        return highestep;
    }

    public void setHighestep(int highestep) {
        this.highestep = highestep;
    }

    public void setPoint(long exp) {
        int iexp = (int) exp;
        float offset = (float) 20 / 100;
        float pointss = offset*iexp;
        this.point = this.point + (int)pointss;
        Log.d("offset vm","calculatePoints: exp: "+exp+" iexp: "+iexp+" offset: "+offset+" pointss: "+pointss+" this.point: "+this.point);
    }

    public int getPoint() {
        return point;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getExp() {
        return  exp;
    }

    public void updatePlayerData(String uid, int level, int stepCount, int points, long exp, long expCap){
       repository.updatePlayerData(uid, level, stepCount, points,  exp, expCap);
    }

    public void insertRunData(){
        Date date = new Date();
       repository.insertRunData(this.userid, date,this.distance,this.elapsedMillis/1000,this.count, this.point);
    }
}
