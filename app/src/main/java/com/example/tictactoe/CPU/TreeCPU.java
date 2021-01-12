package com.example.tictactoe.CPU;

import android.util.Log;

import com.example.tictactoe.Board;
import com.example.tictactoe.CPUTerminatedException;
import com.example.tictactoe.Views.SectionButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeCPU extends CPU {
    protected boolean log;
    // A map between a given board and all its possible child boards
    protected Map<Board, Set<Board>> children = new HashMap<>(5478);
    // The set of all boards who have at least one child in ourTrapBoards
    protected Set<Board> ourParentBoards = new HashSet<>(1830);
    // The set of all boards which they would be stuck in (could be forced to lose from)
    protected Set<Board> theirTrapBoards = new HashSet<>(1106);
    // The set of all boards who have at least one child in theirTrapBoards
    protected Set<Board> theirParentBoards = new HashSet<>(1830);
    // The set of all boards which we would be stuck in (could be forced to lose from)
    protected Set<Board> ourTrapBoards = new HashSet<>(1106);
    // The algorithm that controls what move to make when we have no obvious move
    protected TreeSubCPU bestMoveFinder;


    public TreeCPU(SectionButton.Marker marker, String playerType) {
        this(marker, playerType, true);
    }
    public TreeCPU(SectionButton.Marker marker, String playerType, boolean log){
        super(marker, playerType);
        this.log = log;
        setUpTree();
        bestMoveFinder = new TreeSubCPU(marker, playerType, log);
    }

    protected void setUpTree() {
        // The set of all boards that can be reached from the blank starting board
        Set<Board> allBoards = new HashSet<>(5478);
        // The boards that represent a game that is over where we lost
        Set<Board> ourEndBoards = new HashSet<>(626);
        // The boards that represent a game that is over where they lost
        Set<Board> theirEndBoards = new HashSet<>(626);

        /*Find all possible boards*/
        Set<Board> nextLevel = new HashSet<>(1680);
        SectionButton.Marker currentMarker = SectionButton.Marker.O;
        nextLevel.add(new Board());

        while (!nextLevel.isEmpty()) {
            Set<Board> level = new HashSet<>(nextLevel);
            allBoards.addAll(nextLevel);
            nextLevel.clear();
            for (Board board : level) {
                if (board.findThree()[0] != -1) {
                    if (currentMarker == theirMarker) {
                        theirEndBoards.add(board);
                    }
                    else {
                        ourEndBoards.add(board);
                    }
                }
                else if(!board.isFull()){
                    // Generate Children
                    Set<Board> children = new HashSet<>(9);
                    List<Integer> emptySquares = board.getEmptySquares();
                    for (Integer empty : emptySquares) {
                        Board newBoard = new Board(board);
                        newBoard.set(empty, currentMarker);
                        children.add(newBoard);
                    }
                    this.children.put(board, children);
                    nextLevel.addAll(children);
                }
            }
            currentMarker = SectionButton.Marker.getOtherMarker(currentMarker);
        }
        ourTrapBoards.addAll(ourEndBoards);
        theirTrapBoards.addAll(theirEndBoards);


        /*Classify all boards that we can*/
        for (int i = 0; i < 9; i++) {
            // Here we are looking for parent boards
            if (i % 2 == 0) {
                for (Board board : allBoards) {
                    // Any boards who have a child that is a trap board are parent boards
                    Set<Board> tempKids = children.get(board);
                    if (tempKids == null) continue;
                    Set<Board> kids = new HashSet<>(tempKids);
                    kids.retainAll(ourTrapBoards);
                    if (!kids.isEmpty()) {
                        ourParentBoards.add(board);
                    }

                    // Any boards who have a child that is a trap board are parent boards
                    kids = new HashSet<>(tempKids);
                    kids.retainAll(theirTrapBoards);
                    if (!kids.isEmpty()) {
                        theirParentBoards.add(board);
                    }
                }
            }
            // Here we are looking for trap boards
            else {
                for (Board board : allBoards) {
                    // A board whose children are all parent boards is a trap board
                    Set<Board> kids = children.get(board);
                    if (kids == null) continue;
                    if (ourParentBoards.containsAll(kids)) {
                        ourTrapBoards.add(board);
                    }

                    // A board whose children are all parent boards is a trap board
                    if (theirParentBoards.containsAll(kids)) {
                        theirTrapBoards.add(board);
                    }
                }
            }

            allBoards.removeAll(ourTrapBoards);
            allBoards.removeAll(ourParentBoards);
            allBoards.removeAll(theirTrapBoards);
            allBoards.removeAll(theirParentBoards);
        }
    }

    @Override
    public int play(Board board) throws CPUTerminatedException {
        Set<Board> possibleMoves = children.get(board);
        // We never want to place our opponent in one of our parent boards, since they could move us
        // to a trap board, so we remove all those options
        if(possibleMoves == null) throw new IllegalStateException("Our current board has no child boards.  The game should be over.");
        possibleMoves = new HashSet<>(possibleMoves);
        possibleMoves.removeAll(ourParentBoards);
        // If we have no moves other than those, then choose the "best" option from among these
        if(possibleMoves.isEmpty()){
            throwIfTerminated();
            return bestMoveFinder.play(board);
        }

        // If there are any moves that place our opponent in a trap board, then do that
        Set<Board> trapThemMoves = new HashSet<>(possibleMoves);
        trapThemMoves.retainAll(theirTrapBoards);
        if(!trapThemMoves.isEmpty()){
            Board nextBoard = trapThemMoves.iterator().next();
            int move = nextBoard.difference(board);
            throwIfTerminated();
            if(move == -1){
                throw new IllegalArgumentException("There is no difference between these two boards!");
            }
            if(log) Log.d(TAG, "Trapping them with "+move);
            return move;
        }

        // If we can't trap them, then choose the next "best" option
        int move = bestMoveFinder.play(board);
        Board newBoard = new Board(board);
        newBoard.set(move, ourMarker);
        Set<Board> kids = children.get(board);
        if(!kids.contains(newBoard)){
            throw new IllegalStateException("The move suggested by our move finder CPU is not listed as a child!");
        }
        if(ourParentBoards.contains(newBoard)){
            if(log) Log.d(TAG, "Our move finder wants to lead us into a trap!");
            possibleMoves = new HashSet<>(kids);
            possibleMoves.removeAll(ourParentBoards);
            Board nextBoard = possibleMoves.iterator().next();
            int rMove = nextBoard.difference(board);
            throwIfTerminated();
            if(rMove == -1) throw new IllegalArgumentException("There is no difference between these two boards!");
            if(rMove == move) throw new IllegalStateException("I thought the move "+rMove+" would lead us into a trap!");
            if(log) Log.d(TAG, "Non-trap Random Move at "+rMove);
            return rMove;
        }
        throwIfTerminated();
        return move;
    }

    private class TreeSubCPU extends CPU{

        protected boolean log;

        public TreeSubCPU(SectionButton.Marker marker, String playerType, boolean log) {
            super(marker, playerType);
            this.log = log;
        }

        @Override
        public int play(Board board) throws CPUTerminatedException {
            Set<Board> possibleMoves = new HashSet<>(children.get(board));
            // Remove all the moves that place them in a parent board, because otherwise, they might trap us
            possibleMoves.removeAll(ourParentBoards);
            // If we have no other choice, then choose a good option from among the ourParentBoards
            if(possibleMoves.isEmpty()) {
                possibleMoves = new HashSet<>(children.get(board));
            }
            // If we only have one possible move, then we have to choose it
            if(possibleMoves.size() == 1){
                return possibleMoves.iterator().next().difference(board);
            }
            // If we have a choice, then don't go with an ourParentBoard
            Map<Board, Double> scores = new HashMap<>(possibleMoves.size());
            RandomPlusCPU mockOpponent = new RandomPlusCPU(theirMarker, "Random CPU", false);
            for(Board move : possibleMoves){
                if(move.isFull()) continue;
                int oppMove = mockOpponent.play(move, true);
                // This is the set of all their move options
                Set<Board> kids;
                // If they play randomly, then they could play on any space, so the score is based on all their options
                if(mockOpponent.strategy == RandomPlusCPU.Strategy.RANDOM){
                    kids = new HashSet<>(children.get(move));
                }
                // Otherwise, they are either blocking us or winning, so they will always choose that spot
                // Thus, we base the score on that move
                else {
                    Board toScore = new Board(move);
                    toScore.set(oppMove, theirMarker);
                    kids = new HashSet<>(1);
                    kids.add(toScore);
                }
                double score = -1;
                if(kids.isEmpty()) throw new IllegalStateException("I thought we were trapped.  How can they not have any options?!");
                for(Board kid : kids){
                    // if this move places us in a trap board, then don't choose that move
                    if(ourTrapBoards.contains(kid)){
                        score = 0;
                    }
                    // if this move allows us to trap them, then definitely choose that move
                    else if(theirParentBoards.contains(kid)){
                        score = 1;
                    }
                    // neutral board, neutral score
                    else {
                        score = 0.5;
                    }
                }
                scores.put(move, score);
            }
            double maxScore = 0;
            Board bestBoard = null;
            for(Map.Entry<Board, Double> score : scores.entrySet()){
                if(score.getValue() == -1) throw new IllegalStateException("How can we have a negative score!");
                if(score.getValue() >= maxScore){
                    maxScore = score.getValue();
                    bestBoard = new Board(score.getKey());
                }
            }
            throwIfTerminated();
            if(bestBoard == null) throw new IllegalStateException("No board is best?!");
            return bestBoard.difference(board);
        }
    }
}
