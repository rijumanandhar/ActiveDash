package com.example.activedash.quest;

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
import com.google.firebase.database.Query;

import java.util.Date;

public class QuestViewModel extends AndroidViewModel {
    public static final String LIST_DISPLAY = "selectQuest";
    public static final String SCORE_DISPLAY = "scoreQuest";
    public static final String CALC_DISPLAY = "scoreCalQuest";
    public static String displayedFragment;
    public static String userid;
    public static String batchid;
    public static boolean setBadge =true;

    private FirebaseQueryLiveData userLiveData, runLiveData;
    private DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child("user");
    private DatabaseReference dbBadge = FirebaseDatabase.getInstance().getReference().child("badge");
    private Repository repository = new Repository();

    private String titleCal;
    private String descriptionCal;
    private String questId;
    private String username;
    private long expDis;
    private double distanceDis;
    private int stepGoalCal;
    private int pointRewardedCal;
    private boolean isRunning = false;
    private long elapsedMillis,newExp,exp, expCap,currExp;
    private int count = 0;
    private double height, stride, distance = 0;
    private int level, highestep, oldPoint;
    private String badgeID, badgeIcon, badgeName;
    private String status = "incomplete";

    private double magnitudePrev =0,magnitude=0,magnitudeDelta=0;

    public QuestViewModel(@NonNull Application application) {
        super(application);
    }

    public String getTitleCal() {
        return titleCal;
    }

    public void setTitleCal(String titleCal) {
        this.titleCal = titleCal;
    }

    public String getDescriptionCal() {
        return descriptionCal;
    }

    public void setDescriptionCal(String descriptionCal) {
        this.descriptionCal = descriptionCal;
    }

    public int getStepGoalCal() {
        return stepGoalCal;
    }

    public void setStepGoalCal(int stepGoalCal) {
        this.stepGoalCal = stepGoalCal;
    }

    public int getPointRewardedCal() {
        return pointRewardedCal;
    }

    public void setPointRewardedCal(int pointRewardedCal) {
        this.pointRewardedCal = pointRewardedCal;
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public void setElapsedMillis(long elapsedMillis) {
        this.elapsedMillis = elapsedMillis;
    }

    public long getNewExp() {
        return newExp;
    }

    public void setNewExp(long newExp) {
        this.newExp = newExp;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getExpCap() {
        return expCap;
    }

    public void setExpCap(long expCap) {
        this.expCap = expCap;
    }

    public long getCurrExp() {
        return currExp;
    }

    public void setCurrExp(long currExp) {
        this.currExp = currExp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public double getDistance() {
        return distance;
    }

    public int getOldPoint() {
        return oldPoint;
    }

    public void setOldPoint() {
        this.oldPoint = oldPoint+pointRewardedCal;
    }

    @NonNull
    public LiveData<DataSnapshot> getUserDataSnapshotLiveData(String uid) {
        userLiveData = new FirebaseQueryLiveData(dbUser.child(uid));
        return userLiveData;
    }

    @NonNull
    public LiveData<DataSnapshot> getBadgeSnapshotLiveData(String qid) {
        Query query = dbBadge.orderByChild("questid").equalTo(qid);
        return new FirebaseQueryLiveData(query);
    }

    @NonNull
    public LiveData<DataSnapshot> checkUserBadgeSnapshotLiveData(String bid) {
        Query query = FirebaseDatabase.getInstance().getReference().child("user_badge").orderByChild("batchid").equalTo(bid);
        return new FirebaseQueryLiveData(query);
    }

    public void setUserData(String username,String level, String highestep, String point, String exp, String expCap,String height){
        this.username = username;
        this.level = Integer.parseInt(level);
        this.highestep = Integer.parseInt(highestep);
        this.oldPoint = Integer.parseInt(point);
        this.exp = Integer.parseInt(exp);
        this.expCap = Integer.parseInt(expCap);
        this.height = Double.parseDouble(height);
        setStride(this.height);
    }

    private void setStride(double height) {
        if (height == 0){
            height = 5.2;
        }
        this.stride = height * 0.43;
    }

    public void calculateDistance(int steps){
        double distanceInF = stride * steps;
        distance = distanceInF/3.28;
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

    public void insertLeaderBoard(){
        repository.insertToLeaderBoard(this.userid,username,highestep,oldPoint);
    }

    public void updatePlayerData(String uid, int level, int stepCount, int points, long exp, long expCap){
        repository.updatePlayerData(uid, level, stepCount, points,  exp, expCap);
    }

    public void insertUserQuestData(){
        repository.insertUserQuestData(this.userid,this.questId,this.pointRewardedCal,this.newExp, this.status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setupMagnitude(double x_acceleration, double y_acceleration, double z_acceleration){
        this.magnitude = Math.sqrt(x_acceleration*x_acceleration+y_acceleration*y_acceleration+z_acceleration*z_acceleration);
        magnitudeDelta = magnitude - magnitudePrev;
        magnitudePrev = magnitude;
    }
    public double getMagnitudeDelta(){
        return magnitudeDelta;
    }

    public void setBadgeData(String badgeID, String icon, String name){
        this.badgeID = badgeID;
        badgeIcon = icon;
        badgeName = name;
    }

    public void insertUserBadge(){
        repository.insertUserBadge(this.userid,badgeID,badgeIcon,badgeName);
    }

    public long getExpDis() {
        return expDis;
    }

    public void setExpDis(long expDis) {
        this.expDis = expDis;
    }

    public double getDistanceDis() {
        return distanceDis;
    }

    public void setDistanceDis(double distanceDis) {
        this.distanceDis = distanceDis;
    }
}
