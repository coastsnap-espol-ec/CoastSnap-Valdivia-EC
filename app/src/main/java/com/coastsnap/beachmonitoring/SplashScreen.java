package com.coastsnap.beachmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    public static long SPLASH_TIME_OUT = 3000;   //3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Se chequea si se abre por primera vez
                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                String firstTimeOpen = preferences.getString("FirstTimeOpen", "");

                if (firstTimeOpen.equals("Yes")) {
                    //Si a aplicacion es abierta por primera vez, envia al Dashboard
                    Intent toCamera = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(toCamera);
                } else {
                    //Caso contrario, se abre la camara directamente
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("FirstTimeOpen", "Yes");
                    editor.apply();
                    Intent toDashboardActivity = new Intent(SplashScreen.this, DashboardActivity.class);
                    startActivity(toDashboardActivity);

                }

            }
        }, SPLASH_TIME_OUT);
    }
}