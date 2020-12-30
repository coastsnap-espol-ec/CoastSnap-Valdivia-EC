package com.coastsnap.beachmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

    /**
    * Creado el 30/12/2020
    */

public class SplashScreenActivity extends AppCompatActivity {

    public static long SPLASH_TIME_OUT = 1000;   //2 segundos

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(toMainActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
