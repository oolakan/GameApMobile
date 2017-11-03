package com.example.mygame.mygame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import model.DBController;
import utils.Constants;

public class AccountSettings extends AppCompatActivity {

    private TextView name,email, phone;
    private DBController db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        db = new DBController(this);
        setViews();
        setValueToViews();
    }

    public void setViews() {
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone_no);
    }

    public TextView getName() {
        return name;
    }

    public void setValueToViews() {
        name.setText(db.getUser().get(Constants.USER_NAME));
        email.setText(db.getUser().get(Constants.USER_EMAIL));
        phone.setText(db.getUser().get(Constants.USER_PHONE_NO));
    }
}
