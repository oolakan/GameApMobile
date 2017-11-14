package com.example.mygame.mygame.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mygame.mygame.R;

public class PrintResultActivity extends AppCompatActivity {

    private Button playNewGameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_result);
        playNewGameBtn = findViewById(R.id.startNewGame);
        playNewGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrintResultActivity.this, GameHomeActivity.class));
                finish();
            }
        });
    }
}
