package com.example.tictactoe.GameLogic;

//1 is O, 2 is x
public enum Turn {
    PLAYER_1(0),
    PLAYER_2(1),
    ;
    public int id;
    Turn(int id){
        this.id = id;
    }
    public Turn switchTurn(){
        if(this == PLAYER_1) return PLAYER_2;
        else return PLAYER_1;
    }
}
