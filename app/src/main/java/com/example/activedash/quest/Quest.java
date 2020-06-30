package com.example.activedash.quest;

public class Quest {
    String description;
    String name;
    int point;
    int steps;

    public Quest() {
    }

    public Quest(String description, String name, int point, int steps) {
        this.description = description;
        this.name = name;
        this.point = point;
        this.steps = steps;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

}
