package com.example.mygame.mygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.LoginFilter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.mygame.mygame.auth.CreateAccount;
import com.example.mygame.mygame.auth.LoginActivity;
import com.example.mygame.mygame.auth.PhoneRegistration;

import utils.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private Button signInButton, signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        setViews();
        setOnClickListener();
    }

    private void setOnClickListener() {
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    private void setViews() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.sign_up_button:
                Intent intent = new Intent(MainActivity.this, PhoneRegistration.class);
                intent.putExtra(Constants.ACTIVITY_TYPE, Constants.REGISTRATION_ACTIVITY_TYPE);
                startActivity(intent);
                finish();
                break;
        }
    }
}
