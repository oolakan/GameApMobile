package com.example.mygame.mygame;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.auth.ForgotPassword;
import com.example.mygame.mygame.auth.LoginActivity;
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

public class SettingsActivity extends AppCompatActivity {

    private DBController dbController;
    private TextView nameTxt, emailTxt, phoneTxt;
    private EditText newPassword, confirmPassword;
    private Button changePasswordBtn;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.profile));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dbController = new DBController(this);
        getViews();
        setValues();
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePassword();
            }
        });
    }

    public void getViews(){
        nameTxt = findViewById(R.id.name);
        emailTxt = findViewById(R.id.email);
        phoneTxt = findViewById(R.id.phone_no);

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.password_confirm);

        changePasswordBtn = findViewById(R.id.change_password);
        progressBar = findViewById(R.id.progressbar);
    }

    public void setValues() {
        nameTxt.setText(dbController.getUser().get(Constants.USER_NAME));
        emailTxt.setText(dbController.getUser().get(Constants.USER_EMAIL));
        phoneTxt.setText(dbController.getUser().get(Constants.PHONE));

    }

    private void validatePassword() {
        boolean cancel = false;
        if (newPassword.getText().toString().trim().isEmpty()) {
            newPassword.requestFocus();
            newPassword.setError(getString(R.string.enter_password));
            cancel = true;
            return;
        }
        if (confirmPassword.getText().toString().trim().isEmpty()) {
            confirmPassword.requestFocus();
            confirmPassword.setError(getString(R.string.reenter_password));
            cancel = true;
            return;
        }
        if (!TextUtils.equals(newPassword.getText().toString().trim(), confirmPassword.getText().toString().trim())) {
            confirmPassword.requestFocus();
            confirmPassword.setError(getString(R.string.password_does_not_match));
            cancel = true;
            return;
        }
        if (!cancel) {
            if (isConnected()) {
                new ResetPasswordTask().execute(Constants.PASSWORD_RESET_URL);
            } else {
                Snackbar.make(newPassword, getString(R.string.internet_connection), Snackbar.LENGTH_SHORT).show();
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
            showProgres();
        }

        protected void onPostExecute(String result) {
            hideProgress();
            Log.e("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(Constants.MESSAGE);
                JSONObject json = jsonObject.getJSONObject(Constants.USER_JSON_RESPONSE);
                int status = jsonObject.getInt(Constants.STATUS);
                if (status == 200) {
                    User customer = new User();
                    customer.setJsonObject(json);
                    showDialog(message);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.retry), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException ex) {
                Log.e("JSonException", ex.getMessage());
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
            params.put(Constants.USERS_ID, dbController.getUser().get(Constants.USERS_ID));
            params.put(Constants.NEW_PASSWORD, newPassword.getText().toString().trim());
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
    //show progress
    public void showProgres(){
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage(message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbController.deleteUser();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dbController.deleteUser();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
