package com.example.tictactoe.GameLogic;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.example.tictactoe.Activities.BoardActivity;
import com.example.tictactoe.Board;
import com.example.tictactoe.CPU.CPU;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.R;
import com.example.tictactoe.SectionClickListener;
import com.example.tictactoe.Views.BoardLayout;
import com.example.tictactoe.Views.SectionButton;

import static com.example.tictactoe.GameLogic.Turn.PLAYER_1;
import static com.example.tictactoe.GameLogic.Turn.PLAYER_2;

public class GameManagerImpl implements GameManager {

    //the tic-tac-toe board
    private Board board = new Board();
    //the number of wins by each player
    private int[] wins = new int[]{0, 0};
    //the number of games played
    private int gameNums = 0;
    //represents whose turn it is
    private Turn turn = PLAYER_1;
    private CPU[] CPUs = new CPU[2];
    //the current status of the game
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    //listens for the user pressing one of the boxes (a button)
    private SectionClickListener clickListener;
    //removes all delays so that games can be run as fast as possible, for statistical purposes
    private boolean statMode = false;

    private Thread turnThread;
    private final Object turnLock = new Object();
    private boolean restarted = false;
    private int humanIndex = -1;

    public GameManagerImpl(SectionClickListener sectionClickListener){
        Log.d(TAG, "Creating Game Manager Implementation");
        clickListener = sectionClickListener;
    }

    @Override
    public void setCPUs(CPU[] cpus) {
        System.arraycopy(cpus, 0, CPUs, 0, 2);
        Log.d(TAG, "CPUs set as "+cpus[0].getPlayerType()+" and "+cpus[1].getPlayerType());
    }

    @Override
    public CPU getCPU(int i) { return CPUs[i]; }

    @Override
    public void setStatMode(boolean statMode) {
        this.statMode = statMode;
        Log.d(TAG, "Stat Mode set to "+statMode);
    }

    @Override
    public boolean inStatMode() { return statMode; }

    public void start(final Activity activity){
        Log.d(TAG, "Starting Turn Control Thread");
        turnThread = new Thread(new Runnable(){
            //Should we start a new game
            Boolean startNewGame = false;
            public void run(){
                restarted = false;
                do{
                    if (isHumanTurn()) {
                        Log.d(TAG, turn+": Human Turn");
                        synchronized (turnLock) {
                            while (humanIndex == -1 && !restarted) {
                                try {
                                    Log.d(TAG, "Waiting for Human...");
                                    turnLock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.d(TAG, "Human Play at index "+humanIndex);
                        startNewGame = play(humanIndex, activity);
                        humanIndex = -1;
                    }
                    else {
                        Log.d(TAG, turn+": CPU "+CPUs[turn.id].getPlayerType());
                        startNewGame = cpuPlay(activity);
                    }
                    Log.i(TAG, getGameStatus()+"");

                    if(startNewGame == null || startNewGame) break; //paranoia
                } while(getGameStatus() == GameStatus.IN_PROGRESS && !restarted);
                if(startNewGame != null && startNewGame){
                    ((BoardActivity) activity).restartGame();
                }
            }
        });
        turnThread.start();
    }

    private Boolean cpuPlay(Activity activity){
        if(restarted) return null;
        long startTime = System.currentTimeMillis();
        int index = -1;
        try {
            index = CPUs[turn.id].play(new Board(board));
        } catch(CPUTerminatedException e){ Log.d(TAG, "CPU Play terminated");}
        Log.d(TAG, "CPU playing at "+index);
        long thinkTime = System.currentTimeMillis() - startTime;

        if(restarted) return null;

        if(statMode) return play(index, activity);
        else return play(index, activity, 750-thinkTime);
    }
    public void humanPlay(int index){
        synchronized (turnLock) {
            Log.d(TAG, "Human Played at "+index);
            humanIndex = index;
            turnLock.notifyAll();
        }
    }
    /**@return if a new game should be started, or null if a new game has been started*/
    private Boolean play(int index, Activity activity, long milliDelay){
        if(restarted) return null;
        buttonPressed(index, activity, milliDelay);
        if(restarted) return null;

        return boardChanged(activity);
    }
    /**@return if a new game should be started*/
    private Boolean play(int index, Activity activity){return play(index, activity, 0);}

    @Override
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public SectionButton.Marker getBoardSectionMarker(int sectionNum) {
        return board.get(sectionNum);
    }

    /**@return if a new game should be started*/
    private boolean boardChanged(Activity activity){
        TextView infoText = activity.findViewById(R.id.info_text);
        TextView gameCount = activity.findViewById(R.id.game_count);
        TextView win1Count = activity.findViewById(R.id.win_count_1);
        TextView win2Count = activity.findViewById(R.id.win_count_2);
        BoardLayout boardLayout = activity.findViewById(R.id.board_layout);

        int[] threeInARow = board.findThree();
        if(threeInARow[0] != -1){
            return winner(threeInARow, turn, boardLayout, infoText, win1Count, win2Count, gameCount);
        }

        if(board.isFull() && gameStatus == GameStatus.IN_PROGRESS){
            return catsGame(boardLayout, infoText, gameCount);
        }

        Log.d(TAG, "Game In Progress");

        if(gameStatus == GameStatus.IN_PROGRESS){
            switch (turn) {
                case PLAYER_1:
                    if(!statMode) setText(infoText, R.string.player_1_turn);
                    turn = PLAYER_2;
                    break;
                case PLAYER_2:
                    if(!statMode) setText(infoText, R.string.player_2_turn);
                    turn = PLAYER_1;
                    break;
            }
        }
        return false;

    }

    private void buttonPressed(int index, Activity activity, long milliDelay){
        try {
            Thread.sleep(Math.max(milliDelay, 0));
        } catch(InterruptedException e){
            return;
        }
        buttonPressed(index, activity);
    }

    private void buttonPressed(int index, Activity activity){
        if(index < 0) return;

        Log.d(TAG, "Button Pressed at "+index+" Previous Marker"+board.get(index));
        Log.v(TAG, gameStatus+"");

        if(board.get(index) != SectionButton.Marker.NONE || gameStatus != GameStatus.IN_PROGRESS){
            return;
        }

        BoardLayout boardLayout = activity.findViewById(R.id.board_layout);
        switch (turn) {
            case PLAYER_1:
                board.set(index, SectionButton.Marker.O);
                break;
            case PLAYER_2:
                board.set(index, SectionButton.Marker.X);
                break;
        }
        if(!statMode){redrawBoard(boardLayout);}
        Log.d(TAG, "Button Pushed");
    }

    public boolean isHumanTurn(){
        synchronized (turnLock) {
            return CPUs[turn.id].isHuman();
        }
    }

    /**@return if a new game should be started*/
    private boolean winner(int[] rowLocations, Turn turn, BoardLayout layout, TextView infoText,
                        TextView win1Count, TextView win2Count, TextView gameCount){
        gameNums++;
        setText(gameCount, gameCount.getContext().getString(R.string.game_count, gameNums));
        if(turn == PLAYER_2) {
            wins[1]++;
            gameStatus = GameStatus.WON_2;
            if(!statMode){ setText(infoText, R.string.player_2_win);}
            setText(win2Count, win2Count.getContext().getString(R.string.player_2_win_count, wins[1]));
        }
        else {
            wins[0]++;
            gameStatus = GameStatus.WON_1;
            if(!statMode){setText(infoText, R.string.player_1_win);}
            setText(win1Count, win1Count.getContext().getString(R.string.player_1_win_count, wins[0]));
        }
        if(!statMode){ layout.onWinner(rowLocations);}

        return autoStart(layout);
    }
    /**@return if a new game should be started*/
    private boolean catsGame(BoardLayout layout, TextView infoText, TextView gameCount){
        gameNums++;
        setText(gameCount, gameCount.getContext().getString(R.string.game_count, gameNums));
        gameStatus = GameStatus.CATS_GAME;
        if(!statMode){
            setText(infoText, R.string.cats_game);
            layout.onCatsGame();
        }

        return autoStart(layout);
    }

    /**@return if a new game should be started*/
    private boolean autoStart(final BoardLayout layout){
        return statMode && gameNums < 1000;
    }

    public void restart(final Activity activity){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //shutdown
                Log.i(TAG, "Shutting Down");
                for(CPU cpu : CPUs){
                    cpu.terminate();
                }
                restarted = true;
                synchronized (turnLock){turnLock.notifyAll();}
                turnThread.interrupt();

                //restart
                Log.i(TAG, "Restarting");
                for(int i = 0; i < 9; i++) {
                    board.set(i, SectionButton.Marker.NONE);
                }
                turn = PLAYER_1;
                gameStatus = GameStatus.IN_PROGRESS;
                try {
                    turnThread.join();
                } catch(InterruptedException e){e.printStackTrace();}
                restarted = true;
                for(CPU cpu : CPUs){
                    cpu.restart();
                }
                start(activity);
            }
        }).start();
    }

    private void redrawBoard(final BoardLayout boardLayout){
        Log.v(TAG, "Redrawing Board");
        boardLayout.post(new Runnable(){
            public void run(){
                boardLayout.redraw();
            }
        });
    }
    private void setText(final TextView textView, final String text){
        Log.v(TAG, "Setting text");
        textView.post(new Runnable() {
            public void run() {
                textView.setText(text);
            }
        });
    }
    private void setText(final TextView textView, final int text){
        Log.v(TAG, "Setting text");
        textView.post(new Runnable() {
            public void run() {
                textView.setText(text);
            }
        });
    }
}
