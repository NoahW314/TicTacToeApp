package com.example.tictactoe;


import com.example.tictactoe.GameLogic.Turn;
import com.example.tictactoe.Views.SectionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test {
    public static SectionButton.Marker[] turnToMarker = new SectionButton.Marker[]{SectionButton.Marker.O, SectionButton.Marker.X};
    public static void main(String[] args){
        Map<Board, Double> boardToScore;
        //turn is swapped since no one plays the first time
        Data d = investigatePaths(-1, new Board(), Turn.PLAYER_2, Turn.PLAYER_1);
        boardToScore = new HashMap<>(d.map);

        String str = boardToScore.toString();
        System.out.println(str);
        /*Map<Board, Double> parsedMap = parseBoardScoreMap(str);
        String parsedString = parsedMap.toString();
        System.out.println(parsedString);
        System.out.println(parsedMap.equals(boardToScore));
        System.out.println(parsedString.equals(str));*/
    }
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

    private static Set<Board> generateAllBoards(){
        Set<Board> boards = new HashSet<>(5478);
        boards.add(new Board());
        Set<Board> prevLevel = new HashSet<>(boards);

        Turn turn = Turn.PLAYER_1;
        for(int i = 0; i < 9; i++) {
            Set<Board> currentLevel = generateNextLevelOfBoards(prevLevel, turn);
            boards.addAll(currentLevel);
            turn = turn.switchTurn();
            prevLevel = new HashSet<>(currentLevel);
            System.out.println(i);
        }

        return boards;
    }
    private static Set<Board> generateNextLevelOfBoards(Set<Board> boards, Turn turn){
        Set<Board> nextLevel = new HashSet<>(750);
        for(Board b : boards) {
            if(isOver(b)) continue;

            //count the empty squares
            ArrayList<Integer> emptySquares = new ArrayList<>(9);
            for(int i = 0; i < 9; i++){
                if(b.get(i) == SectionButton.Marker.NONE){
                    emptySquares.add(i);
                }
            }

            for(Integer index : emptySquares){
                Board board = new Board(b);
                board.set(index, turnToMarker[turn.id]);
                nextLevel.add(board);
            }
        }
        return nextLevel;
    }

    private static boolean isOver(Board board){
        //did someone win
        int[] threeInARow = board.findThree();

        return threeInARow[0] != -1 || board.isFull();
    }

    private static Data investigatePaths(int index, Board board, Turn turn, Turn player) {
        Board newBoard = new Board(board);

        if(index != -1) {
            newBoard.set(index, turnToMarker[turn.id]);
        }

        //did someone win
        int[] threeInARow = newBoard.findThree();
        if(threeInARow[0] != -1){
            if(turn == player){ return new Data(createMap(new Board(newBoard), 1d), 1d);}
            else{ return new Data(createMap(new Board(newBoard), 0d), 0d);}
        }

        //count the empty squares
        ArrayList<Integer> emptySquares = new ArrayList<>(9);
        for(int i = 0; i < 9; i++){
            if(newBoard.get(i) == SectionButton.Marker.NONE){
                emptySquares.add(i);
            }
        }

        if(emptySquares.size() == 0){
            //cats game
            return new Data(createMap(new Board(newBoard), 0.5), 0.5);
        }

        double score = 0;
        Map<Board, Double> map = new HashMap<>(600*emptySquares.size());
        int entryCount = emptySquares.size();

        for(int i = 0; i < entryCount; i++){
            int indexToPlayNext = emptySquares.get(i);
            Data d = investigatePaths(indexToPlayNext, newBoard, turn.switchTurn(), player);
            map.putAll(d.map);
            score+=d.score;
        }
        map.put(new Board(newBoard), score/entryCount);

        return new Data(map, score/entryCount);
    }

    private static class Data{
        Map<Board, Double> map;
        double score;

        private Data(Map<Board, Double> map, double score) {
            this.map = map;
            this.score = score;
        }
    }

    private static<K, V> Map<K, V> createMap(K key, V value){
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
