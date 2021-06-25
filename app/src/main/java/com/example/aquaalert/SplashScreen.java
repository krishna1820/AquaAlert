package com.example.aquaalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.aquaalert.Registration.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if( user!=null && user.isEmailVerified()){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                }
            }
            }, secondsDelayed * 1500);
        //Foreground Service
        Intent i = new Intent(this, MyService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(i);
        else
            startService(i);
    }
}