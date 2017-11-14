package com.example.mygame.mygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mygame.mygame.auth.LoginActivity;
import com.example.mygame.mygame.game.GameHomeActivity;

import model.DBController;
import utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private DBController db;
    private SharedPreferences mSharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = new DBController(this);
        mSharedPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        if (!mSharedPrefs.contains(Constants.USERNAME)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent toLogin = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(toLogin);
                    finish();
                }
            }, 3000);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, GameHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }
}