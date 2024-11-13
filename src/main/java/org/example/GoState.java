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

    public Point[] getNeighbors(){
        Point point = new Point(1,1);
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
}
