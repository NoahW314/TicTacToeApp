package com.example.tictactoe.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tictactoe.CPU.CPU;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.GameLogic.GameManagerImpl;
import com.example.tictactoe.GameLogic.Player;
import com.example.tictactoe.R;
import com.example.tictactoe.SectionClickListener;
import com.example.tictactoe.Views.BoardLayout;

import java.util.Arrays;
import java.util.List;

/**Logging conventions:
 * Verbose is for things that we normally will never care about, unless we need some extreme
    debugging of a single portion of the program
 * Debug is for normal game flow stuff, creating, playing, turn logic, cpu decisions etc.
 * Info is for major game flow stuff, starting, restarting, stopping, etc
 * */
public class BoardActivity extends AppCompatActivity {

    public static final String TAG = "BoardActivity";

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
        Log.i(TAG, Arrays.toString(intA));
        List<String> players = Player.fromIntArray(intA);
        manager.setCPUs(CPU.fromPlayers(players));
        Log.v(TAG, manager.getCPU(0)+" "+manager.getCPU(1));
        manager.setStatMode(intent.getBooleanExtra(SelectionActivity.statMode, false));

        findViewById(R.id.restart_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                restartGame();
            }
        });

        manager.start(this);
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
        Log.i(TAG, "Restarting Game");
        manager.restart(this);
        if(!manager.inStatMode()) {
            ((TextView) findViewById(R.id.info_text)).setText(R.string.player_1_turn);
            ((BoardLayout) findViewById(R.id.board_layout)).redraw();
        }
    }

    public void resetGame(){
        manager = new GameManagerImpl(boxListener);
        ((TextView)findViewById(R.id.info_text)).setText(R.string.player_1_turn);
        ((BoardLayout)findViewById(R.id.board_layout)).redraw();
        ((TextView)findViewById(R.id.win_count_1)).setText(getString(R.string.player_1_win_count, 0));
        ((TextView)findViewById(R.id.win_count_2)).setText(getString(R.string.player_2_win_count, 0));
        ((TextView)findViewById(R.id.game_count)).setText(getString(R.string.game_count, 0));
    }
}
