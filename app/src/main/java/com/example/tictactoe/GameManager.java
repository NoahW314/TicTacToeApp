package com.example.tictactoe;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tictactoe.Activities.BoardActivity;
import com.example.tictactoe.CPU.CPU;
import com.example.tictactoe.Views.BoardLayout;
import com.example.tictactoe.Views.SectionButton;

import static com.example.tictactoe.GameManager.Turn.PLAYER_1;
import static com.example.tictactoe.GameManager.Turn.PLAYER_2;

public class GameManager {
    public static final String TAG = "tictactoe.GameManager";

    //the tic-tac-toe board
    public Board board = new Board();
    //the number of wins by each player
    public int[] wins = new int[]{0, 0};
    //the number of games played
    public int gameNums = 0;
    //represents whose turn it is
    public Turn turn = PLAYER_1;
    public CPU[] CPUs = new CPU[2];
    //the current status of the game
    public GameStatus gameStatus = GameStatus.IN_PROGRESS;
    //listens for the user pressing one of the boxes (a button)
    private SectionClickListener clickListener;
    //handles the delaying of a computer pressing a button
    public Handler handler = new Handler();
    //whether or not to auto-start te next game
    public boolean continuous = false;


    public GameManager(SectionClickListener sectionClickListener){
        clickListener = sectionClickListener;

        for (int i = 0; i < 9; i++) {
            board.add(SectionButton.Marker.NONE);
        }
    }

    public void start(){
        if(CPUs[turn.id] != null){
            CPUs[turn.id].play(this);
        }
    }

    public void buttonPressed(final int index, final Activity activity, int milliDelay){
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                buttonPressed(index, activity);
            }
        }, milliDelay);
    }

    public void buttonPressed(int index, Activity activity){

        Log.v(TAG, index+" "+board.get(index));
        Log.v(TAG, gameStatus+"");

        if(board.get(index) != SectionButton.Marker.NONE || gameStatus != GameStatus.IN_PROGRESS){
            return;
        }

        TextView infoText = activity.findViewById(R.id.info_text);
        TextView gameCount = activity.findViewById(R.id.game_count);
        TextView win1Count = activity.findViewById(R.id.win_count_1);
        TextView win2Count = activity.findViewById(R.id.win_count_2);
        BoardLayout boardLayout = activity.findViewById(R.id.board_layout);

        switch (turn) {
            case PLAYER_1:
                board.set(index, SectionButton.Marker.O);
                boardLayout.redraw();
                turn = PLAYER_2;
                break;
            case PLAYER_2:
                board.set(index, SectionButton.Marker.X);
                boardLayout.redraw();
                turn = PLAYER_1;
                break;
        }


        int[] threeInARow = board.findThree();
        if(threeInARow[0] != -1){
            winner(threeInARow, turn, boardLayout, infoText, win1Count, win2Count, gameCount);
        }

        if(board.isFull() && gameStatus == GameStatus.IN_PROGRESS){
            catsGame(boardLayout, infoText, gameCount);
        }

        if(gameStatus == GameStatus.IN_PROGRESS){
            switch(turn){
                case PLAYER_1:
                    infoText.setText(R.string.player_1_turn);
                    break;
                case PLAYER_2:
                    infoText.setText(R.string.player_2_turn);
                    break;
            }
            if(CPUs[turn.id] != null){
                CPUs[turn.id].play(this);
            }
        }

        //Log.v("EndMarker", gameStatus.toString());
    }

    public boolean isHumanTurn(){
        return CPUs[turn.id] == null;
    }

    private void winner(int[] rowLocations, Turn turn, BoardLayout layout, TextView infoText,
                        TextView win1Count, TextView win2Count, TextView gameCount){
        gameNums++;
        gameCount.setText(gameCount.getContext().getString(R.string.game_count, gameNums));
        if(turn == PLAYER_1) {
            wins[1]++;
            gameStatus = GameStatus.WON_2;
            infoText.setText(R.string.player_2_win);
            win2Count.setText(win2Count.getContext().getString(R.string.player_2_win_count, wins[1]));
        }
        else{
            wins[0]++;
            gameStatus = GameStatus.WON_1;
            infoText.setText(R.string.player_1_win);
            win1Count.setText(win1Count.getContext().getString(R.string.player_1_win_count, wins[0]));
        }
        layout.onWinner(rowLocations);

        autoStart(layout);
    }
    private void catsGame(BoardLayout layout, TextView infoText, TextView gameCount){
        gameNums++;
        gameCount.setText(gameCount.getContext().getString(R.string.game_count, gameNums));
        gameStatus = GameStatus.CATS_GAME;
        infoText.setText(R.string.cats_game);
        layout.onCatsGame();

        autoStart(layout);
    }

    private void autoStart(final BoardLayout layout){
        if(continuous && gameNums < 1000){
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    ((BoardActivity)layout.getContext()).restartGame();
                }
            }, 0);//1000
        }
    }

    public void restart(){
        handler.removeCallbacksAndMessages(null);
        for(int i = 0; i < 9; i++) {
            board.set(i, SectionButton.Marker.NONE);
        }
        turn = PLAYER_1;
        gameStatus = GameStatus.IN_PROGRESS;
        start();
    }

    //1 is 0, 2 is x
    public enum Turn{
        PLAYER_1(0),
        PLAYER_2(1),
    ;
        public int id;
        Turn(int id){
            this.id = id;
        }
    }
    public enum GameStatus{IN_PROGRESS, WON_1, WON_2, CATS_GAME}
    public enum Player{
        Human(0),
        Random_CPU(1),
        Random_Plus_CPU(2),
        ;
        public int id;
        Player(int i){
            id = i;
        }
        public static int[] toIntArray(Player[] players){
            int[] intArray = new int[players.length];
            for(int i = 0; i < players.length; i++){
                intArray[i] = players[i].id;
            }
            return intArray;
        }
        public static Player[] fromIntArray(int[] intArr){
            Player[] players = new Player[intArr.length];
            for(int i = 0; i < intArr.length; i++){
                players[i] = fromId(intArr[i]);
            }
            return players;
        }
        public static Player fromId(int id){
            for(Player p : Player.values()){
                if(p.id == id){
                    return p;
                }
            }
            throw new IllegalArgumentException("Invalid ID for a player");
        }
    }
}
