package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RandomPlusCPU extends RandomCPU{

    public RandomPlusCPU(SectionButton.Marker marker, String type) {
        super(marker, type);
    }
    public RandomPlusCPU(SectionButton.Marker marker, String type, boolean log){
        super(marker, type, log);
    }

    public int[] twoOfThree(int[] three, Board board){
        int[] twoP = new int[]{-1, -1};

        if(board.get(three[0]) == board.get(three[1])
                && board.get(three[0]) != SectionButton.Marker.NONE
                && board.get(three[2]) == SectionButton.Marker.NONE){
            twoP = new int[]{board.get(three[0]).id, three[2]};
        }
        else if(board.get(three[0]) == board.get(three[2])
                && board.get(three[2]) != SectionButton.Marker.NONE
                && board.get(three[1]) == SectionButton.Marker.NONE){
            twoP = new int[]{board.get(three[2]).id, three[1]};
        }
        else if(board.get(three[1]) == board.get(three[2])
                && board.get(three[1]) != SectionButton.Marker.NONE
                && board.get(three[0]) == SectionButton.Marker.NONE){
            twoP = new int[]{board.get(three[1]).id, three[0]};
        }

        return twoP;
    }

    public Strategy strategy = Strategy.UNKNOWN;
    public int[] strategyCount = new int[Strategy.values().length];

    @Override
    public int play(Board oldBoard) throws CPUTerminatedException {
        return play(oldBoard, false);
    }

    @Override
    public int play(Board oldBoard, boolean isSuperCall) throws CPUTerminatedException {

        if(log) Log.v(TAG, "Playing...");

        ArrayList<ArrayList<Integer>> twoMarkers = new ArrayList<>(2);
        twoMarkers.add(new ArrayList<Integer>(10));
        twoMarkers.add(new ArrayList<Integer>(10));

        Board board = new Board(oldBoard);

        throwIfTerminated();

        for(int i = 0; i < 3; i++){
            int[] hor = twoOfThree(new int[]{i*3, i*3+1, i*3+2}, board);
            int[] ver = twoOfThree(new int[]{i, i+3, i+6}, board);
            if(hor[0] != -1){
                twoMarkers.get(hor[0]-1).add(hor[1]);
            }
            if(ver[0] != -1){
                twoMarkers.get(ver[0]-1).add(ver[1]);
            }
        }

        throwIfTerminated();

        for(int i = 0; i < 2; i++){
            int[] dia = twoOfThree(new int[]{i*2, 4, 8-i*2}, board);
            if(dia[0] != -1){
                twoMarkers.get(dia[0]-1).add(dia[1]);
            }
        }

        throwIfTerminated();

        if(twoMarkers.get(ourMarker.id-1).size() > 0){
            int play = twoMarkers.get(ourMarker.id-1).get(0);
            if(log) Log.d(TAG, "Playing 2 on "+play);
            strategy = Strategy.WIN;
            return play;
        }
        if(twoMarkers.get(theirMarker.id-1).size() > 0){
            int play = twoMarkers.get(theirMarker.id-1).get(0);
            if(log) Log.d(TAG, "Block 2 on "+play);
            strategy = Strategy.BLOCK;
            return play;
        }

        strategy = Strategy.RANDOM;

        int randomPlay = super.play(board, true);
        if(log && !isSuperCall) Log.d(TAG, "Playing Randomly "+randomPlay);
        return randomPlay;
    }

    public String getStrategies(){
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < strategyCount.length; i++){
            builder.append(Strategy.values()[i].name());
            builder.append(": ");
            builder.append(strategyCount[i]);
            builder.append("\n");
        }

        return builder.toString();
    }

    public enum Strategy{UNKNOWN(0), RANDOM(1), BLOCK(2), WIN(3), BLOCK_2(4), WIN_2(5), BLOCK_TRAP(6), SET_UP(7), TRAP(8), END(9);
        public int id; Strategy(int id){ this.id = id; }}


}
