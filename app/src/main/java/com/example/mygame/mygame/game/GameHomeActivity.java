package com.example.mygame.mygame.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.report.SalesActivity;
import com.example.mygame.mygame.auth.SettingsActivity;
import com.example.mygame.mygame.report.TransactionsActivity;
import com.example.mygame.mygame.report.UsersActivity;
import com.example.mygame.mygame.report.WinningGameActivity;
import com.example.mygame.mygame.auth.LoginActivity;
import com.example.mygame.mygame.custom.SpinnerCustomList;

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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import com.example.mygame.mygame.model.DBController;
import com.example.mygame.mygame.model.Game;
import com.example.mygame.mygame.utils.Constants;

public class GameHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
private DBController db;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private TextView userName, userEmail, customer_name, creditBalanceView;
    private Spinner gameNamesView, gameTypesView, gameTypeOptionsView;
    private NavigationView navigationView;
    private View headerView;
    SpinnerCustomList gameNameAdapter;
    SpinnerCustomList gameTypeAdapter;
    SpinnerCustomList gameTypeOptionAdapter;

    private TextClock currentTime;

    ArrayList<Game> _gameTypeOptions;
    ArrayList<Game> _gameTypes;
    ArrayList<Game> _gameNames;

    String _gameName = "";
    String _gameId = "";

    String _gameTypeId = "", _gameType="";
    String _gameTypeOptionId="", _gameTypeOption="";
    String _gameQuaterId="", _gameQuaterName;

    private String startTime, stopTime, drawTime;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String current_timestamp;
    final Calendar c = Calendar.getInstance();

    private Button playGameBtn;
    private String dateTimetoMillis;

    private JSONArray gameTypeOptions;
    private String creditBalance;


    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setBackgroundResource(R.color.colorPrimary);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        db = new DBController(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView =  navigationView.getHeaderView(0);
        customer_name = (TextView) headerView.findViewById(R.id.userName);
        customer_name.setText(db.getUser().get(Constants.USER_NAME));
        creditBalanceView = (TextView) headerView.findViewById(R.id.credit_balance);

        // show agent menu if user is a merchat
        if (TextUtils.equals(db.getUser().get(Constants.ROLES_ID), Constants.AGENTS_ROLE_ID)) {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.agents);
            target.setVisible(false);
        }
        setViews();
        setClicks();
        setName();
        getCreditBalance();

        new GetGamesInfo().execute(Constants.GAMES_INFORMATION_URL);
        getPlayGameBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for game status
                if (isConnected()) {
                    validate();
                }
                else {
                    Snackbar.make(userName, getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getCreditBalance() {
        if(isConnected()) {
            new GetCreditBalance().execute(Constants.CREDIT_BALANCE_URL);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getCreditBalance();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCreditBalance();
    }

    private void validate() {
        if (TextUtils.isEmpty(_gameId)) {
            Toast.makeText(getApplicationContext(), getString(R.string.game_not_found), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(_gameTypeId)) {
            Toast.makeText(getApplicationContext(), getString(R.string.game_type_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(_gameTypeOptionId)) {
            Toast.makeText(getApplicationContext(), getString(R.string.game_type_option_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isConnected()) {
            Intent intent = new Intent(GameHomeActivity.this, SelectedGameActivity.class);
            intent.putExtra(Constants.GAME_NAME, _gameName);
            intent.putExtra(Constants.GAME_TYPE, _gameType);
            intent.putExtra(Constants.GAME_TYPE_OPTION, _gameTypeOption);
            intent.putExtra(Constants.GAME_QUATERS, _gameQuaterName);

            intent.putExtra(Constants.GAME_NAMES_ID, _gameId);
            intent.putExtra(Constants.GAME_TYPES_ID, _gameTypeId);
            intent.putExtra(Constants.GAME_TYPE_OPTIONS_ID, _gameTypeOptionId);
            intent.putExtra(Constants.GAME_QUATERS_ID, _gameQuaterId);

            intent.putExtra(Constants.START_TIME, startTime);
            intent.putExtra(Constants.STOP_TIME, stopTime);
            intent.putExtra(Constants.DRAW_TIME, drawTime);
            startActivity(intent);
        }
        else {
            showDialog(getString(R.string.no_internet_connection));
        }
    }

    public Button getPlayGameBtn() {
        return playGameBtn;
    }

    private void setClicks() {
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(GameHomeActivity.this,ProfileActivity.class));
            }
        });
    }

    private void setName() {
        try {
            if (db.getUser() != null) {
                userName.setText(db.getUser().get(Constants.USER_NAME).toUpperCase());
            }
        }catch (NullPointerException ex){}
    }

    public void setViews(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView =  navigationView.getHeaderView(0);
        userName = (TextView) headerView.findViewById(R.id.userName);
        gameNamesView = (Spinner) findViewById(R.id.game_name);
        gameTypesView = (Spinner) findViewById(R.id.game_type);
        gameTypeOptionsView = (Spinner) findViewById(R.id.game_type_option);

        playGameBtn = (Button) findViewById(R.id.start_game);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        currentTime = findViewById(R.id.currentTime);
        gameNamesView.setOnItemSelectedListener(this);
        gameTypesView.setOnItemSelectedListener(this);
        gameTypeOptionsView.setOnItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            new GetGamesInfo().execute(Constants.GAMES_INFORMATION_URL);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
           startActivity(getIntent());
        } else if (id == R.id.winnings) {
            startActivity(new Intent(GameHomeActivity.this, WinningGameActivity.class));
        } else if (id == R.id.transactions) {
            startActivity(new Intent(GameHomeActivity.this, TransactionsActivity.class));
        }
        else if (id == R.id.sales) {
            startActivity(new Intent(GameHomeActivity.this, SalesActivity.class));
        }
        else if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(GameHomeActivity.this, LoginActivity.class));
            db.deleteUser();
            finish();
        }
        else if (id == R.id.settings) {
            startActivity(new Intent(GameHomeActivity.this, SettingsActivity.class));
        }
        else if (id == R.id.validate) {
            startActivity(new Intent(GameHomeActivity.this, GameValidationActivity.class));
        }
        else if (id == R.id.agents) {
            startActivity(new Intent(GameHomeActivity.this, UsersActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.game_name) {
            _gameId = _gameNames.get(i).getId();
            _gameQuaterId = _gameNames.get(i).getGameQuaterId();
            _gameQuaterName = _gameNames.get(i).getGameQuaterName();
            _gameName = _gameNames.get(i).getName();
            startTime = _gameNames.get(i).getStartTime();
            stopTime = _gameNames.get(i).getStopTIme();
            drawTime = _gameNames.get(i).getDrawTime();
        }
        else if (spinner.getId() == R.id.game_type) {
            _gameTypeId = _gameTypes.get(i).getId();
            _gameType = _gameTypes.get(i).getName();

            // populate game type options based on users selection
            try {
                _gameTypeOptions = new ArrayList<>();
                Game iGameTypeOption = new Game();
                iGameTypeOption.setName(getString(R.string.select_game_type_option).toUpperCase());
                iGameTypeOption.setGameDay(Constants.EMPTY);
                _gameTypeOptions.add(iGameTypeOption);
                int gameTypeOptionCount = gameTypeOptions.length();
                for (int index = 0; index < gameTypeOptionCount; index++) {
                    Game game = new Game();
                    JSONObject jsonObject = gameTypeOptions.getJSONObject(index);
                    if (jsonObject.getString(Constants.NAME).contains(_gameType)) {
                        game.setId(jsonObject.getString(Constants.ID));
                        game.setName(jsonObject.getString(Constants.NAME));
                        game.setGameDay(Constants.EMPTY);
                        _gameTypeOptions.add(game);
                    }
                }
                gameTypeOptionAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameTypeOptions);
                gameTypeOptionsView.setAdapter(gameTypeOptionAdapter);
            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        else if (spinner.getId() == R.id.game_type_option) {
            _gameTypeOptionId = _gameTypeOptions.get(i).getId();
            _gameTypeOption = _gameTypeOptions.get(i).getName();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * Online Status AsyncTask
     */
    private class GetGamesInfo extends AsyncTask<String, Void, String> {
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
                JSONArray gameNames = jsonObject.getJSONArray(Constants.GAME_NAMES_JSON_RESPONSE);
                JSONArray gameTypes = jsonObject.getJSONArray(Constants.GAME_TYPES_JSON_RESPONSE);
                gameTypeOptions = jsonObject.getJSONArray(Constants.GAME_TYPE_OPTIONS_JSON_RESPONSE);
                int gameNamesCount = gameNames.length();
                _gameNames = new ArrayList<>();
                Game iGameName = new Game();
                iGameName.setName(getString(R.string.select_game_name).toUpperCase());
                iGameName.setGameDay(Constants.EMPTY);
                _gameNames.add(iGameName);
                for (int i=0 ;i<gameNamesCount; i++) {
                    Game game = new Game();
                    JSONObject jsonObject1 = gameNames.getJSONObject(i);
                    game.setId(jsonObject1.getString(Constants.ID));
                    game.setGameQuaterId(jsonObject1.getJSONObject(Constants.GAME_QUATERS).getString(Constants.ID));
                    game.setGameQuaterName(jsonObject1.getJSONObject(Constants.GAME_QUATERS).getString(Constants.NAME));
                    game.setName(jsonObject1.getString(Constants.NAME));
                    game.setStartTime(jsonObject1.getString(Constants.START_TIME));
                    game.setStopTIme(jsonObject1.getString(Constants.STOP_TIME));
                    game.setDrawTime(jsonObject1.getString(Constants.DRAW_TIME));
                    game.setGameDay(jsonObject1.getJSONObject(Constants.DAY).getString(Constants.NAME));
                    _gameNames.add(game);
                }

                _gameTypes = new ArrayList<>();
                Game iGameTypes = new Game();
                iGameTypes.setName(getString(R.string.select_game_type).toUpperCase());
                iGameTypes.setGameDay(Constants.EMPTY);
                _gameTypes.add(iGameTypes);
                int gameTypesCount = gameTypes.length();
                for (int i=0 ;i<gameTypesCount; i++) {
                    Game game = new Game();
                    JSONObject jsonObject1 = gameTypes.getJSONObject(i);
                    game.setId(jsonObject1.getString(Constants.ID));
                    game.setName(jsonObject1.getString(Constants.NAME));
                    game.setGameDay(Constants.EMPTY);
                    _gameTypes.add(game);
                }

                gameNameAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameNames);
                gameNamesView.setAdapter(gameNameAdapter);

                gameTypeAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameTypes);
                gameTypesView.setAdapter(gameTypeAdapter);

            }
            catch (JSONException ex){
                Log.e("JSONExcepion1", ex.getMessage());
                String message = getString(R.string.poor_internet_connection);
            }
            catch (Exception ex) {}
        }
    }
    /**
     * Post user data to the server
     */
    public String GET(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
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
     * Post user data to the server
     */
    public String GETCREDIT(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + db.getUser().get(Constants.USERS_ID));
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

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameHomeActivity.this);
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

    private void showBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideBar (){
        progressBar.setVisibility(View.GONE);
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm a");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }

//    private boolean isGameValid() {
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//        // get game stop time from server
//        String[] splitTime = stopTime.split(":");
//        mHour = Integer.parseInt(splitTime[0]);
//        mMinute = Integer.parseInt(splitTime[1]);
//
//        Long current_timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
//        Long dateTimeToMillis = Long.parseLong(Integer.toString(componentTimeToTimestamp(mYear, mMonth, mDay, mHour, mMinute)));
//
//        if (current_timestamp < dateTimeToMillis) {
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
    //convertdatetime to timestamp
    public int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (int) (c.getTimeInMillis() / 1000L);
    }


    /**
     * Online Status AsyncTask
     */
    private class GetCreditBalance extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                return GETCREDIT(urls[0]);
            } catch (JSONException ex) {
                Log.e("JSONSException",ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected void onPostExecute(String result) {
            Log.e("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                int status = jsonObject.getInt(Constants.STATUS);
                if (status == 200) {
                    JSONObject balanceObject = jsonObject.getJSONObject(Constants.CREDIT_JSON_RESPONSE);
                    if (balanceObject != null) {
                        creditBalance = balanceObject.getString(Constants.AMOUNT);
                        creditBalanceView.setText(String.format("Credit Balance: N%s.00", creditBalance));
                    }
                    else{
                        creditBalanceView.setText(String.format("Credit Balance: N0.00", creditBalance));
                    }
                }
                else {
                    creditBalanceView.setText(String.format("Credit Balance: N0.00", creditBalance));
                }
            }
            catch (JSONException ex){
                Log.e("JSONExcepion", ex.getMessage());
                String message = getString(R.string.poor_internet_connection);
            }
            catch (Exception ex) {}
        }
    }
}