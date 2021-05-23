package com.example.malko;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.SecureRandom;
import java.util.Random;

public class Session {
    public static final String KEY_LOGIN = generateKey();
    public static final String SP_NAME = "userDetails";
    private static final Random r = new SecureRandom();
    private static final Random rand = new Random();
    SharedPreferences userLocalDatabase;

    public Session(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public static String generateKey() {
        try {
            return Integer.toString(rand.nextInt(999999));
        } catch (NullPointerException e) {
            return "0000000";
        }
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.putString("id", user.getUid());
        editor.putString("name", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.putString("dob", user.getDob());
        editor.putString("dateDreated", user.getDateCreated());
        editor.apply();
    }

    public boolean getUserloggedIn() {
        if (userLocalDatabase.getBoolean("loggedIn", false)) {
            return true;
        } else {
            return false;
        }
    }

    public User getLoggedInUser() {
        String username = userLocalDatabase.getString("name", "");
        String password = userLocalDatabase.getString("password", "");
        String uid = userLocalDatabase.getString("id", "");
        String dob = userLocalDatabase.getString("dob", "");
        String dateCreated = userLocalDatabase.getString("dateCreated", "2021-05-22");

        User sessionUser = new User(uid, username, password, dob, dateCreated);

        return sessionUser;

    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.commit();
    }

    public void clearSession() {
        SharedPreferences.Editor editor = userLocalDatabase.edit();
        editor.clear();
        editor.commit();
    }

}
