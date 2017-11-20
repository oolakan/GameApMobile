package com.example.mygame.mygame.game;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.custom.GameTransactionsCustomList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.mygame.mygame.model.Transaction;
import com.example.mygame.mygame.utils.Constants;

public class GameValidationActivity extends AppCompatActivity {

    private Button validateGameBtn;
    private EditText serialNoTxt;
    private ListView gameView;
    private ProgressBar progressBar;
    private String serialNumber;
    private ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    private GameTransactionsCustomList adapter;
    private TextView errorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_validation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.validate_game));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setView();
        getValidateGameBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void setView() {
        serialNoTxt = findViewById(R.id.serial_no);
        validateGameBtn = findViewById(R.id.validate_game);
        gameView = findViewById(R.id.game);
        progressBar = findViewById(R.id.progressbar);
        errorView = findViewById(R.id.errorView);
    }

    private void validate(){
        serialNumber = serialNoTxt.getText().toString().trim();
        if (TextUtils.equals(serialNumber, Constants.EMPTY)) {
            serialNoTxt.setError(getString(R.string.enter_serial_no));
            return;
        }
        if (isConnected()) {
            new ValidateGame().execute(Constants.VALIDATE_GAME_URL);
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public Button getValidateGameBtn() {
        return validateGameBtn;
    }

    /**
     * Online Status AsyncTask
     */
    private class ValidateGame extends AsyncTask<String, Void, String> {
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
            Log.e("Result", result);
            hideBar();
            transactionArrayList.clear();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String staus = jsonObject.getString(Constants.STATUS);
                if (TextUtils.equals(staus, "200")) {
                    JSONObject transactionResult = jsonObject.getJSONObject(Constants.GAME_TRANSACTIONS);
                    Transaction transaction = new Transaction();
                    transaction.setTicketId(transactionResult.getString(Constants.TICKET_ID));
                    transaction.setUnitStake(transactionResult.getString(Constants.UNIT_STAKE));
                    transaction.setTotalAmount(transactionResult.getString(Constants.TOTAL_AMOUNT));
                    transaction.setGame_no_played(transactionResult.getString(Constants.GAME_NO_PLAYED));
                    transaction.setGame_names_id(transactionResult.getString(Constants.GAME_NAMES_ID));
                    transaction.setGame_types_id(transactionResult.getString(Constants.GAME_TYPES_ID));
                    transaction.setGame_type_options_id(transactionResult.getString(Constants.GAME_TYPE_OPTIONS_ID));
                    transaction.setDate_played(transactionResult.getString(Constants.DATE_PLAYED));
                    transaction.setTime_played(transactionResult.getString(Constants.TIME_PLAYED));
                    transaction.setSerialNo(transactionResult.getString(Constants.SERIAL_NO));
                    transaction.setGameName(transactionResult.getJSONObject(Constants.GAME_NAME).getString(Constants.NAME));
                    transaction.setGameType(transactionResult.getJSONObject(Constants.GAME_TYPE).getString(Constants.NAME));
                    transaction.setStatus(transactionResult.getString(Constants.STATUS));
                    transaction.setWinningAmount(transactionResult.getString(Constants.WINNING_AMOUNT));
                    transaction.setGameTypeOption(transactionResult.getJSONObject(Constants.GAME_TYPE_OPTION).getString(Constants.NAME));
                    transaction.setPayment_option(transactionResult.getString(Constants.PAYMENT_OPTION));
                    transactionArrayList.add(transaction);
                }
                else {
                    errorView.setVisibility(View.VISIBLE);
                }
                Log.e("TransactionList", transactionArrayList.toString());
                adapter = new GameTransactionsCustomList(GameValidationActivity.this, transactionArrayList);
                gameView.setAdapter(adapter);
            }
            catch (JSONException ex){
                Log.e("JSONExcepion1", ex.getMessage());
            }
        }
    }
    /**
     * Post user data to the server
     */
    public String GET(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + serialNumber);
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
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideBar (){
        progressBar.setVisibility(View.GONE);
    }



}
