package com.example.mygame.mygame.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mygame.mygame.MainActivity;
import com.example.mygame.mygame.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import utils.Constants;

public class PhoneRegistration extends AppCompatActivity {

    private EditText phoneNoView, countryCode;
    private Button confirmPhoneView;
    private ProgressDialog dialog;
    private String phoneNo, countryCd;
    private ImageButton close;
    private String countryISOCode;
    private String countryZipcode;
    private String[] countryCodeList;
    private EditText codeNumber;
    private String activity_type;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_registration);
        phoneNoView = (EditText) findViewById(R.id.phone_no);
        confirmPhoneView = (Button) findViewById(R.id.confirm_phone);
        countryCodeList = this.getResources().getStringArray(R.array.CountryCodes);
        codeNumber = (EditText) findViewById(R.id.codeNumber);
        phoneNoView.requestFocus();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        // get activity type
        activity_type = getIntent().getStringExtra(Constants.ACTIVITY_TYPE);
        TelephonyManager teleMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (teleMgr != null){
            countryISOCode = teleMgr.getSimCountryIso();
            for(int i = 0; i < countryCodeList.length; i++){
                String  code = countryCodeList[i];
                //    Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();
                String[] countryExt = code.split(",");
                String zipCode = countryExt[1];

                if(zipCode.equalsIgnoreCase(countryISOCode)){
                    countryZipcode = countryExt[0];
                }
            }
        }
        codeNumber.setText(countryZipcode);

        phoneNoView.setFocusable(true);
        confirmPhoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneNoView.getText().toString().trim();
                String countryEstension = codeNumber.getText().toString().trim();
                phoneNo = countryEstension+phone;
                //if phone number supplied is valid send otp to phone number
                if(phone.matches("[0-9]+") && phone.length() > 5 ){
                    if (activity_type.equalsIgnoreCase(Constants.REGISTRATION_ACTIVITY_TYPE)) {
                        Log.e("Phone Number", phoneNo);
                        new SendOtp().execute(Constants.SEND_OTP_FOR_REG_URL);
                    }
                    else if(activity_type.equalsIgnoreCase(Constants.PASSWORD_RESET_ACTIVITY_TYPE)) {
                        new SendOtp().execute(Constants.SEND_OTP_FOR_PASSWORD_RESET_URL);
                    }
                    else {
                    }
                }
                else {
                    phoneNoView.setError(getString(R.string.invalid_phone_no));
                    phoneNoView.requestFocus();
                }
            }
        });

    }
    /**
     * Send OTP Code to provided phone number
     */
    private class SendOtp extends AsyncTask<String, Void, String> {
        public SendOtp(){
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try{
                return POST(params[0]);
            }
            catch (Exception ex){
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", ex.getMessage());
                ex.printStackTrace();
            }
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            showProgres();
        }

        protected void onPostExecute(String result){
            hideProgress();
            Log.e("Result", result);
            Log.d("Result", result);
            try {
                final JSONObject json = new JSONObject(result);
                JSONObject otp = json.getJSONObject(Constants.OTP);
                String otpCode = otp.getString(Constants.CODE);
                String users_id = otp.getString(Constants.USERS_ID);
                if(!otpCode.isEmpty()){
                    Intent intent = new Intent(PhoneRegistration.this, VerifyOtpActivity.class);
                    intent.putExtra(Constants.OTP, otpCode);
                    intent.putExtra(Constants.USERS_ID, users_id);
                    intent.putExtra(Constants.ACTIVITY_TYPE, activity_type);
                    intent.putExtra(Constants.PHONE, phoneNo);
                    startActivity(intent);
                    finish();
                }
            }catch (JSONException e)
            {
                // TODO Auto-generated catch block
                Log.e("Exception", e.getMessage());
                Log.d("Exception", e.getMessage());
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Post customer phone number
     * @param url
     * @return
     * @throws JSONException
     */
    public String POST(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";

        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            params.put(Constants.PHONE, phoneNo);
            json = params.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamtoString(inputStream);
            } else
                result = "Did not work";
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Convert input to stream
     * @param inputStream
     * @return
     * @throws IOException
     */
    public String convertInputStreamtoString(InputStream inputStream) throws IOException {
        String result = "";
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        reader.close();
        return result;
    }
    //show progress
    public void showProgres(){
       progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgress(){
       progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(PhoneRegistration.this, MainActivity.class));
        finish();
    }
}