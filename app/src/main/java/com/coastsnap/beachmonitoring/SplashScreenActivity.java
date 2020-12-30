package com.coastsnap.beachmonitoring;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

    /**
    * Creado el 30/12/2020
    */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent toMainActivity = new Intent(this, MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }
}
