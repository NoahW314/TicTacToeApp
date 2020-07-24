package com.example.tictactoe.CPU;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.List;

public abstract class CPU {

    public static final String TAG = "tictactoe.CPU";

    protected SectionButton.Marker ourMarker;
    protected SectionButton.Marker theirMarker;
    protected volatile Boolean terminate = false;
    protected String playerType;

    public CPU(SectionButton.Marker marker, String playerType){
        ourMarker = marker;
        theirMarker = SectionButton.Marker.getOtherMarker(ourMarker);
        this.playerType = playerType;
    }

    public abstract int play(Board board) throws CPUTerminatedException;
    public void terminate(){
        terminate = true;
    }
    public void restart(){terminate = false;}
    protected void throwIfTerminated()throws CPUTerminatedException {if(terminate) throw new CPUTerminatedException();}
    public String getPlayerType(){return playerType;}
    public boolean isHuman(){return false;}
    public SectionButton.Marker getOurMarker() { return ourMarker; }
    public SectionButton.Marker getTheirMarker() { return theirMarker; }

    public static CPU[] fromPlayers(List<String> players){
        CPU[] cpus = new CPU[players.size()];

        for(int i = 0; i < players.size(); i++) {
            switch (players.get(i)) {
                /*Cases should match the items in the player_options.xml file in the res/values folder*/
                case "Random CPU":
                    cpus[i] = new RandomCPU(SectionButton.Marker.fromId(i+1), players.get(i));
                    break;
                case "Random Plus CPU":
                    cpus[i] = new RandomPlusCPU(SectionButton.Marker.fromId(i+1), players.get(i));
                    break;
                case "Random++ CPU":
                    cpus[i] = new RandomPlusPlusCPU(SectionButton.Marker.fromId(i+1), players.get(i));
                    break;
                case "Random Plus Three CPU":
                    cpus[i] = new RandomPlusThreeCPU(SectionButton.Marker.fromId(i+1), players.get(i));
                    break;
                case "Monte Carlo CPU V1":
                    cpus[i] = new MonteCarloCPU(SectionButton.Marker.fromId(i+1), players.get(i),1,1,1,1,1,1,1,1,1);
                    break;
                case "Monte Carlo CPU V2":
                    cpus[i] = new MonteCarloCPU(SectionButton.Marker.fromId(i+1), players.get(i),5,1,1,1,1,1,1,1,1);
                    break;
                case "Monte Carlo CPU V3":
                    cpus[i] = new MonteCarloCPU(SectionButton.Marker.fromId(i+1), players.get(i),2,2,2,2,2,2,2,2,2);
                    break;
                case "Monte Carlo CPU V4":
                    cpus[i] = new MonteCarloCPU(SectionButton.Marker.fromId(i+1), players.get(i),3,3,3,3,3,3,3,3,3);
                    break;
                case "Monte Carlo CPU V5":
                    cpus[i] = new MonteCarloCPU(SectionButton.Marker.fromId(i+1), players.get(i),9,9,9,9,9,9,9,9,9);
                    break;
                case "Monte Carlo CPU Predictive V1":
                    cpus[i] = new MonteCarloPredictiveCPU(SectionButton.Marker.fromId(i+1), players.get(i), 2,2,2,2,2,2,2,2,2,2);
                    break;
                case "Monte Carlo CPU Predictive V2":
                    cpus[i] = new MonteCarloPredictiveCPU(SectionButton.Marker.fromId(i+1), players.get(i), 3,3,3,3,3,3,3,3,3,3);
                    break;
                case "Monte Carlo CPU Predictive V3":
                    cpus[i] = new MonteCarloPredictiveCPU(SectionButton.Marker.fromId(i+1), players.get(i), 5,5,5,5,5,5,5,5,5,5);
                    break;
                case "Monte Carlo CPU Predictive V4":
                    cpus[i] = new MonteCarloPredictiveCPU(SectionButton.Marker.fromId(i+1), players.get(i), 9,8,3,3,3,3,3,3,3,3);
                    break;
                case "Monte Carlo CPU Predictive V5":
                    cpus[i] = new MonteCarloPredictiveCPU(SectionButton.Marker.fromId(i+1), players.get(i), 9,9,9,9,9,9,9,9,9,9);
                    break;
                case "Human":
                    cpus[i] = new Human(SectionButton.Marker.fromId(i+1), players.get(i));
                    break;
                default:
                    cpus[i] = null;
                    break;
            }
        }
        for(int i = 0; i < cpus.length; i++){
            if(cpus[i] instanceof MonteCarloPredictiveCPU){
                ((MonteCarloPredictiveCPU)cpus[i]).setOtherPlayer(cpus[(i+1)%2]);
            }
        }

        return cpus;
    }
}
