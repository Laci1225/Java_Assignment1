package org.example;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class GoState implements Predicate<Point>, Serializable {

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
            if (scanned.contains(current)) continue;
            BoardSpace space = board[current.x][current.y];
            if(space == BoardSpace.EMPTY) {
                liberties.add(current);
                if (current.equals(p)){
                    toScan.addAll(Arrays.asList(getNeighbors(current)));
                }
            }
            else if (board[current.x][current.y].stone.equals(s)) {
                scanned.add(current);
                toScan.addAll(Arrays.asList(getNeighbors(current)));
            }
        }
        return liberties.toArray(Point[]::new);
    }
    public void checkCaptured(Point p){
        var color = board[p.x][p.y].stone;
        var scanned = new HashSet<Point>();
        var liberties = getLiberties(color, p, scanned);
        if (liberties.length == 0) {
            for (Point point : scanned) {
                board[point.x][point.y] = BoardSpace.EMPTY;
            }
            if (color.equals(Stone.WHITE))
                blackCaptured += scanned.size();
            else whiteCaptured += scanned.size();
        }
    }

    public GoState placeStone(Point p){
        board[p.x][p.y] = BoardSpace.fromStone(currentPlayer);
        Arrays.stream(getNeighbors(p)).forEach(this::checkCaptured);
        return this;
    }

    public boolean isLegalMove(Point p){
        var a =  this.test(p) && board[p.x][p.y].equals(BoardSpace.EMPTY);
        var b = Arrays.stream(getNeighbors(p))
                .anyMatch(
                point-> board[point.x][point.y].stone != currentPlayer
                && getLiberties(currentPlayer, p, new HashSet<>()).length == 1
        );
        if (!a && !b) return false;
        GoState newState = new GoState(this);
        newState = newState.placeStone(p);
        return !previousStates.contains(newState);
    }

    public boolean makeMove(Point p){
        if (p == null) {
            previousStates.add(new GoState(this));
            currentPlayer = currentPlayer.opposite();
            return previousStates.contains(new GoState(this));
        }
        if (!isLegalMove(p)) return false;
        previousStates.add(new GoState(this));
        placeStone(p);
        currentPlayer = currentPlayer.opposite();
        return false;
    }

    @Override
    public String toString() {
        return "Black Captured: " + blackCaptured + "\n" +
                "White Captured: " + whiteCaptured;
    }

    public GoState saveGame(String filename) {
        try (var out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
            return this;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save game file to: " + filename);
        }
    }
    public static GoState loadGame(String filename) {
        try (var in = new ObjectInputStream(new FileInputStream(filename))) {
            return (GoState) in.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load game file from: " + filename);
        }
    }
}

