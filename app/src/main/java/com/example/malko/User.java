package com.example.malko;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class User {
    String uid, username, password, dob, date_created;

    public User(String uid, String username, String password, String dob, String date_created) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.date_created = date_created;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.dob = "";
        this.date_created = "";
    }


    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDateCreated() {
        return this.date_created;
    }

    public void setDateCreated(String date) {
        this.date_created = date;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
