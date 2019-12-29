package com.example.tictactoe.CPU;

import com.example.tictactoe.Board;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

public class Human extends CPU {

    public Human(SectionButton.Marker ourMarker, String type){
        super(ourMarker, type);
    }

    @Override
    public int play(Board board) {
        return -1;
    }

    @Override
    public boolean isHuman(){return true;}
}
