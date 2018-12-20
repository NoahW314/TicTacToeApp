package com.example.tictactoe.CPU;

import android.app.Activity;

import com.example.tictactoe.GameManager;
import com.example.tictactoe.Views.SectionButton;

public abstract class CPU {

    public static final String TAG = "tictactoe.CPU";

    public Activity activity;
    public SectionButton.Marker ourMarker;
    public SectionButton.Marker theirMarker;

    public CPU(SectionButton.Marker marker, Activity activity){
        ourMarker = marker;
        theirMarker = SectionButton.Marker.getOtherMarker(ourMarker);
        this.activity = activity;
    }

    public abstract void play(GameManager gameManager);

    public static CPU[] fromPlayers(GameManager.Player[] players, Activity activity){
        CPU[] cpus = new CPU[players.length];

        for(int i = 0; i < players.length; i++) {
            switch (players[i]) {
                case Random_CPU:
                    cpus[i] = new RandomCPU(SectionButton.Marker.fromId(i+1), activity);
                    break;
                case Random_Plus_CPU:
                    cpus[i] = new RandomPlusCPU(SectionButton.Marker.fromId(i+1), activity);
                    break;
                default:
                    cpus[i] = null;
                    break;
            }
        }

        return cpus;
    }
}
