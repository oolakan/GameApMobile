package com.example.mygame.mygame.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class ForgotPassword extends AppCompatActivity {
    private Button reset_password, sign_in;
    private EditText newPasswordView, confirmPasswordView;
    private ProgressDialog pDialog;
    private final String CUSTOMER_ID = "customer_id";
    private final String MESSAGE = "message";
    private TextView password_reset_view;
    private String newPassword, confirmPassword;
    private ImageButton close;
    private boolean cancel = false;
    private DBController db;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        db = new DBController(this);
        setViews();
        setButtonClicks();
    }

    private void setButtonClicks() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPassword = newPasswordView.getText().toString().trim();
                confirmPassword = confirmPasswordView.getText().toString().trim();
                validatePassword();
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setViews() {
        reset_password = (Button) findViewById(R.id.reset_password);
        sign_in = (Button) findViewById(R.id.sign_in);
        newPasswordView = (EditText) findViewById(R.id.new_password);
        confirmPasswordView = (EditText) findViewById(R.id.password_confirm);
        password_reset_view = (TextView) findViewById(R.id.password_reset_response);
        layout = (LinearLayout) findViewById(R.id.layout);
    }

    public void hideLayout() {
        layout.setVisibility(View.GONE);
    }

    private void validatePassword() {
        cancel = false;
        if (newPassword.isEmpty()) {
            newPasswordView.requestFocus();
            newPasswordView.setError(getString(R.string.enter_password));
            cancel = true;
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordView.requestFocus();
            confirmPasswordView.setError(getString(R.string.reenter_password));
            cancel = true;
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordView.requestFocus();
            confirmPasswordView.setError(getString(R.string.password_does_not_match));
            cancel = true;
            return;
        }
        if (!cancel) {
            if (isConnected()) {
                new ResetPasswordTask().execute(Constants.PASSWORD_RESET_URL);
            } else {
                Snackbar.make(newPasswordView, getString(R.string.internet_connection), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Recover Password Asyn Task
     */
    private class ResetPasswordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return POST_PASSWORD(urls[0]);
            } catch (JSONException ex) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPassword.this);
            pDialog.setMessage(Html.fromHtml("<b>Wait...</b><br/>Password reset in progress..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.getWindow().setBackgroundDrawableResource(R.color.textBoxTextColor);
            pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            pDialog.show();
        }

        protected void onPostExecute(String result) {
            pDialog.cancel();
            Log.e("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(MESSAGE);
                JSONObject json = jsonObject.getJSONObject(Constants.USER_JSON_RESPONSE);
                User customer = new User();
                customer.setJsonObject(json);
                db.createOrUpdateUserRecord(customer);
                if (db.confirmLoginStatus()) {
                    startActivity(new Intent(ForgotPassword.this, GameHomeActivity.class));
                    finish();
                }
                int status = jsonObject.getInt(Constants.STATUS);
                if (status == 200) {
                    hideLayout();
                    password_reset_view.setVisibility(View.VISIBLE);
                    password_reset_view.setText(message + getString(R.string.password_redirect));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ForgotPassword.this, GameHomeActivity.class));
                            finish();
                        }
                    }, 5000);

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.retry), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException ex) {
                Log.e("JSonException", ex.getMessage());
                Log.d("JSonException", ex.getMessage());
            }
        }
    }


    /**
     * Post user data to the server
     */
    public String POST_PASSWORD(String url) throws JSONException {
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            params.put(Constants.USERS_ID, getIntent().getStringExtra(Constants.USERS_ID));
            params.put(Constants.NEW_PASSWORD, newPassword);
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

    /**
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}