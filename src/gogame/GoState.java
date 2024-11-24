package gogame;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class GoState implements Predicate<Point>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Integer blackCaptured;
    public Integer whiteCaptured;
    public Stone turn;
    public Set<GoState> previousStates;
    public BoardSpace[][] board;

    public GoState(int size) {
        this.blackCaptured = 0;
        this.whiteCaptured = 0;
        this.turn = Stone.BLACK;
        this.previousStates = new HashSet<>();
        this.board = new BoardSpace[size][size];

        Arrays.stream(board).forEach(row -> Arrays.fill(row, BoardSpace.EMPTY));
    }

    public GoState(GoState other) {
        int size = other.board.length;
        this.board = new BoardSpace[size][size];
        this.blackCaptured = other.blackCaptured;
        this.whiteCaptured = other.whiteCaptured;
        this.turn = other.turn;
        this.previousStates = new HashSet<>(other.previousStates);

        IntStream.range(0, size)
                .forEach(i -> IntStream.range(0, size)
                        .forEach(j -> this.board[i][j] = other.board[i][j]));
    }

    public Point[] getNeighbors(Point point){
        Point[] neighbours = new Point[]{
                new Point(point.x - 1, point.y),
                new Point(point.x, point.y + 1),
                new Point(point.x + 1, point.y),
                new Point(point.x, point.y - 1),
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
            else if (space.stone.equals(s)) {
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
            scanned.forEach(point -> board[point.x][point.y] = BoardSpace.EMPTY);
            if (color.equals(Stone.WHITE))
                blackCaptured += scanned.size() ;
            else whiteCaptured += scanned.size();
        }
    }

    public GoState placeStone(Point p){
        board[p.x][p.y] = BoardSpace.fromStone(turn);
        Arrays.stream(getNeighbors(p)).forEach(this::checkCaptured);
        return this;
    }

    public boolean isLegalMove(Point p) {
        if (!this.test(p) || !board[p.x][p.y].equals(BoardSpace.EMPTY)) return false;
        var isSuicide = getLiberties(turn, p, new HashSet<>()).length == 1;
        boolean isCapture = Arrays.stream(getNeighbors(p))
                .anyMatch(neighbor ->
                        board[neighbor.x][neighbor.y].stone != turn
                            && getLiberties(turn.opposite(), neighbor, new HashSet<>()).length == 1);
        if (isSuicide && !isCapture) {
            return false;
        }
        GoState newState = new GoState(this);
        newState.placeStone(p);
        return !previousStates.contains(newState);
    }
    public boolean makeMove(Point p){
        if (p == null) {
            previousStates.add(new GoState(this));
            turn = turn.opposite();
            return previousStates.contains(this);
        }
        if (!isLegalMove(p)) return false;
        placeStone(p);
        previousStates.add(new GoState(this));
        turn = turn.opposite();
        return false;
    }

    @Override
    public String toString() {
        return "Black Captured: " + blackCaptured + "\n" +
                "White Captured: " + whiteCaptured;
    }

    public void saveGame(File file) {
        try (var out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save game file to: " + file);
        }
    }
    public static GoState loadGame(File file) {
        try (var in = new ObjectInputStream(new FileInputStream(file))) {
            return (GoState) in.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load game file from: " + file);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoState goState = (GoState) o;
        return turn == goState.turn && Objects.deepEquals(board, goState.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, Arrays.deepHashCode(board));
    }
}

