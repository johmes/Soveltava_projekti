package com.example.malko;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.malko.ui.login.LoginActivity;
import com.example.malko.ui.signup.SignupActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private final int STR_SPLASH_TIME = 3000;
    public Class destination;
    private boolean debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startSplashTimer();
        debug = true;
    }

    private void startSplashTimer() {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (debug) {
                        destination = MainActivity.class;
                    } else {
                        destination = SignupActivity.class;
                    }
                    Intent intent = new Intent(SplashActivity.this, destination);
                    startActivity(intent);
                    finish();
                }
            }, STR_SPLASH_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}