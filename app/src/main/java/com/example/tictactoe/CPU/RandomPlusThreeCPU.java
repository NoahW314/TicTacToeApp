package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;
import java.util.Collections;

public class RandomPlusThreeCPU extends RandomPlusPlusCPU {
    public RandomPlusThreeCPU(SectionButton.Marker marker, String type) { super(marker, type); }
    public RandomPlusThreeCPU(SectionButton.Marker marker, String type, boolean log){super(marker, type, log);}

    @Override
    public int play(Board oldBoard) throws CPUTerminatedException {
        //First, play like Random++, unless it wants to play random, then look ahead
        int randomPlusPlusPlay = super.play(oldBoard);
        if(strategy == Strategy.UNKNOWN){//paranoia
            throw new RuntimeException("RandomPlusPlus doesn't know what to play?");
        }
        else if(strategy != Strategy.RANDOM){
            return randomPlusPlusPlay;
        }


        int[] lookAheads = new int[9];
        throwIfTerminated();
        //Look ahead, check all squares to see if any move gives us the potential for twoTwos
        ArrayList<Integer> emptySquares = oldBoard.getEmptySquares();
        for(Integer empty : emptySquares){
            Board newBoard = new Board(oldBoard);
            newBoard.set(empty, ourMarker);

            //for each possible move, check all the next two moves to see if there could be any twoTwos
            ArrayList<Integer> emptySquares2 = newBoard.getEmptySquares();
            int[] twoOfThree = newBoard.getTwoOfThree();
            if(twoOfThree[0] == ourMarker.id){
                emptySquares2 = new ArrayList<>(Collections.singletonList(twoOfThree[1]));
            }

            for(Integer empty2 : emptySquares2){
                Board newBoard2 = new Board(newBoard);
                newBoard2.set(empty2, theirMarker);

                ArrayList<Integer> emptySquares3 = newBoard2.getEmptySquares();
                int[] twoOfThree2 = newBoard2.getTwoOfThree();
                if(twoOfThree2[0] == theirMarker.id) {
                    emptySquares3 = new ArrayList<>(Collections.singletonList(twoOfThree2[1]));
                }

                for(Integer empty3 : emptySquares3) {
                    Board newBoard3 = new Board(newBoard2);
                    newBoard3.set(empty3, ourMarker);

                    ArrayList<ArrayList<Integer>> twoMarkers = new ArrayList<>(2);
                    twoMarkers.add(new ArrayList<Integer>(10));
                    twoMarkers.add(new ArrayList<Integer>(10));

                    throwIfTerminated();

                    for (int i = 0; i < 3; i++) {
                        int[] hor = twoOfThree(new int[]{i * 3, i * 3 + 1, i * 3 + 2}, newBoard3);
                        int[] ver = twoOfThree(new int[]{i, i + 3, i + 6}, newBoard3);
                        if (hor[0] != -1) {
                            twoMarkers.get(hor[0] - 1).add(hor[1]);
                        }
                        if (ver[0] != -1) {
                            twoMarkers.get(ver[0] - 1).add(ver[1]);
                        }
                    }

                    throwIfTerminated();

                    for (int i = 0; i < 2; i++) {
                        int[] dia = twoOfThree(new int[]{i * 2, 4, 8 - i * 2}, newBoard3);
                        if (dia[0] != -1) {
                            twoMarkers.get(dia[0] - 1).add(dia[1]);
                        }
                    }

                    if(log) Log.v(TAG, "For move on "+empty+", "+empty2+", we get " + twoMarkers.get(ourMarker.id - 1) +
                            " as two in a rows if played on " + empty3);
                    if(twoMarkers.get(ourMarker.id-1).size() > 1){
                        if(emptySquares2.size() == 1){
                            if(log) Log.d(TAG, "Playing for trap on "+empty);
                            strategy = Strategy.TRAP;
                            return empty;
                        }
                        else{
                            if(log) Log.v(TAG, "Counting potential on "+empty);
                            lookAheads[empty]++;
                            break;
                        }
                    }

                    throwIfTerminated();
                }
            }
        }

        int lookAheadPlay = findMaxOfLookAheads(lookAheads);
        if(lookAheadPlay != -1){
            if(log) Log.d(TAG, "Playing for potential two on "+lookAheadPlay+" with "+lookAheads[lookAheadPlay]+" options");
            strategy = Strategy.SET_UP;
            return lookAheadPlay;
        }

        //If we can't setup any two Twos (mostly for player 2), then try blocking
        ArrayList<Integer> blockSquares = oldBoard.getEmptySquares();
        //we can't block if nothing has been played
        if(blockSquares.size() == 9 || blockSquares.size() == 1){ blockSquares.clear(); }
        Strategy[] blockOptions = new Strategy[9]; for(int i = 0; i < 9; i++){ blockOptions[i] = Strategy.UNKNOWN;}
        int[] theirPlays = new int[9];
        RandomPlusThreeCPU opponent = new RandomPlusThreeCPU(theirMarker, playerType, false);
        for(Integer empty : blockSquares){
            Board newBoard = new Board(oldBoard);
            newBoard.set(empty, ourMarker);

            theirPlays[empty] = opponent.play(newBoard);
            blockOptions[empty] = opponent.strategy;

            throwIfTerminated();
        }

        int blockPlay = findBestBlock(blockOptions, theirPlays, oldBoard);
        if(blockPlay != -1){
            if(log) Log.d(TAG, "Blocking generally on "+blockPlay+" best move for them is "+blockOptions[blockPlay]);
            strategy = Strategy.BLOCK_TRAP;
            return blockPlay;
        }

        //Otherwise, play randomly
        if(log) Log.d(TAG, "Playing Randomly "+randomPlusPlusPlay);
        if(oldBoard.getEmptySquares().size() == 1) strategy = Strategy.END;
        return randomPlusPlusPlay;
    }

    private int findMaxOfLookAheads(int[] lookAheads){
        int maxCounts = 0;
        int maxSpot = -1;
        for(int i = 0; i < 9; i++){
            if(lookAheads[i] > maxCounts){
                maxSpot = i;
                maxCounts = lookAheads[i];
            }
        }
        return maxSpot;
    }

    private int findBestBlock(Strategy[] blocks, int[] theirPlays, Board board) throws CPUTerminatedException{
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
                case TRAP: //the three cases below are a loss for us, so we never want to do that
                case WIN:
                case WIN_2:
                    continue;
                case SET_UP: //Only allowing them a potential twoTwo, is better than playing randomly
                    if(bestOpponentMove == Strategy.UNKNOWN){
                        play = i;
                        bestOpponentMove = blocks[i];
                    }
                    break;
                case BLOCK: //The next best thing is to force them to block us. (This will likely just force us into a cat's game, but we could win)
                case BLOCK_2:
                case BLOCK_TRAP:
                    if(bestOpponentMove == Strategy.UNKNOWN || bestOpponentMove == Strategy.SET_UP){
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
}
