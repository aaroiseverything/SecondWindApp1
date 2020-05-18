package com.secondwind.android;

import android.net.Uri;

public class Member {
    private String Username, Email, GoogleId, LoginType, PhotoUrl;

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
}
