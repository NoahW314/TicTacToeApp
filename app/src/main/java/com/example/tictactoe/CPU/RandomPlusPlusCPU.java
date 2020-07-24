package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class RandomPlusPlusCPU extends RandomPlusCPU {
    public RandomPlusPlusCPU(SectionButton.Marker marker, String type) {
        super(marker, type);
    }
    public RandomPlusPlusCPU(SectionButton.Marker marker, String type, boolean log){
        super(marker, type, log);
    }

    @Override
    public int play(Board oldBoard) throws CPUTerminatedException {

        //First, play like RandomPlus, unless it wants to play random, then look ahead
        int randomPlusPlay = super.play(oldBoard);
        if(strategy == Strategy.UNKNOWN){
            throw new RuntimeException("RandomPlus doesn't know what to play?");
        }
        else if(strategy != Strategy.RANDOM){
            return randomPlusPlay;
        }

        throwIfTerminated();
        //Look ahead, check all squares to see if any move gives us two different (two in a row)s
        //If so, play on that square
        ArrayList<Integer> emptySquares = oldBoard.getEmptySquares();
        for(Integer empty : emptySquares){
            Board newBoard = new Board(oldBoard);
            newBoard.set(empty, ourMarker);

            throwIfTerminated();

            ArrayList<ArrayList<Integer>> twoMarkers = twoInARows(newBoard);

            throwIfTerminated();

            if(twoMarkers.get(ourMarker.id-1).size() > 1){
                if(log) Log.d(TAG, "Playing for 2 on "+empty);
                strategy = Strategy.WIN_2;
                return empty;
            }
        }


        //Otherwise, block them from getting two different (two in a row)s
        ArrayList<Integer> blockSquares = oldBoard.getEmptySquares();
        RandomPlusPlusCPU opponent = new RandomPlusPlusCPU(theirMarker, playerType, false);
        if(blockSquares.size() >= 7 || blockSquares.size() <= 2) blockSquares.clear();
        Strategy[] blockOptions = new Strategy[9]; for(int i = 0; i < 9; i++){ blockOptions[i] = Strategy.UNKNOWN;}
        int[] theirPlays = new int[9];
        for(Integer empty : blockSquares){
            Board newBoard = new Board(oldBoard);
            newBoard.set(empty, ourMarker);

            theirPlays[empty] = opponent.play(newBoard);
            blockOptions[empty] = opponent.strategy;

            throwIfTerminated();
        }

        int blockPlay = findBestBlock(blockOptions, theirPlays, oldBoard);
        if(blockPlay != -1){
            if(log) Log.d(TAG, "Blocking for 2 on "+blockPlay);
            strategy = Strategy.BLOCK_2;
            return blockPlay;
        }

        /*emptySquares = oldBoard.getEmptySquares();
        for(Integer empty : emptySquares){
            Board newBoard = new Board(oldBoard);
            newBoard.set(empty, theirMarker);

            ArrayList<ArrayList<Integer>> twoMarkers = new ArrayList<>(2);
            twoMarkers.add(new ArrayList<Integer>(10));
            twoMarkers.add(new ArrayList<Integer>(10));

            for(int i = 0; i < 3; i++){
                int[] hor = twoOfThree(new int[]{i*3, i*3+1, i*3+2}, newBoard);
                int[] ver = twoOfThree(new int[]{i, i+3, i+6}, newBoard);
                if(hor[0] != -1){
                    twoMarkers.get(hor[0]-1).add(hor[1]);
                }
                if(ver[0] != -1){
                    twoMarkers.get(ver[0]-1).add(ver[1]);
                }
            }

            throwIfTerminated();

            for(int i = 0; i < 2; i++){
                int[] dia = twoOfThree(new int[]{i*2, 4, 8-i*2}, newBoard);
                if(dia[0] != -1){
                    twoMarkers.get(dia[0]-1).add(dia[1]);
                }
            }

            throwIfTerminated();
            if(twoMarkers.get(theirMarker.id-1).size() > 1){
                Log.d(TAG, "Blocking for 2 on "+empty);
                strategy = Strategy.BLOCK_2;
                return empty;
            }
        }*/

        //Otherwise, play randomly
        return randomPlusPlay;
    }

    private int findBestBlock(Strategy[] blocks, int[] theirPlays, Board board) throws CPUTerminatedException {
        int play = -1;
        Strategy bestOpponentMove = Strategy.UNKNOWN;
        for(int i = 0; i < blocks.length; i++){

            if(blocks[i] == Strategy.UNKNOWN) continue; //represents a non-empty square
            Board newBoard = new Board(board);
            newBoard.set(i, ourMarker);
            newBoard.set(theirPlays[i], theirMarker);
            //if their play would give them twoTwos, then don't play there, since we would lose
            if(twoInARows(newBoard).get(theirMarker.id-1).size() > 1) continue;

            switch(blocks[i]){
                case WIN: //the two cases below are a loss for us, so we never want to do that
                case WIN_2:
                    continue;
                case BLOCK: //The next best thing is to force them to block us
                case BLOCK_2: //(This will likely just force us into a cat's game, but we could win, it they make a mistake)
                    if(bestOpponentMove == Strategy.UNKNOWN){
                        play = i;
                        bestOpponentMove = blocks[i];
                    }
                    break;
                case RANDOM: //Their playing randomly is best for us (though this may never happen)
                case END:
                    play = i;
                    bestOpponentMove = blocks[i];
                    break;
            }
        }
        return play;
    }
    protected ArrayList<ArrayList<Integer>> twoInARows(Board board) throws CPUTerminatedException{
        ArrayList<ArrayList<Integer>> twoMarkers = new ArrayList<>(2);
        twoMarkers.add(new ArrayList<Integer>(10));
        twoMarkers.add(new ArrayList<Integer>(10));

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

        return twoMarkers;
    }
}
