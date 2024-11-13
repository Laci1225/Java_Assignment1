package org.example;

import java.util.*;
import java.util.function.Predicate;

public class GoState implements Predicate<Point> {

    Integer blackCaptured;
    Integer whiteCaptured;
    Stone currentPlayer;
    Set<GoState> previousStates;
    BoardSpace[][] board;

    public GoState(Integer size) {
        this.blackCaptured = 0;
        this.whiteCaptured = 0;
        this.currentPlayer = Stone.BLACK;
        this.previousStates = new HashSet<>();
        this.board = new BoardSpace[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.board[i][j] = BoardSpace.EMPTY;
            }
        }
    }

    public GoState(GoState other) {
        int size = other.board.length;
        this.board = new BoardSpace[size][size];
        this.blackCaptured = other.blackCaptured;
        this.whiteCaptured = other.whiteCaptured;
        this.currentPlayer = other.currentPlayer;
        this.previousStates = new HashSet<>(other.previousStates);

        for (int i = 0; i < size; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, size);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoState goState = (GoState) o;
        return currentPlayer == goState.currentPlayer && Objects.deepEquals(board, goState.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPlayer, Arrays.deepHashCode(board));
    }

    public Point[] getNeighbors(Point point){
        Point[] neighbours = new Point[]{
                new Point(point.x + 1, point.y),
                new Point(point.x, point.y + 1),
                new Point(point.x - 1, point.y),
                new Point(point.x, point.y - 1)
        };
        return Arrays.stream(neighbours).filter(this::test).toArray(Point[]::new);
    }

    @Override
    public boolean test(Point point) {
        return point.x >= 0 && point.x < board.length && point.y >= 0 && point.y < board.length;
    }

    public Point[] getLiberties(Stone s, Point p, Set<Point> scanned){
        Deque<Point> toScan = new LinkedList<>();
        toScan.add(p);
        Set<Point> liberties = new HashSet<>();
        while (!toScan.isEmpty()) {
            Point current = toScan.pop();
            if (board[current.x][current.y].stone.equals(BoardSpace.EMPTY.stone)) {
                liberties.add(current);
            } else if (board[current.x][current.y].stone.equals(s)) {
                scanned.add(current);
                toScan.addAll(Arrays.asList(getNeighbors(current)));
            }
        }
        return liberties.toArray(Point[]::new);
    }
    public void checkCaptured(Point p){
        if (getLiberties(Stone.BLACK,p, new HashSet<>()).length == 0){
            board[p.x][p.y] = BoardSpace.EMPTY;
            whiteCaptured++;
        }
        if (getLiberties(Stone.WHITE,p, new HashSet<>()).length == 0){
            board[p.x][p.y] = BoardSpace.EMPTY;
            blackCaptured++;
        }
    }

    public GoState placeStone(Point p){
        var currentPlayer = this.currentPlayer;
        board[p.x][p.y] = BoardSpace.fromStone(currentPlayer);
        var neighbours = getNeighbors(p);
        Arrays.stream(neighbours).forEach(this::checkCaptured);
        return this;
    }

    public boolean isLegalMove(Point p){
        var a =  this.test(p) && board[p.x][p.y].equals(BoardSpace.EMPTY);
        var b = getLiberties(currentPlayer, p, new HashSet<>()).length == 1
                && Arrays.stream(getNeighbors(p)).anyMatch(
                        pr-> board[pr.x][pr.y].stone.equals(currentPlayer)
        );
        if (!a && !b) return false;
        GoState newState = new GoState(this);
        newState.placeStone(p);
        return !previousStates.contains(newState);
    }

    public boolean makeMove(Point p){
        // todo
        if (!isLegalMove(p)) return false;
        previousStates.add(new GoState(this));
        placeStone(p);
        currentPlayer = currentPlayer.opposite();
        return true;
    }

    @Override
    public String toString() {
        return "Black Captured: " + blackCaptured + "\n" +
                "White Captured: " + whiteCaptured;
    }
}

