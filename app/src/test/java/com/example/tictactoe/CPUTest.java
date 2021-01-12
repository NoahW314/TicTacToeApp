package com.example.tictactoe;

import com.example.tictactoe.CPU.TreeCPU;
import com.example.tictactoe.Views.SectionButton;

public class CPUTest {
    public static void main(String[] args){
        TreeCPU cpu = new TreeCPU(SectionButton.Marker.O, "Tree CPU");
        TreeCPU cpu2 = new TreeCPU(SectionButton.Marker.X, "Tree CPU");
    }
}
