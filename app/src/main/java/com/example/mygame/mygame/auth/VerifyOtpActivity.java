package com.example.mygame.mygame.auth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mygame.mygame.R;

import com.example.mygame.mygame.utils.Constants;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText otp;
    private Button resendOtp, verifyOtp;
    private int REQUEST_CODE = 1;
    private String otpCode;
    private ProgressDialog dialog;
    private String activity_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        otp = (EditText) findViewById(R.id.verify_otp);
        resendOtp = (Button) findViewById(R.id.resend_otp);
        verifyOtp = (Button) findViewById(R.id.confirm_otp);
        // get activity type
        activity_type = getIntent().getStringExtra(Constants.ACTIVITY_TYPE);
        //checking wether the permission is already granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            // permission is already granted
            // here you can directly access contacts
        }
        else{
            //persmission is not granted yet
            //Asking for permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS},REQUEST_CODE);
        }
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(otp.getText().toString().trim())){
                    otp.setError(getString(R.string.enter_otp));
                }
                else {
                    if(otp.getText().toString().trim().contentEquals(getIntent().getStringExtra(Constants.OTP))){
                        if (activity_type.equalsIgnoreCase(Constants.REGISTRATION_ACTIVITY_TYPE)) {
                            Intent intent1 = new Intent(VerifyOtpActivity.this, CreateAccount.class);
                            intent1.putExtra(Constants.USERS_ID, getIntent().getStringExtra(Constants.USERS_ID));
                            intent1.putExtra(Constants.PHONE, getIntent().getStringExtra(Constants.PHONE));
                            startActivity(intent1);
                            finish();
                        }
                        else if (activity_type.equalsIgnoreCase(Constants.PASSWORD_RESET_ACTIVITY_TYPE)) {
                            Intent intent2 = new Intent(VerifyOtpActivity.this, ForgotPassword.class);
                            intent2.putExtra(Constants.USERS_ID, getIntent().getStringExtra(Constants.USERS_ID));
                            intent2.putExtra(Constants.PHONE, getIntent().getStringExtra(Constants.PHONE));
                            startActivity(intent2);
                            finish();

                        }
                    }
                }
            }
        });
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyOtpActivity.this, PhoneRegistration.class);
                intent.putExtra(Constants.ACTIVITY_TYPE, activity_type);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                otpCode = intent.getStringExtra("message");
                //Do whatever you want with the code here
                if(!otpCode.isEmpty() && otpCode.contentEquals(getIntent().getStringExtra(Constants.OTP))){
                    //send user details to server
                    Intent intent1 = new Intent(VerifyOtpActivity.this, CreateAccount.class);
                    intent1.putExtra(Constants.USERS_ID, getIntent().getStringExtra(Constants.USERS_ID));
                    startActivity(intent1);
                    finish();
                }
            }
        }
    };



}