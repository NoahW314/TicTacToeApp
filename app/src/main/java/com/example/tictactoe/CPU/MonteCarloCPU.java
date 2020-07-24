package com.example.tictactoe.CPU;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.GameLogic.GameManager;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonteCarloCPU extends CPU {

    public int[] pathNums;
    private static Map<Board, Double> parseBoardScoreMap(String str){
        Map<Board, Double> map = new HashMap<>(5478);
        str = str.substring(2, str.length()-1);
        String[] pairs = str.split(", \\[");
        //System.out.println(Arrays.asList(pairs));

        for(String stringPair : pairs) {
            String[] pair = stringPair.split("=");
            double value = Double.valueOf(pair[1]);
            Board key = new Board();
            pair[0] = pair[0].substring(0, pair[0].length() - 1);
            String[] markers = pair[0].split(", ");
            for (int i = 0; i < 9; i++) {
                key.set(i, SectionButton.Marker.valueOf(markers[i]));
            }

            map.put(key, value);
        }

        return map;
    }


    public MonteCarloCPU(SectionButton.Marker marker, String playerType, int...pathNum){
        super(marker, playerType);
        this.pathNums = pathNum;
    }

    @Override
    public int play(Board oldBoard) throws CPUTerminatedException{
        Board board = new Board(oldBoard);
        ArrayList<Double> scores = new ArrayList<>(9);

        for(int i = 0; i < 9; i++){
            scores.add(null);
            if(board.get(i) == SectionButton.Marker.NONE){
                String pat = i+" ";
                PathData pathData = investigatePaths(i, board, Turn.OURS, pat, pathNums);
                scores.set(i, pathData.score);
            }
        }

        int index = 0;
        double maxValue = 0;
        for(int i = 0; i < scores.size(); i++){
            if(scores.get(i) != null && scores.get(i) >= maxValue){
                index = i;
                maxValue = scores.get(i);
            }
        }
        return index;
    }

    protected PathData investigatePaths(int index, Board board, Turn turn, String pat, int... paths) throws CPUTerminatedException{
        throwIfTerminated();

        Board newBoard = new Board(board);

        if(turn.equals(Turn.OURS)) { newBoard.set(index, ourMarker); }
        else{newBoard.set(index, theirMarker);}

        //did someone win
        int[] threeInARow = newBoard.findThree();
        if(threeInARow[0] != -1){
            if(turn.equals(Turn.OURS)){ return new PathData(1, pat, Turn.OURS);}
            else{ return new PathData(0, pat, Turn.THEIRS); }
        }

        ArrayList<Integer> emptySquares = newBoard.getEmptySquares();

        if(emptySquares.size() == 0){
            //cats game
            return new PathData(0.5, pat, null);
        }

        PathData score = new PathData(0, pat, null);

        int entryCount = Math.min(emptySquares.size(), paths[0]);
        int[] newPaths = new int[paths.length-1];
        System.arraycopy(paths, 1, newPaths, 0, paths.length-1);

        throwIfTerminated();

        for(int i = 0; i < entryCount; i++){
            int indexToPlayNext = generateNextIndex(board, turn, emptySquares);
            pat+=(indexToPlayNext+" ");
            PathData p = investigatePaths(indexToPlayNext, newBoard, turn.switchTurn(), pat, newPaths);
            score.add(p);
            emptySquares.remove(Integer.valueOf(indexToPlayNext));
        }

        throwIfTerminated();

        return score.div(entryCount);
    }

    protected int generateNextIndex(Board board, Turn turn, ArrayList<Integer> emptySquares) throws CPUTerminatedException{
        return emptySquares.get((int)Math.floor(Math.random()*emptySquares.size()));
    }

    protected enum Turn{OURS, THEIRS;
        public Turn switchTurn(){
            if(this.equals(OURS)){ return THEIRS; }
            else {return OURS;}
        }
    }

    protected class PathData {
        double score;
        String pat;
        Turn winner;

        public PathData(double score, String pat, Turn winner){
            this.score = score;
            this.pat = pat;
            this.winner = winner;
        }

        public void add(PathData pd){
            this.score+=pd.score;
            this.pat = pd.pat;
            this.winner = pd.winner;
        }
        public PathData div(int scalar){
            return new PathData(this.score/scalar, this.pat, this.winner);
        }
    }
}
