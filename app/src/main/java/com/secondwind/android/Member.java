package com.secondwind.android;

import android.net.Uri;

public class Member {
    private String Name, Email, Id, PhotoUrl;

    public Member() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUri() {
        return PhotoUrl;
    }

    public void setUri(Uri photoUrl) {
        PhotoUrl = photoUrl.toString();
    }
}
