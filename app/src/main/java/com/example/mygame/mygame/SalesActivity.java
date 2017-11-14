package com.example.mygame.mygame;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mygame.mygame.custom.GameTransactionsCustomList;
import com.example.mygame.mygame.custom.SpinnerCustomList;
import com.example.mygame.mygame.game.GameHomeActivity;
import com.example.mygame.mygame.game.GameSummaryActivity;

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
import java.util.Calendar;

import model.DBController;
import model.Game;
import model.Transaction;
import utils.Constants;

public class SalesActivity extends AppCompatActivity {

    private ListView listView;
    private EditText gameSearchBox;
    private ProgressBar progressBar;
    private DBController dbController;
    private String usersId;

    private ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    private ArrayList<Transaction> searchResult = new ArrayList<>();
    private GameTransactionsCustomList adapter;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dateFromString, dateToString;
    private EditText gameFromDate, gameToDate;
    private Button viewGame;

    private TextView totalAmountView, errorView;

    private LinearLayout totalAmountLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.sales));
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getViews();
        dbController = new DBController(this);
        usersId = dbController.getUser().get(Constants.USERS_ID);

        gameFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SalesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateFromString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                gameFromDate.setText(dateFromString);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        gameToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SalesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateToString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                gameToDate.setText(dateToString);
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        viewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSalesHistory();
            }
        });

    }

    private void getSalesHistory() {
        if (isConnected()) {
            new SalesReport().execute(Constants.SALES_REPORT_URL);
        }
    }

    private void getViews() {
        listView = findViewById(R.id.sales);
        progressBar = findViewById(R.id.progressbar);
        gameFromDate = findViewById(R.id.from_date);
        gameToDate = findViewById(R.id.to_date);
        viewGame = findViewById(R.id.view_game);
        totalAmountView = findViewById(R.id.total_sale);
        errorView = findViewById(R.id.errorView);
        totalAmountLayout = findViewById(R.id.layout);
    }


    /**
     * Online Status AsyncTask
     */
    private class SalesReport extends AsyncTask<String, Void, String> {
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray transactionLists = jsonObject.getJSONArray(Constants.GAME_TRANSACTIONS);
                String totalAmount = jsonObject.getString(Constants.TOTAL_AMOUNT);
                totalAmountView.setText(String.format("N%s",totalAmount));
                if (transactionLists.length() > 0) {
                    totalAmountLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < transactionLists.length(); i++) {
                        Transaction transaction = new Transaction();
                        transaction.setTicketId(transactionLists.getJSONObject(i).getString(Constants.TICKET_ID));
                        transaction.setUnitStake(transactionLists.getJSONObject(i).getString(Constants.UNIT_STAKE));
                        transaction.setTotalAmount(transactionLists.getJSONObject(i).getString(Constants.TOTAL_AMOUNT));
                        transaction.setGame_no_played(transactionLists.getJSONObject(i).getString(Constants.GAME_NO_PLAYED));
                        transaction.setGame_names_id(transactionLists.getJSONObject(i).getString(Constants.GAME_NAMES_ID));
                        transaction.setGame_types_id(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPES_ID));
                        transaction.setGame_type_options_id(transactionLists.getJSONObject(i).getString(Constants.GAME_TYPE_OPTIONS_ID));
                        transaction.setDate_played(transactionLists.getJSONObject(i).getString(Constants.DATE_PLAYED));
                        transaction.setTime_played(transactionLists.getJSONObject(i).getString(Constants.TIME_PLAYED));
                        transaction.setSerialNo(transactionLists.getJSONObject(i).getString(Constants.SERIAL_NO));
                        transaction.setGameName(transactionLists.getJSONObject(i).getJSONObject(Constants.GAME_NAME).getString(Constants.NAME));
                        transaction.setGameType(transactionLists.getJSONObject(i).getJSONObject(Constants.GAME_TYPE).getString(Constants.NAME));
                        transaction.setGameTypeOption(transactionLists.getJSONObject(i).getJSONObject(Constants.GAME_TYPE_OPTION).getString(Constants.NAME));
                        transaction.setPayment_option(transactionLists.getJSONObject(i).getString(Constants.PAYMENT_OPTION));
                        transaction.setStatus(transactionLists.getJSONObject(i).getString(Constants.STATUS));
                        transactionArrayList.add(transaction);
                    }
                }
                else {
                    errorView.setVisibility(View.VISIBLE);
                    totalAmountLayout.setVisibility(View.GONE);
                }
                Log.e("TransactionList", transactionArrayList.toString());
                adapter = new GameTransactionsCustomList(SalesActivity.this, transactionArrayList);
                listView.setAdapter(adapter);
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
            HttpGet httpGet = new HttpGet(url + usersId + "/" +dateFromString + "/" + dateToString);
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
        totalAmountLayout.setVisibility(View.GONE);
    }

    private void hideBar (){
        progressBar.setVisibility(View.GONE);
    }


}
