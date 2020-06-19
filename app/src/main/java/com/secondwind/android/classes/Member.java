package com.secondwind.android.classes;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String Username;
    private String Email;
    private String GoogleId;
    private String LoginType;
    private String PhotoUrl;
    private String LastWorkout;
    //private ArrayList WorkoutFocus;

    public Member() {

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String name) {
        Username = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public void setLoginType(String loginType) {
        LoginType = loginType;
    }

    public String getLoginType() {
        return LoginType;
    }

    public void setId(String googleId) {
        GoogleId = googleId;
    }

    public String getId() {
        return GoogleId;
    }


    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getLastWorkout() {
        return LastWorkout;
    }

    public void setLastWorkout(String lastWorkout) {
        LastWorkout = lastWorkout;
    }
//    public List getWorkoutFocus() {
//        return WorkoutFocus;
//    }
//
//    public void setWorkoutFocus(ArrayList workoutFocus) {
//        WorkoutFocus = workoutFocus;
//    }

}
