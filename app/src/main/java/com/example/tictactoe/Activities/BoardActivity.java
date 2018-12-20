package com.example.tictactoe.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tictactoe.CPU.CPU;
import com.example.tictactoe.GameManager;
import com.example.tictactoe.R;
import com.example.tictactoe.SectionClickListener;
import com.example.tictactoe.Views.BoardLayout;

public class BoardActivity extends AppCompatActivity {

    private static final String TAG = "BoardActivity";

    //manages the game and its transitions
    public static GameManager manager;
    //listens for button presses on the main boxes
    public static SectionClickListener boxListener = new SectionClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        setContentView(R.layout.activity_board);

        resetGame();

        Intent intent = getIntent();
        int[] intA = intent.getIntArrayExtra(SelectionActivity.playerInfo);
        GameManager.Player[] players = GameManager.Player.fromIntArray(intA);
        manager.CPUs = CPU.fromPlayers(players, this);
        Log.v(TAG, manager.CPUs[0]+" "+manager.CPUs[1]);
        manager.continuous = intent.getBooleanExtra(SelectionActivity.continuous, false);

        findViewById(R.id.restart_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                restartGame();
            }
        });

        manager.start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    public void restartGame(){
        manager.restart();
        ((TextView)findViewById(R.id.info_text)).setText(R.string.player_1_turn);
        ((BoardLayout)findViewById(R.id.board_layout)).redraw();
    }

    public void resetGame(){
        manager = new GameManager(boxListener);
        ((TextView)findViewById(R.id.info_text)).setText(R.string.player_1_turn);
        ((BoardLayout)findViewById(R.id.board_layout)).redraw();
        ((TextView)findViewById(R.id.win_count_1)).setText(getString(R.string.player_1_win_count, 0));
        ((TextView)findViewById(R.id.win_count_2)).setText(getString(R.string.player_2_win_count, 0));
        ((TextView)findViewById(R.id.game_count)).setText(getString(R.string.game_count, 0));
    }
}
