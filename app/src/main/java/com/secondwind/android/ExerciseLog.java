package com.secondwind.android;

import java.util.List;

// This is the Class of exercises the user has completed. We use this to keep record of completed exercises.
public class ExerciseLog {
    private String Username;
    private String Email;
    private String GoogleId;
    private String LoginType;
    private String PhotoUrl;
    private List<String> Goals;

    public ExerciseLog() {

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
    //failed
    public List<String> getGoals() {
        return Goals;
    }

    public void setGoals(List<String> goals) {
        Goals = goals;
    }
}
