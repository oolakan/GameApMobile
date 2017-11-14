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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mygame.mygame.custom.GameTransactionsCustomList;
import com.example.mygame.mygame.game.GameHomeActivity;

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
import model.Transaction;
import utils.Constants;

public class WinningGameActivity extends AppCompatActivity {
    private ListView listView;
    private EditText gameSearchBox;
    private EditText gameFromDate, gameToDate;
    private ProgressBar progressBar;
    private DBController dbController;
    private String usersId;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dateFromString, dateToString;

    private Button viewGame;
    private TextView errorView;

    private ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    private ArrayList<Transaction> searchResult = new ArrayList<>();
    private GameTransactionsCustomList adapter;
    private CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setTitle(getString(R.string.wiining_games));
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

        viewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWinningGame();
            }
        });
        gameSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    for (int index = 0; index < transactionArrayList.size(); index++) {
                        if (TextUtils.equals(transactionArrayList.get(index).getSerialNo(), gameSearchBox.getText().toString().trim())) {
                            Transaction transaction = new Transaction();
                            transaction.setTicketId(transactionArrayList.get(index).getTicketId());
                            transaction.setUnitStake(transactionArrayList.get(index).getUnitStake());
                            transaction.setTotalAmount(transactionArrayList.get(index).getTotalAmount());
                            transaction.setGame_no_played(transactionArrayList.get(index).getGame_no_played());
                            transaction.setTime_played(transactionArrayList.get(index).getTime_played());
                            transaction.setSerialNo(transactionArrayList.get(index).getSerialNo());
                            transaction.setPayment_option(transactionArrayList.get(index).getPayment_option());
                            searchResult.add(transaction);
                        }
                    }
                    Log.e("TransactionList", searchResult.toString());
                    adapter = new GameTransactionsCustomList(WinningGameActivity.this, searchResult);
                    listView.setAdapter(adapter);
                }
                else {
                    adapter = new GameTransactionsCustomList(WinningGameActivity.this, transactionArrayList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        gameFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(WinningGameActivity.this,
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(WinningGameActivity.this,
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
    }

    private void getWinningGame() {
        if (isConnected()) {
            new WinningGames().execute(Constants.WINNING_GAMES_URL);
        }
        else {
            String message = getString(R.string.no_internet_connection);
            showDialog(message);
        }
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WinningGameActivity.this);
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
    private void getViews() {
        listView = findViewById(R.id.sales);
        gameSearchBox = findViewById(R.id.gameSearch);
        progressBar = findViewById(R.id.progressbar);
        cardView = findViewById(R.id.card_view);
        gameFromDate = findViewById(R.id.from_date);
        gameToDate = findViewById(R.id.to_date);
        viewGame = (Button) findViewById(R.id.view_game);
        errorView = (TextView) findViewById(R.id.errorView);
    }


    /**
     * Online Status AsyncTask
     */
    private class WinningGames extends AsyncTask<String, Void, String> {
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
            transactionArrayList.clear();
            Log.e("Result", result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray transactionLists = jsonObject.getJSONArray(Constants.GAME_TRANSACTIONS);
                if (transactionLists.length() > 0) {
                    cardView.setVisibility(View.VISIBLE);
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
                    Log.e("TransactionList", transactionArrayList.toString());
                    adapter = new GameTransactionsCustomList(WinningGameActivity.this, transactionArrayList);
                    listView.setAdapter(adapter);
                }
                else {
                    cardView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
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
    public String GET(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + usersId + "/" + dateFromString + "/" + dateToString + "/1");
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


}
