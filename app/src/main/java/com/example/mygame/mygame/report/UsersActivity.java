package com.example.mygame.mygame.report;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.custom.UsersCustomList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.mygame.mygame.model.DBController;
import com.example.mygame.mygame.model.User;
import com.example.mygame.mygame.utils.Constants;

public class UsersActivity extends AppCompatActivity implements UsersCustomList.customButtonListener {
    private ArrayList<User> userArrayList = new ArrayList<>();
    private DBController dbController;
    private UsersCustomList adapter;
    private ListView agentsView;
    private ProgressBar progressBar;
    private TextView errorView;
    private String creditAmount;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.agents));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dbController = new DBController(this);
        setViews();
        getAgents();
    }
    private void setViews() {
        progressBar = findViewById(R.id.progressbar);
        agentsView = findViewById(R.id.agents);
        errorView = findViewById(R.id.errorView);
    }

    private void getAgents() {
        if (isConnected()) {
            new GetAgents().execute(Constants.AGENTS_URL);
        }
        else{
            Snackbar.make(errorView, getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onButtonClickListner(int position, final String userId, String title, UsersCustomList.ViewHolder holder) {
        if (TextUtils.equals(title, Constants.BLOCKED)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
            builder.setMessage(getString(R.string.block_question_message))
                    .setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    new UpdateAgentStatus().execute(Constants.UPDATE_AGENT_URL + Constants.BLOCKED + "/" + dbController.getUser().get(Constants.USERS_ID));
                }
            });
            builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (TextUtils.equals(title, Constants.APPROVED)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
            builder.setMessage(getString(R.string.unblock_question_message))
                    .setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    new UpdateAgentStatus().execute(Constants.UPDATE_AGENT_URL + Constants.APPROVED + "/" + dbController.getUser().get(Constants.USERS_ID));
                }
            });
            builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (TextUtils.equals(title, Constants.CREDIT_BALANCE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
            builder.setMessage(getString(R.string.credit_question_message))
                    .setTitle(R.string.dialog_title);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    dialogInterface.cancel();

                    dialog = new Dialog(UsersActivity.this);
                    dialog.setTitle(getString(R.string.ADD_CREDIT));
                    dialog.setContentView(R.layout.credit_balance);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    final EditText creditBalance = dialog.findViewById(R.id.credit_amount);
                    Button addCreditBtn = dialog.findViewById(R.id.addCredit);
                    addCreditBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            creditAmount = creditBalance.getText().toString().trim();
                            if (isConnected()) {
                                dialog.cancel();
                                new UpdateCreditBalance().execute(Constants.AACOUNT_BALANCE_UPDATE_URL + userId + "/" + dbController.getUser().get(Constants.USERS_ID) + "/" + creditAmount);
                            }
                            else {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }
            });
            builder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Online Status AsyncTask
     */
    private class GetAgents extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return GET(urls[0]);
            } catch (JSONException ex) {
                Log.e("JSONSException",ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBar();
        }
        protected void onPostExecute(String result) {
            userArrayList.clear();
            Log.e("Result", result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString(Constants.STATUS);
                if (TextUtils.equals(status, "200")) {
                    JSONArray userLists = jsonObject.getJSONArray(Constants.AGENTS_JSON_RESPONSE);
                    getUserInformation(userLists);
                }
                else {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(getString(R.string.error_occured));
                }
            }
            catch (JSONException ex){
                Log.e("JSONExcepion1", ex.getMessage());
                errorView.setVisibility(View.VISIBLE);
                errorView.setText(getString(R.string.error_occured));
            }
        }
    }

    private void getUserInformation(JSONArray userLists) throws JSONException {
        if (userLists.length() > 0) {
            for (int i = 0; i < userLists.length(); i++) {
                User user = new User();
                JSONObject jsonObject1 = userLists.getJSONObject(i);
                user.setJsonObject(jsonObject1);
                userArrayList.add(user);
            }
            Log.e("TransactionList", userLists.toString());
            adapter = new UsersCustomList(UsersActivity.this, userArrayList);
            adapter.setCustomButtonListner(UsersActivity.this);
            agentsView.setAdapter(adapter);
        } else {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Post user data to the server
     */
    public String GET (String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + dbController.getUser().get(Constants.USERS_ID));
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpGet);
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
     * Online Status AsyncTask
     */
    private class UpdateAgentStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return GETSTATUS(urls[0]);
            } catch (JSONException ex) {
                Log.e("JSONSException",ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBar();
        }
        protected void onPostExecute(String result) {
            Log.e("Result", result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(Constants.MESSAGE);
                showDialog(message);
            }
            catch (JSONException ex){
                Log.e("JSONExcepion1", ex.getMessage());
            }
        }
    }
    /**
     * Post user data to the server
     */
    public String GETSTATUS (String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpGet);
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
     * Online Status AsyncTask
     */
    private class UpdateCreditBalance extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return GETAMOUNT(urls[0]);
            } catch (JSONException ex) {
                Log.e("JSONSException",ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBar();
        }
        protected void onPostExecute(String result) {
            Log.e("Result", result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(Constants.MESSAGE);
                String status = jsonObject.getString(Constants.STATUS);
                showDialog(message);
                if (TextUtils.equals(status, "200")) {
                    userArrayList.clear();
                    JSONArray userLists = jsonObject.getJSONArray(Constants.AGENTS_JSON_RESPONSE);
                    getUserInformation(userLists);
                }
            }
            catch (JSONException ex){
                Log.e("JSONExcepion1", ex.getMessage());
            }
        }
    }
    /**
     * Post user data to the server
     */
    public String GETAMOUNT (String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpGet);
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

    private void showBar() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideBar (){
        progressBar.setVisibility(View.GONE);
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
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

}
