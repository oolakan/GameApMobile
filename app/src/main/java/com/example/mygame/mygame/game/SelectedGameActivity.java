package com.example.mygame.mygame.game;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.adapter.GridListAdapter;

import org.json.JSONException;

import java.util.ArrayList;

import model.DBController;
import model.Transaction;
import utils.Constants;

import static com.example.mygame.mygame.adapter.GridListAdapter.*;

public class SelectedGameActivity extends AppCompatActivity implements  customButtonListener {
    private Context context;
    private GridListAdapter adapter;
    private ArrayList<String> arrayList;
    GridView gridView;
    private EditText gameNoSelectedTextBox, gameAmount, unitStakes;
    private Button playNewGameButton, payForGameButton;
    private DBController dbController;
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
        getPlayNewGameButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // get current timestamp
                    Long tsLong = System.currentTimeMillis()/1000;
                    String currentTimestamp = tsLong.toString();
                    Transaction transaction = new Transaction();
                    transaction.setGame_no_played(getIntent().getStringExtra(Constants.GAME_NO_PLAYED));
                    transaction.setGame_names_id(getIntent().getStringExtra(Constants.GAME_NAMES_ID));
                    transaction.setGame_types_id(getIntent().getStringExtra(Constants.GAME_TYPES_ID));
                    transaction.setGame_type_options_id(getIntent().getStringExtra(Constants.GAME_TYPE_OPTIONS_ID));
                    transaction.setGame_quaters_id(getIntent().getStringExtra(Constants.GAME_QUATERS_ID));
                    transaction.setAmount_paid(gameAmount.getText().toString().trim());
                    transaction.setTime_played(currentTimestamp);
                    transaction.setPayment_option(getString(R.string.cash));
                    dbController.createTransaction(transaction);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // get transacions
                Toast.makeText(getApplicationContext(), dbController.getTransactions().size()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadGridView() {
        gridView = (GridView) findViewById(R.id.game_grid_view);
        arrayList = new ArrayList<>();
        for (int i = 1; i <= 90; i++)
            arrayList.add(i + "");
        adapter = new GridListAdapter(SelectedGameActivity.this, arrayList);
        adapter.setCustomButtonListner(SelectedGameActivity.this);
        gridView.setAdapter(adapter);
    }

    private void loadViews() {
        gameNoSelectedTextBox = (EditText) findViewById(R.id.lines);
        gameAmount = (EditText) findViewById(R.id.game_amount);
        unitStakes = (EditText) findViewById(R.id.unit_stake);

        playNewGameButton = (Button) findViewById(R.id.playNewGame);
        payForGameButton = (Button) findViewById(R.id.proceedToPlay);
    }

    public Button getPlayNewGameButton() {
        return playNewGameButton;
    }

    @Override
    public void onButtonClickListner(int position, String value, GridListAdapter.ViewHolder holder) {
        if (TextUtils.isEmpty(gameNoSelectedTextBox.getText().toString().trim())) {
            gameNoSelectedTextBox.append(String.format("%s", value));
        }
        else {
            gameNoSelectedTextBox.append(String.format(",%s", value));
        }
    }
}
