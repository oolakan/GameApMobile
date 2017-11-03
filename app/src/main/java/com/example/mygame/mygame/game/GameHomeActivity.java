package com.example.mygame.mygame.game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.auth.LoginActivity;
import com.example.mygame.mygame.custom.SpinnerCustomList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import model.DBController;
import model.Game;
import model.User;
import utils.Constants;

public class GameHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
private DBController db;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private TextView userName, userEmail;
    private ImageView userImage;
    private Spinner gameNamesView, gameTypesView, gameTypeOptionsView, gameQuatersView;
    private NavigationView navigationView;
    private View headerView;
    SpinnerCustomList gameNameAdapter;
    SpinnerCustomList gameTypeAdapter;
    SpinnerCustomList gameTypeOptionAdapter;
    SpinnerCustomList gameQuaterAdapter;

    ArrayList<Game> _gameQuaters;
    ArrayList<Game> _gameTypeOptions;
    ArrayList<Game> _gameTypes;
    ArrayList<Game> _gameNames;

    String _gameName = "";
    String _gameId = "";

    String _gameTypeId = "", _gameType="";
    String _gameTypeOptionId="", _gameTypeOption="";
    String _gameQuaterId="", _gameQuater="";

    private Button playGameBtn;
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
        setViews();
        setClicks();
        setName();
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

        if (TextUtils.isEmpty(_gameQuaterId)) {
            Toast.makeText(getApplicationContext(), getString(R.string.game_qaters), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isConnected()) {
            new CheckGameAvailability().execute(Constants.GAME_CHECK_URL);
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
        userImage = (ImageView) headerView.findViewById(R.id.userImage);
        gameNamesView = (Spinner) findViewById(R.id.game_name);
        gameTypesView = (Spinner) findViewById(R.id.game_type);
        gameTypeOptionsView = (Spinner) findViewById(R.id.game_type_option);
        gameQuatersView = (Spinner) findViewById(R.id.game_quaters);
        playGameBtn = (Button) findViewById(R.id.start_game);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        gameNamesView.setOnItemSelectedListener(this);
        gameTypesView.setOnItemSelectedListener(this);
        gameTypeOptionsView.setOnItemSelectedListener(this);
        gameQuatersView.setOnItemSelectedListener(this);
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
            // Handle the camera action
        } else if (id == R.id.winnings) {

        } else if (id == R.id.transactions) {

        } else if (id == R.id.logout) {
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
            _gameName = _gameNames.get(i).getName();
        }
        else if (spinner.getId() == R.id.game_type) {
            _gameTypeId = _gameTypes.get(i).getId();
            _gameType = _gameTypes.get(i).getName();
        }
        else if (spinner.getId() == R.id.game_type_option) {
            _gameTypeOptionId = _gameTypeOptions.get(i).getId();
            _gameTypeOption = _gameTypeOptions.get(i).getName();
        }
        else if (spinner.getId() == R.id.game_quaters) {
            _gameQuaterId = _gameQuaters.get(i).getId();
            _gameQuater = _gameQuaters.get(i).getName();
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
                JSONArray gameTypeOptions = jsonObject.getJSONArray(Constants.GAME_TYPE_OPTIONS_JSON_RESPONSE);
                JSONArray gameQuaters = jsonObject.getJSONArray(Constants.GAME_QUATERS_JSON_RESPONSE);
                _gameNames = new ArrayList<>();
                Game iGameName = new Game();
                iGameName.setName(getString(R.string.select_game_name));
                _gameNames.add(iGameName);
                for (int i=0 ;i<gameNames.length(); i++) {
                    Game game = new Game();
                    game.setId(gameNames.getJSONObject(i).getString(Constants.ID));
                    game.setName(gameNames.getJSONObject(i).getString(Constants.NAME));
                    _gameNames.add(game);
                }

                _gameTypes = new ArrayList<>();
                Game iGameTypes = new Game();
                iGameTypes.setName(getString(R.string.select_game_type));
                _gameTypes.add(iGameTypes);
                for (int i=0 ;i<gameNames.length(); i++) {
                    Game game = new Game();
                    game.setId(gameTypes.getJSONObject(i).getString(Constants.ID));
                    game.setName(gameTypes.getJSONObject(i).getString(Constants.NAME));
                    _gameTypes.add(game);
                }

                _gameTypeOptions = new ArrayList<>();
                Game iGameTypeOption = new Game();
                iGameTypeOption.setName(getString(R.string.select_game_type_option));
                _gameTypeOptions.add(iGameTypeOption);
                for (int i=0 ;i<gameTypeOptions.length(); i++) {
                    Game game = new Game();
                    game.setId(gameTypeOptions.getJSONObject(i).getString(Constants.ID));
                    game.setName(gameTypeOptions.getJSONObject(i).getString(Constants.NAME));
                    _gameTypeOptions.add(game);
                }

                _gameQuaters = new ArrayList<>();
                Game iGameQuater = new Game();
                iGameQuater.setName(getString(R.string.select_game_quaters));
                _gameQuaters.add(iGameQuater);
                for (int i=0 ;i<gameQuaters.length(); i++) {
                    Game game = new Game();
                    game.setId(gameQuaters.getJSONObject(i).getString(Constants.ID));
                    game.setName(gameQuaters.getJSONObject(i).getString(Constants.NAME));
                    _gameQuaters.add(game);
                }

                gameNameAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameNames);
                gameNamesView.setAdapter(gameNameAdapter);

                gameTypeAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameTypes);
                gameTypesView.setAdapter(gameTypeAdapter);

                gameTypeOptionAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameTypeOptions);
                gameTypeOptionsView.setAdapter(gameTypeOptionAdapter);

                gameQuaterAdapter = new SpinnerCustomList(GameHomeActivity.this, _gameQuaters);
                gameQuatersView.setAdapter(gameQuaterAdapter);

            }
            catch (JSONException ex){
                Log.e("Excepion", ex.getMessage());
            }
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

    public String POST(String url) throws JSONException{
        InputStream inputStream = null;
        String result = "";
        JSONObject params;
        params = new JSONObject();
        String json = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            params.put(Constants.GAME_NAMES_ID, _gameId);
            params.put(Constants.GAME_QUATERS_ID, _gameQuaterId);
            json = params.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
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
     * Online Status AsyncTask
     */
    private class CheckGameAvailability extends AsyncTask<String, Void, String> {
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
            showBar();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideBar();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString(Constants.MESSAGE);
                String status = String.format("%s",jsonObject.getInt(Constants.STATUS));
                if (status.equals("202")) {
                    showDialog(message);
                }
                else if (status.equals("200")) {
                    Intent intent = new Intent(GameHomeActivity.this, SelectedGameActivity.class);
                    intent.putExtra(Constants.GAME_NAME, _gameName);
                    intent.putExtra(Constants.GAME_TYPE, _gameType);
                    intent.putExtra(Constants.GAME_TYPE_OPTION, _gameTypeOption);
                    intent.putExtra(Constants.GAME_QUATERS, _gameQuater);

                    intent.putExtra(Constants.GAME_NAMES_ID, _gameId);
                    intent.putExtra(Constants.GAME_TYPES_ID, _gameTypeId);
                    intent.putExtra(Constants.GAME_TYPE_OPTIONS_ID, _gameTypeOptionId);
                    intent.putExtra(Constants.GAME_QUATERS_ID, _gameQuaterId);

                    startActivity(intent);
                }
                else {
                    showDialog(message);
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
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
}