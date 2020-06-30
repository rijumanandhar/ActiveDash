package com.example.activedash.leaderboard;

public class LeaderBoard {
    String username;
    int point;
    int stepcount;

    public LeaderBoard(){

    }

    public LeaderBoard(String username, int coins, int stepscount) {
        this.username = username;
        this.point = coins;
        this.stepcount = stepscount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getStepcount() {
        return stepcount;
    }

    public void setStepcount(int stepcount) {
        this.stepcount = stepcount;
    }
}
