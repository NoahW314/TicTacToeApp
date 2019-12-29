package com.example.tictactoe.GameLogic;

import android.app.Activity;

import com.example.tictactoe.CPU.CPU;
import com.example.tictactoe.Views.SectionButton;

public interface GameManager {
    String TAG = "tictactoe.GameManager";

    void setCPUs(CPU[] cpus);
    CPU getCPU(int i);
    void setStatMode(boolean statMode);
    boolean inStatMode();
    void start(Activity activity);
    void restart(Activity activity);
    boolean isHumanTurn();
    void humanPlay(int index);
    GameStatus getGameStatus();
    SectionButton.Marker getBoardSectionMarker(int sectionNum);


    enum GameStatus{IN_PROGRESS, WON_1, WON_2, CATS_GAME}
        /*Human(0),
        Random_CPU(1),
        Random_Plus_CPU(2),
        Monte_Carlo_CPU_V1(3),
        Monte_Carlo_CPU_V2(4),
        Monte_Carlo_CPU_V3(5),
        Monte_Carlo_CPU_V4(6),
        Monte_Carlo_CPU_V5(7),
        Monte_Carlo_CPU_Predictive(8)*/
}
