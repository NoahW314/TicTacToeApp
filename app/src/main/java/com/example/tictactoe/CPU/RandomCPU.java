package com.example.tictactoe.CPU;

import android.app.Activity;
import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class RandomCPU extends CPU{

    public RandomCPU(SectionButton.Marker marker, Activity activity) {
        super(marker, activity);
    }

    @Override
    public void play(GameManager gameManager){
        play(gameManager, 0);//500
    }

    public void play(GameManager gameManager, int milliDelay){
        Board board = gameManager.board;
        ArrayList<Integer> emptySquares = new ArrayList<>(9);

        for(int i = 0; i < 9; i++){
            if(board.get(i) == SectionButton.Marker.NONE){
                emptySquares.add(i);
            }
        }


        int emptySquaresIndex = (int)Math.floor(Math.random()*(emptySquares.size()));
        int selection = emptySquares.get(emptySquaresIndex);

        Log.v(TAG, selection+"");

        gameManager.buttonPressed(selection, activity, milliDelay);
    }
}
