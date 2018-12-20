package com.example.tictactoe;

import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;

public class Board extends ArrayList<SectionButton.Marker> {
    public Board(){
        super(9);
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

    public boolean isFull(){
        for(SectionButton.Marker marker: this){
            if(marker == SectionButton.Marker.NONE){return false;}
        }
        return true;
    }
}
