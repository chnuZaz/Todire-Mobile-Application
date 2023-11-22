package com.example.todire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1000); // 1 second delay
                }
            });
        } else {
            Intent intent = new Intent(SplashScreen.this, LoginPage.class);
            startActivity(intent);
            finish();
        }
    }



}