package com.example.mygame.mygame.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mygame.mygame.MainActivity;
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

import model.DBController;
import model.User;
import utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private AutoCompleteTextView mPhoneView;
    private View mProgressView;
    private View mLoginFormView;
    private Button forgot_password_button;
    private Button create_account_button;
    private final String MESSAGE = "message";
    private String CUSTOMER_ID = "customer_id";
    private String PASSWORD = "password";

    private Button sign_in_button, signupButton;
    private String emailId;
    private String password;
    private DBController db;
    private ProgressDialog pDialog;

    private ProgressBar progressBar;
    private Button fb;
    private String name, email, img, user_social_id, phoneNo="";

    private double latitude, longitude;
    private ImageButton close;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBController(this);
        setViews();
        getForgot_password_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneRegistration.class);
                intent.putExtra(Constants.ACTIVITY_TYPE, Constants.PASSWORD_RESET_ACTIVITY_TYPE);
                startActivity(intent);
                finish();
            }
        });

        getSign_in_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId = mEmailView.getText().toString().trim();
                password = mPasswordView.getText().toString().trim();
                validate();
            }

        });
        getSignupButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneRegistration.class);
                intent.putExtra(Constants.ACTIVITY_TYPE, Constants.REGISTRATION_ACTIVITY_TYPE);
                startActivity(intent);
                finish();
            }
        });
    }

    private void validate() {
        if (TextUtils.equals(emailId, "")) {
            mEmailView.setError(getString(R.string.enter_user_id));
            mEmailView.requestFocus();
        }
        if (TextUtils.equals(password, "")) {
            mPasswordView.setError(getString(R.string.password));
            mPasswordView.requestFocus();

        }
        if (!isEmailValid(mEmailView.getText().toString())) {
            mEmailView.setError(getString(R.string.invalid_userId));
            mEmailView.requestFocus();
        }
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
        }
        if (!TextUtils.equals(emailId, "") && !TextUtils.equals(password, "")) {
            if (isConnected()) {
                new LoginTask().execute(Constants.SIGN_IN_URL);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setViews() {
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        signupButton = (Button) findViewById(R.id.sign_up);
        forgot_password_button = (Button) findViewById(R.id.forgot_password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    public Button getSign_in_button() {
        return sign_in_button;
    }

    public Button getSignupButton() {
        return signupButton;
    }

    public Button getForgot_password_button() {
        return forgot_password_button;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPhoneNoValid(String phone) {
        return Character.isDigit(phone.charAt(0));
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Online Status AsyncTask
     */
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return POST(urls[0]);
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
                String message =jsonObject.getString(MESSAGE);
                String status = jsonObject.getString(Constants.STATUS);
                if (TextUtils.equals(status, "200")) {
                    JSONObject json = jsonObject.getJSONObject(Constants.USER_JSON_RESPONSE);
                    User customer = new User();
                    customer.setJsonObject(json);
                    db.createOrUpdateUserRecord(customer);
                    SharedPreferences sp = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Constants.USERNAME, customer.getUser_name());
                    editor.apply();
                    Toast.makeText(getApplicationContext(), getString(R.string.welcome_back)+Constants.SPACE+customer.getUser_name(), Toast.LENGTH_SHORT).show();
                    if (db.confirmLoginStatus()) {
                        startActivity(new Intent(LoginActivity.this, GameHomeActivity.class));
                        finish();
                    }
                }
                else if (TextUtils.equals(status, "203")) {
                    showDialog(message);
                    mEmailView.setText(Constants.EMPTY);
                    mPasswordView.setText(Constants.EMPTY);
                }
                else if (TextUtils.equals(status, "202")) {
                    showDialog(message);
                    mEmailView.setText(Constants.EMPTY);
                    mPasswordView.setText(Constants.EMPTY);
                }
                else if (TextUtils.equals(status, "201")) {
                    showDialog(message);
                }

            }
            catch (JSONException ex){
                Log.e("Excepion", ex.getMessage());
                String message = getString(R.string.poor_internet_connection);
                showDialog(message);
            }
        }
    }
    /**
     * Post user data to the server
     */
    public String POST(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            params.put(Constants.USER_EMAIL, emailId);
            params.put(Constants.PASSWORD, password);
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
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

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

    @Override
    public void onBackPressed(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //show progress
    public void showProgres(){
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }

}
