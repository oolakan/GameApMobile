package com.example.mygame.mygame.auth;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.game.GameHomeActivity;

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

import model.User;
import model.DBController;
import utils.Constants;


public class CreateAccount extends AppCompatActivity implements View.OnClickListener {
    private Button create_id, sign_in;
    private EditText full_name, userLocationView, email_address, password;
    private Button terms_conditions;
    private ImageButton closeView;

    private String userLocation;
    private String fullName;
    private String emailAddress;
    private String _password;

    private DBController db;
    private double longitude, latitude;
    private ProgressBar progressBar;

    private Button fb;
    private ImageButton close;
    private String name, email, user_social_id, img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        db = new DBController(this);
        setViews();
        setClicks();

    }

    private void setClicks() {
        create_id.setOnClickListener(this);
        terms_conditions.setOnClickListener(this);
    }

    private void setViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        create_id = (Button) findViewById(R.id.sign_up);
        sign_in = (Button) findViewById(R.id.sign_in);
        full_name = (EditText) findViewById(R.id.full_name);
        email_address = (EditText) findViewById(R.id.email_address);
        password = (EditText) findViewById(R.id.password);
        userLocationView = (EditText) findViewById(R.id.user_location);
        terms_conditions = (Button) findViewById(R.id.terms_conditions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sign_up:
                validate();
                break;
            case R.id.terms_conditions:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rashollinvestment.com/")));
                break;
            case R.id.sign_in:
                startActivity(new Intent(CreateAccount.this, LoginActivity.class));
                finish();
        }
    }

    private void validate() {
        fullName = full_name.getText().toString().trim();
        emailAddress = email_address.getText().toString().trim();
        _password = password.getText().toString().trim();
        userLocation = userLocationView.getText().toString().trim();
        if (fullName.isEmpty()) {
            full_name.setError(getString(R.string.enter_name));
            full_name.requestFocus();
            return;
        }

        if (emailAddress.isEmpty()) {
            email_address.setError(getString(R.string.enter_email));
            email_address.requestFocus();
            return;
        }

        if (_password.isEmpty()) {
            password.setError(getString(R.string.enter_password));
            password.requestFocus();
            return;
        }

        if (userLocation.isEmpty()) {
            userLocationView.setError(getString(R.string.enter_location));
            userLocationView.requestFocus();
            return;
        }
        new CreateAccountTask().execute(Constants.REGISTER_URL);
    }


    /**
     * Recover Password Asyn Task
     */
    private class CreateAccountTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return POST_INFO(urls[0]);
            } catch (JSONException ex) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgres();
        }

        protected void onPostExecute(String result) {
            hideProgress();
            Log.e("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject json = jsonObject.getJSONObject(Constants.USER_JSON_RESPONSE);
                String message =jsonObject.getString(Constants.MESSAGE);
                String status = jsonObject.getString(Constants.STATUS);
                if (TextUtils.equals(status, "200")) {
                    if (TextUtils.equals(json.getString(Constants.APPROVAL_STATUS), Constants.PENDING)) {
                        String msg = getString(R.string.account_not_approved);
                        showDialog(msg);
                    }
                    else if (TextUtils.equals(json.getString(Constants.APPROVAL_STATUS), Constants.BLOCKED)) {
                        String msg = getString(R.string.account_blocked);
                        showDialog(msg);
                    }
                    else if (TextUtils.equals(json.getString(Constants.APPROVAL_STATUS), Constants.ACTIVE)) {
                        User customer = new User();
                        customer.setJsonObject(json);
                        db.createOrUpdateUserRecord(customer);
                        SharedPreferences sp = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Constants.USERNAME, customer.getUser_name());
                        editor.apply();
                        Toast.makeText(getApplicationContext(), getString(R.string.welcome_back) + Constants.SPACE + customer.getUser_name(), Toast.LENGTH_SHORT).show();
                        if (db.confirmLoginStatus()) {
                            startActivity(new Intent(CreateAccount.this, GameHomeActivity.class));
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), getString(R.string.welcome) + " " + customer.getUser_name(), Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (JSONException ex) {
                Log.e("JSONEXCEPTION", ex.getMessage());
            }
        }
    }

    /**
     * Post user data to the server
     */
    public String POST_INFO(String url) throws JSONException {
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url + getIntent().getStringExtra(Constants.USERS_ID));
            params.put(Constants.USER_NAME, fullName);
            params.put(Constants.USER_EMAIL, emailAddress);
            params.put(Constants.USER_PASSWORD, _password);
            params.put(Constants.PHONE, getIntent().getStringExtra(Constants.PHONE));
            params.put(Constants.USER_ADDRESS, userLocation);
            json = params.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
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
     *
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
    public void onBackPressed() {
        finish();
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setMessage(message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                startActivity(new Intent(CreateAccount.this, LoginActivity.class));
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}