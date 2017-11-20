package com.example.mygame.mygame.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.adapter.GridListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.example.mygame.mygame.model.DBController;
import com.example.mygame.mygame.model.Transaction;
import com.example.mygame.mygame.utils.Constants;

import static com.example.mygame.mygame.adapter.GridListAdapter.*;

public class SelectedGameActivity extends AppCompatActivity implements  customButtonListener {
    private Context context;
    private LinearLayout againstLayout;
    private GridListAdapter adapter;
    private ArrayList<String> arrayList;
    GridView gridView;
    private EditText gameNoSelectedTextBox, totalAmount, unitStakes, againstNo;
    private Button playNewGameButton, payForGameButton;
    private String gameTypeOption;
    private String gameType;
    private ImageButton clearBtn;
    private int lines = 0;
    private DBController dbController;

    private String[] gameNumbers;
    private String message;
    private String serialNumberValue = "";
    private SharedPreferences sharedPreferences;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("%s: %s",getIntent().getStringExtra(Constants.GAME_NAME),
                getIntent().getStringExtra(Constants.GAME_TYPE_OPTION)));
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.back, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dbController = new DBController(this);
        loadViews();
        loadGridView();

        dbController = new DBController(this);
        gameTypeOption = getIntent().getStringExtra(Constants.GAME_TYPE_OPTION);
        gameType = getIntent().getStringExtra(Constants.GAME_TYPE);
        getGameType();
        // create game serial no
        sharedPreferences = getSharedPreferences(Constants.GAME_SERIAL_NO, MODE_PRIVATE);
        if (!sharedPreferences.contains(Constants.SERIAL_NO)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            serialNumberValue = generateSerialNo();
            editor.putString(Constants.SERIAL_NO, serialNumberValue);
            editor.apply();
        }
        getPlayNewGameButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePreviousGame();
            }
        });
        getPayForGameButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (TextUtils.equals(gameNoSelectedTextBox.getText().toString().trim(), Constants.EMPTY)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.enter_game_no), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.equals(unitStakes.getText().toString().trim(), Constants.EMPTY)) {
                        unitStakes.setError(getString(R.string.enter_unit_stake));
                        unitStakes.requestFocus();
                        return;
                    }
                    saveGamePlayed();
                    // check if saved
                    if (dbController.getTransactions().length() > 0) {
                        //delete serial number
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.GAME_SERIAL_NO, MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        startActivity(new Intent(SelectedGameActivity.this, GameSummaryActivity.class));
                    }
                }
            catch (JSONException ex){}
            }
        });

        gameNoSelectedTextBox.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = gameNoSelectedTextBox.getRight()
                            - gameNoSelectedTextBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        gameNoSelectedTextBox.setText(Constants.EMPTY);
                        unitStakes.setText(Constants.EMPTY);
                        totalAmount.setText(Constants.EMPTY);
                        loadGridView();
                        return true;
                    }
                }
                return false;
            }
        });

        againstNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.equals(gameTypeOption, getString(R.string.against1))) {
                    againstNo.setFilters(new InputFilter[] {
                            new InputFilter.LengthFilter(3)
                    });
                }
                if (TextUtils.equals(gameTypeOption, getString(R.string.against2))) {
                    gameNumbers = againstNo.getText().toString().trim().split(",");
                    if (gameNumbers.length == 2) {
                        String message = getString(R.string.only_two_against);
                        showDialog(message);
                    }
                }
                if (TextUtils.equals(gameTypeOption, getString(R.string.against4))) {
                    gameNumbers = againstNo.getText().toString().trim().split(",");
                    if (gameNumbers.length == 4) {
                        String message = getString(R.string.only_four_against);
                        showDialog(message);
                    }
                }

                if (TextUtils.equals(gameTypeOption, getString(R.string.against5))) {
                    gameNumbers = againstNo.getText().toString().trim().split(",");
                    if (gameNumbers.length == 5) {
                        String message = getString(R.string.only_five_against);
                        showDialog(message);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        unitStakes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    HashMap<String, String> result;
                    String totalAmountStaked = "";
                    String gameNos[] = gameNoSelectedTextBox.getText().toString().trim().split(",");
                    int unitStake = Integer.parseInt(unitStakes.getText().toString().trim());
                    if (TextUtils.equals(gameType, getString(R.string.PERM))) {
                        result = computePermutation(unitStake, gameNos.length, gameTypeOption);
                        totalAmountStaked = result.get(Constants.TOTAL_AMOUNT_STAKED);
                    }
                    else if (TextUtils.equals(gameType, getString(R.string.DIRECT))){
                        result = computeDirect(unitStake, gameNos.length, gameTypeOption);
                        totalAmountStaked = result.get(Constants.TOTAL_AMOUNT_STAKED);
                    }
                    else if (TextUtils.equals(gameType, getString(R.string.AGAINST))) {
                        String gameNumberSelected = againstNo.getText().toString().trim() + "," + gameNoSelectedTextBox.getText().toString().trim();
                        gameNos = gameNumberSelected.split(",");
                        result = computeAgainst(unitStake, gameNos.length, gameTypeOption);
                        totalAmountStaked = result.get(Constants.TOTAL_AMOUNT_STAKED);
                    }
                    totalAmount.setText(totalAmountStaked);
                }catch (NumberFormatException ex){}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

    private void getGameType() {
        if (TextUtils.equals(gameType, getString(R.string.AGAINST))) {
            againstLayout.setVisibility(View.VISIBLE);
        }
    }

    private void saveGamePlayed() throws JSONException {
        // get current timestamp
        String gameNumberSelected = "";
        String serialNo = sharedPreferences.getString(Constants.SERIAL_NO, Constants.EMPTY);
       // if (TextUtils.equals(serialNumberValue, Constants.EMPTY));
        Long tsLong = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        String datePlayed = new java.text.SimpleDateFormat("yyyy-MM-dd").
                format(new java.util.Date(tsLong * 1000));

        String timePlayed = new java.text.SimpleDateFormat("HH:mm").
                format(new java.util.Date(tsLong * 1000));

        Transaction transaction = new Transaction();

        if (TextUtils.equals(gameType, getString(R.string.AGAINST))) {
            gameNumberSelected = againstNo.getText().toString().trim() + "," + gameNoSelectedTextBox.getText().toString().trim();
        }
        else {
            gameNumberSelected = gameNoSelectedTextBox.getText().toString().trim();
        }
        transaction.setGame_no_played(gameNumberSelected);
        transaction.setGame_names_id(getIntent().getStringExtra(Constants.GAME_NAMES_ID));
        transaction.setGame_types_id(getIntent().getStringExtra(Constants.GAME_TYPES_ID));
        transaction.setGame_type_options_id(getIntent().getStringExtra(Constants.GAME_TYPE_OPTIONS_ID));
        transaction.setLines(String.format("%s", lines));
        transaction.setTicketId(dbController.getUser().get(Constants.TICKET_ID));
        transaction.setUnitStake(unitStakes.getText().toString().trim());
        transaction.setTotalAmount(totalAmount.getText().toString().trim());
        transaction.setDate_played(datePlayed);
        transaction.setTime_played(timePlayed);
        transaction.setGame_quaters_id(getIntent().getStringExtra(Constants.GAME_QUATERS_ID));
        transaction.setPayment_option(getString(R.string.cash));
        transaction.setUserId(dbController.getUser().get(Constants.USERS_ID));
        transaction.setGameName(getIntent().getStringExtra(Constants.GAME_NAME));
        transaction.setGameType(getIntent().getStringExtra(Constants.GAME_TYPE));
        transaction.setGameTypeOption(getIntent().getStringExtra(Constants.GAME_TYPE_OPTION));
        transaction.setGameQuater(getIntent().getStringExtra(Constants.GAME_QUATER_NAME));
        transaction.setSerialNo(serialNo);

        dbController.createTransaction(transaction);
    }

    public String generateSerialNo() {
        Random rnd = new Random();
        int rand = 1000000000 + rnd.nextInt(900000000);
        return String.format("%s", Math.abs(rand));
    }

    private void validatePreviousGame() {
        if (TextUtils.equals(gameNoSelectedTextBox.getText().toString().trim(), Constants.EMPTY)) {
            Toast.makeText(getApplicationContext(), getString(R.string.enter_game_no), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.equals(unitStakes.getText().toString().trim(), Constants.EMPTY)) {
            unitStakes.setError(getString(R.string.enter_unit_stake));
            unitStakes.requestFocus();
            return;
        }
        try {
            saveGamePlayed();
            // check if saved
            if (dbController.getTransactions().length() > 0) {
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadGridView() {
        arrayList = new ArrayList<>();
        arrayList.clear();
        gridView = findViewById(R.id.game_grid_view);
        for (int i = 1; i <= 90; i++) {
            arrayList.add(String.format("%s", i));
        }
        adapter = new GridListAdapter(SelectedGameActivity.this, arrayList);
        adapter.setCustomButtonListner(SelectedGameActivity.this);
        gridView.setAdapter(adapter);
    }

    private void loadViews() {
        gameNoSelectedTextBox = (EditText) findViewById(R.id.game_nos);
        totalAmount = (EditText) findViewById(R.id.game_amount);
        unitStakes = (EditText) findViewById(R.id.unit_stake);
        playNewGameButton = (Button) findViewById(R.id.playNewGame);
        payForGameButton = (Button) findViewById(R.id.proceedToPlay);
        againstLayout = (LinearLayout) findViewById(R.id.against_layout);
        againstNo = (EditText) findViewById(R.id.against_no);
    }

    public Button getPlayNewGameButton() {
        return playNewGameButton;
    }

    public Button getPayForGameButton() {
        return payForGameButton;
    }

    @Override
    public void onButtonClickListner(int position, String value, GridListAdapter.ViewHolder holder) {
        if (TextUtils.isEmpty(gameNoSelectedTextBox.getText().toString().trim())) {
            gameNoSelectedTextBox.append(String.format("%s", value));
        }
        else {
            gameNumbers = gameNoSelectedTextBox.getText().toString().trim().split(",");
            if(TextUtils.equals(gameType, getString(R.string.DIRECT))) {
                if (TextUtils.equals(gameTypeOption, getString(R.string.direct2))) {
                    if (gameNumbers.length == 2){
                        message = getString(R.string.only_two);
                        showDialog(message);
                    }
                    else{
                        gameNoSelectedTextBox.append(String.format(",%s", value));
                    }
                }else if (TextUtils.equals(gameTypeOption, getString(R.string.direct3))) {
                    if (gameNumbers.length == 3){
                        message = getString(R.string.only_three);
                        showDialog(message);
                    }
                    else{
                        gameNoSelectedTextBox.append(String.format(",%s", value));
                    }
                }else if (TextUtils.equals(gameTypeOption, getString(R.string.direct4))) {
                    if (gameNumbers.length == 4){
                        message = getString(R.string.only_four);
                        showDialog(message);
                    }
                    else{
                        gameNoSelectedTextBox.append(String.format(",%s", value));
                    }
                } else if (TextUtils.equals(gameTypeOption, getString(R.string.direct5))) {
                    if (gameNumbers.length == 5){
                        message = getString(R.string.only_five);
                        showDialog(message);
                    }else{
                        gameNoSelectedTextBox.append(String.format(",%s", value));
                    }
                }
            }
            else {
                gameNoSelectedTextBox.append(String.format(",%s", value));
            }

        }
    }

    private HashMap<String, String> computePermutation(int unitStake, int noOfSelectedFigure, String gameTypeOption) {
        double totalAmountToBeStaked = 0.0;
        HashMap<String, String> resultOfComputation = new HashMap<String, String>();
        if (noOfSelectedFigure != 0) {
            if (TextUtils.equals(gameTypeOption, getString(R.string.PERM2))){
                lines = (noOfSelectedFigure * (noOfSelectedFigure - 1) ) / 2 ;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.PERM3))) {
                lines = (noOfSelectedFigure * (noOfSelectedFigure - 1)  * (noOfSelectedFigure - 2)) / 6 ;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.PERM4))) {
                lines = (noOfSelectedFigure * (noOfSelectedFigure -1) * (noOfSelectedFigure -2) * (noOfSelectedFigure - 3)) / 24;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.PERM5))) {
                lines = (noOfSelectedFigure * (noOfSelectedFigure - 1) * (noOfSelectedFigure -2) * (noOfSelectedFigure - 3) * (noOfSelectedFigure -4)) / 120;
            }
            totalAmountToBeStaked = lines * unitStake;
            resultOfComputation.put(Constants.LINES, String.format("%s", lines));
            resultOfComputation.put(Constants.TOTAL_AMOUNT_STAKED, String.format("%s", totalAmountToBeStaked));
            return resultOfComputation;
        }
        else {
            String message = getString(R.string.value_cannot_be_zero);
            showDialog(message);
        }
        return null;
    }

    private HashMap<String, String> computeDirect(int unitStake, int noOfSelectedFigure, String gameTypeOption) {
        lines = 1; // CONSTANT FOR ALL DIRECT GAME
        double totalAmountToBeStaked = 0.0;
        HashMap<String, String> resultOfComputation = new HashMap<String, String>();
        if (noOfSelectedFigure != 0) {
            totalAmountToBeStaked = unitStake * lines;
            resultOfComputation.put(Constants.LINES, String.format("%s", lines));
            resultOfComputation.put(Constants.TOTAL_AMOUNT_STAKED, String.format("%s", totalAmountToBeStaked));
            return resultOfComputation;
        }
        return null;
    }

    private HashMap<String, String> computeAgainst(int unitStake, int noOfSelectedFigure, String gameTypeOption) {
        double totalAmountToBeStaked = 0.0;
        String gameNumberSelected =   gameNoSelectedTextBox.getText().toString().trim();
        String[] gameNos = gameNumberSelected.split(",");

        String gameAgainstNo = againstNo.getText().toString().trim();
        String[] gameAgNo = gameAgainstNo.split(",");
        HashMap<String, String> resultOfComputation = new HashMap<String, String>();
        if (noOfSelectedFigure != 0) {
            if (TextUtils.equals(gameTypeOption, getString(R.string.against1))) {
                lines = gameNos.length * 1;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.against2))) {
                lines = gameNos.length * 2;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.against3))) {
                lines = gameNos.length * 3;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.against4))) {
                lines = gameNos.length * 4;
            }
            else if (TextUtils.equals(gameTypeOption, getString(R.string.against5))) {
                lines = gameNos.length * 5;
            }
            totalAmountToBeStaked = lines * unitStake;
            resultOfComputation.put(Constants.LINES, String.format("%s", lines));
            resultOfComputation.put(Constants.TOTAL_AMOUNT_STAKED, String.format("%s", totalAmountToBeStaked));
            return resultOfComputation;
        }
        return null;
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectedGameActivity.this);
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