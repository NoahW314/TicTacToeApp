package com.example.tictactoe;

import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class Board extends ArrayList<SectionButton.Marker> {
    public Board(){
        super(9);
        for (int i = 0; i < 9; i++) {
            add(SectionButton.Marker.NONE);
        }
    }
    public Board(Board board){
        super(board);
    }

    public int[] findThree(){
        int[] three = new int[]{-1, -1, -1};

        for(int i = 0; i < 3; i++){
            //Log.v("EndMarker", get(i*3)+" "+get(i*3+1)+" "+get(i*3+2));
            //horizontal
            if(get(i*3) == get(i*3+1) && get(i*3) == get(i*3+2) && get(i*3) != SectionButton.Marker.NONE){
                three[0] = i*3;
                three[1] = i*3+1;
                three[2] = i*3+2;
            }
            //vertical
            if(get(i) == get(i+3) && get(i) == get(i+6) && get(i) != SectionButton.Marker.NONE){
                three[0] = i;
                three[1] = i+3;
                three[2] = i+6;
            }
        }
        //diagonal
        for(int i = 0; i < 2; i++){
            if(get(4) == get(i*2) && get(4) == get(8-i*2) && get(4) != SectionButton.Marker.NONE){
                three[0] = i*2;
                three[1] = 4;
                three[2] = 8-i*2;
            }
        }
        return three;
    }

    public int[] twoOfThree(int[] three){
        int[] twoP = new int[]{-1, -1};

        if(get(three[0]) == get(three[1])
                && get(three[0]) != SectionButton.Marker.NONE
                && get(three[2]) == SectionButton.Marker.NONE){
            twoP = new int[]{get(three[0]).id, three[2]};
        }
        else if(get(three[0]) == get(three[2])
                && get(three[2]) != SectionButton.Marker.NONE
                && get(three[1]) == SectionButton.Marker.NONE){
            twoP = new int[]{get(three[2]).id, three[1]};
        }
        else if(get(three[1]) == get(three[2])
                && get(three[1]) != SectionButton.Marker.NONE
                && get(three[0]) == SectionButton.Marker.NONE){
            twoP = new int[]{get(three[1]).id, three[0]};
        }

        return twoP;
    }

    public int[] getTwoOfThree(){
        for(int i = 0; i < 3; i++){
            int[] hor = twoOfThree(new int[]{i*3, i*3+1, i*3+2});
            int[] ver = twoOfThree(new int[]{i, i+3, i+6});
            if(hor[0] != -1){
                return hor;
            }
            if(ver[0] != -1){
                return ver;
            }
        }

        for(int i = 0; i < 2; i++){
            int[] dia = twoOfThree(new int[]{i*2, 4, 8-i*2});
            if(dia[0] != -1){
                return dia;
            }
        }
        return new int[]{-1, -1};
    }

    public boolean isFull() {
        for (SectionButton.Marker marker : this) {
            if (marker == SectionButton.Marker.NONE) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Integer> getEmptySquares(){
        ArrayList<Integer> emptySquares = new ArrayList<>(9);
        for(int i = 0; i < 9; i++){
            if(this.get(i) == SectionButton.Marker.NONE){
                emptySquares.add(i);
            }
        }
        return emptySquares;
    }

    public int difference(Board board){
        for(int i = 0; i < 9; i++){
            if(this.get(i) != board.get(i)){
                return i;
            }
        }
        return -1;
    }
}
