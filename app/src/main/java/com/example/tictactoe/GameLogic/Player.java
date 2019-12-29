package com.example.tictactoe.GameLogic;


import android.util.Log;

import com.example.tictactoe.Activities.BoardActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.tictactoe.Activities.BoardActivity.TAG;

public class Player {
    public static void registerName(String name){
        idsUsed.add(name);
    }
    public static List<String> idsUsed = new ArrayList<>();
    public static int[] toIntArray(List<String> players){
        int[] intArray = new int[players.size()];
        for(int i = 0; i < players.size(); i++){
            intArray[i] = idsUsed.indexOf(players.get(i));
        }
        return intArray;
    }
    public static List<String> fromIntArray(int[] intArr){
        List<String> players = new ArrayList<>(intArr.length);
        Log.i(TAG, Arrays.toString(intArr)+"  "+idsUsed);
        for(int i : intArr){
            Log.i(TAG, i+"");
            players.add(fromId(i));
        }
        return players;
    }
    public static String fromId(int id){
        return idsUsed.get(id);
    }
}
