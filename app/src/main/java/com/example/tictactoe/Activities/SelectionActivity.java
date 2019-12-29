package com.example.tictactoe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tictactoe.GameLogic.Player;
import com.example.tictactoe.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {

    public static final String TAG = "SelectionActivityV";
    public static final String playerInfo = "com.example.tictactoe.PLAYER_INFO";
    public static final String statMode = "com.example.tictactoe.STAT_MODE";
    public List<String> players = new ArrayList<>(Arrays.asList("Human", "Human"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        //register the strings from the player options resources to the player class
        String[] playersOptions = getResources().getStringArray(R.array.player_options);
        for(String option : playersOptions){

            Player.registerName(option);
        }
        Log.v(TAG, Player.idsUsed.toString());
        Log.v(TAG, Player.idsUsed.contains("Human")+"");

        //add listeners to the player selection spinners
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
        Log.v(TAG, players.toString());
        intent.putExtra(playerInfo, Player.toIntArray(players));
        startActivity(intent);
    }
    public void onStatModeClicked(View v){
        Intent intent = new Intent(this, BoardActivity.class);
        Log.v(TAG, players.toString());
        int[] pia = Player.toIntArray(players);
        Log.v(TAG, pia[0]+" "+pia[1]);
        intent.putExtra(playerInfo, pia);
        intent.putExtra(statMode, true);
        startActivity(intent);
    }

    public class PlayerOptionsListener implements AdapterView.OnItemSelectedListener {

        private int id;

        public PlayerOptionsListener(int i){
            this.id = i;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            players.set(this.id, Player.fromId((int)id));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
