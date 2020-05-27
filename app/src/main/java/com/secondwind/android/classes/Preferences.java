package com.secondwind.android.classes;

import java.util.List;

public class Preferences {
    private List<String> Goals;
    private Integer numOfWorkouts;

    public Preferences() {

    }
    public Integer getNumOfWorkouts() {
        return numOfWorkouts;
    }

    public void setNumOfWorkouts(Integer num) {
        numOfWorkouts = num;
    }

    public List<String> getGoals() {
        return Goals;
    }

    public void setGoals(List<String> goals) {
        Goals = goals;
    }
}
