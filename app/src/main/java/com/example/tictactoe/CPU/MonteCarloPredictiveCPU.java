package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class MonteCarloPredictiveCPU extends MonteCarloCPU {
    private CPU otherPlayer;
    public MonteCarloPredictiveCPU(SectionButton.Marker marker, String playerType, int... pathNum){
        super(marker, playerType, pathNum);
    }

    public void setOtherPlayer(CPU player){
        if(player.isHuman()) otherPlayer = new RandomPlusCPU(theirMarker, "Human Model");
        //else if(player instanceof MonteCarloPredictiveCPU) otherPlayer = new RandomPlusCPU(theirMarker);
        else otherPlayer = player;
    }

    @Override
    public int generateNextIndex(Board board, Turn turn, ArrayList<Integer> emptySquares) throws CPUTerminatedException {
        Log.i(TAG, otherPlayer.getPlayerType());
        if(turn.equals(Turn.OURS)) return super.generateNextIndex(board, turn, emptySquares);
        else return otherPlayer.play(board);
    }
}
