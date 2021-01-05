package com.coastsnap.beachmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    public static long SPLASH_TIME_OUT = 5000;   //4 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toMainActivity = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(toMainActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}