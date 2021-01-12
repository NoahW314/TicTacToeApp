package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class RandomCPU extends CPU {
    protected boolean log = true;

    public RandomCPU(SectionButton.Marker marker, String type) {
        super(marker, type);
    }
    public RandomCPU(SectionButton.Marker marker, String type, boolean log){
        super(marker, type);
        this.log = log;
    }

    @Override
    public int play(Board oldBoard) throws CPUTerminatedException {
        return play(oldBoard, false);
    }

    public int play(Board oldBoard, boolean isSuperCall) throws CPUTerminatedException {
        Board board = new Board(oldBoard);
        ArrayList<Integer> emptySquares = new ArrayList<>(9);

        for(int i = 0; i < 9; i++){
            if(board.get(i) == SectionButton.Marker.NONE){
                emptySquares.add(i);
            }
        }


        int emptySquaresIndex = (int)Math.floor(Math.random()*(emptySquares.size()));
        int selection = emptySquares.get(emptySquaresIndex);

        if(log && !isSuperCall) Log.d(TAG, "Randomly Playing "+selection);

        return selection;
    }
}
