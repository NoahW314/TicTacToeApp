package com.example.tictactoe.CPU;

import android.app.Activity;
import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class RandomPlusCPU extends RandomCPU{

    public RandomPlusCPU(SectionButton.Marker marker, Activity activity) {
        super(marker, activity);
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

    @Override
    public void play(GameManager gameManager) {

        Log.v(TAG, "Playing...");

        ArrayList<ArrayList<Integer>> twoMarkers = new ArrayList<>(2);
        twoMarkers.add(new ArrayList<Integer>(10));
        twoMarkers.add(new ArrayList<Integer>(10));

        Board board = gameManager.board;

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
        for(int i = 0; i < 2; i++){
            int[] dia = twoOfThree(new int[]{i*2, 4, 8-i*2}, board);
            if(dia[0] != -1){
                twoMarkers.get(dia[0]-1).add(dia[1]);
            }
        }

        if(twoMarkers.get(ourMarker.id-1).size() > 0){
            Log.v(TAG, "Playing 2 ours");
            gameManager.buttonPressed(twoMarkers.get(ourMarker.id-1).get(0), activity, 0);//500
            return;
        }
        if(twoMarkers.get(theirMarker.id-1).size() > 0){
            Log.v(TAG, "Playing 2 theirs");
            gameManager.buttonPressed(twoMarkers.get(theirMarker.id-1).get(0), activity, 0);//500
            return;
        }

        Log.v(TAG, "Randomly Playing");

        super.play(gameManager, 0);//500
    }
}
