package com.example.tictactoe.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tictactoe.GameManager;
import com.example.tictactoe.R;

public class SelectionActivity extends AppCompatActivity{

    public static final String TAG = "SelectionActivityV";
    public static final String playerInfo = "com.example.tictactoe.PLAYER_INFO";
    public static final String continuous = "com.example.tictactoe.CONTINUOUS";
    public GameManager.Player[] players = new GameManager.Player[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Spinner spinner1 = findViewById(R.id.player_1_options);
        Spinner spinner2 = findViewById(R.id.player_2_options);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.player_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new PlayerOptionsListener(0));
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new PlayerOptionsListener(1));
    }

    public void onContinueClicked(View v){
        Intent intent = new Intent(this, BoardActivity.class);
        intent.putExtra(playerInfo, GameManager.Player.toIntArray(players));
        startActivity(intent);
    }
    public void onContinuousClicked(View v){
        Intent intent = new Intent(this, BoardActivity.class);
        int[] pia = GameManager.Player.toIntArray(players);
        Log.v(TAG, pia[0]+" "+pia[1]);
        intent.putExtra(playerInfo, pia);
        intent.putExtra(continuous, true);
        startActivity(intent);
    }

    public class PlayerOptionsListener implements AdapterView.OnItemSelectedListener {

        private int id;

        public PlayerOptionsListener(int i){
            this.id = i;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            players[this.id] = GameManager.Player.fromId((int)id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
