package gogame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GoStateTest {

    @ParameterizedTest
    @ArgumentsSource(NeighborsArgumentsProvider.class)
    public void testGetNeighbors(Point point, Point[] expected) {
        GoState game = new GoState(9);
        assertEquals(Arrays.toString(expected), Arrays.toString(game.getNeighbors(point)));
    }
    @ParameterizedTest
    @CsvSource({"""
            4,5,
            6,5,
            7,4,
            """})
    public void testIsLegalMove(int x, int y) {
        GoState game = new GoState(9);
        assertTrue(game.isLegalMove(new Point(x, y)));
    }

    @ParameterizedTest
    @CsvSource({"""
            4,4,
            5,4,
            """})
    public void testIsLegalMoveFalse(int x, int y) {
        GoState game = new GoState(9);
        game.placeStone(new Point(4, 4));
        game.placeStone(new Point(5, 4));
        assertFalse(game.isLegalMove(new Point(x, y)));
    }

    @Test
    public void testIsLegalMoveSuicide() {
        GoState game = new GoState(9);
        game.placeStone(new Point(4, 3));
        game.placeStone(new Point(3, 4));
        game.placeStone(new Point(4, 5));
        game.placeStone(new Point(5, 4));
        game.turn = game.turn.opposite();
        assertFalse(game.isLegalMove(new Point(4, 4)));
    }

    @Test
    public void testIsLegalMoveSuicideButCapture() {
        GoState game = new GoState(9);
        game.placeStone(new Point(0,4));
        game.placeStone(new Point(0,6));
        game.placeStone(new Point(1,3));
        game.placeStone(new Point(1,5));
        game.turn = game.turn.opposite();
        game.placeStone(new Point(0,3));
        game.placeStone(new Point(1,4));
        game.placeStone(new Point(1,6));
        assertTrue(game.isLegalMove(new Point(0,5)));
    }

    @Test
    public void testIsLegalMoveRepeatedStates() {
        GoState game = new GoState(9);
        game.makeMove(new Point(0,4));
        game.makeMove(new Point(0,3));
        game.makeMove(new Point(0,6));
        game.makeMove(new Point(1,4));
        game.makeMove(new Point(1,3));
        game.makeMove(new Point(1,6));
        game.makeMove(new Point(1,5));
        game.makeMove(new Point(0,5));
        assertFalse(game.isLegalMove(new Point(0,4)));
    }

    @Test
    public void testCheckCaptured(){
        var game = new GoState(9);
        game.placeStone(new Point(0, 1));
        game.placeStone(new Point(0, 2));
        game.placeStone(new Point(1, 0));
        game.placeStone(new Point(2, 1));
        game.placeStone(new Point(2, 2));

        game.turn = game.turn.opposite();
        game.placeStone(new Point(1, 1));
        game.placeStone(new Point(1, 2));
        game.turn = game.turn.opposite();
        game.placeStone(new Point(1, 3));
        game.checkCaptured(new Point(1, 3));
        assertEquals(game.blackCaptured, 2);
        assertTrue(game.board[1][1] == BoardSpace.EMPTY);
        assertTrue(game.board[1][2] == BoardSpace.EMPTY);
    }

    @Test
    void testGetLiberties(){
        var game = new GoState(9);
        game.makeMove(new Point(0,0));
        assertArrayEquals(game.getLiberties(game.turn.opposite(),new Point(0,0),new HashSet<>()),
                new Point[]{new Point(1,0), new Point(0,1)});
        assertArrayEquals(game.getLiberties(game.turn.opposite(),new Point(1,0),new HashSet<>()),
                new Point[]{new Point(1,0), new Point(1,1),new Point(0,1), new Point(2,0)});
    }

    @Test
    void testMakeMove(){
        var game = new GoState(9);
        game.makeMove(new Point(0,0));
        assertFalse(game.makeMove(null));
        game.makeMove(new Point(0,1));
        assertFalse(game.makeMove(null));
        assertTrue(game.makeMove(null));
    }

    @Test
    void testSaveLoadGame() throws IOException {
        GoState game = new GoState(5);
        game.placeStone(new Point(1, 1));
        File file = File.createTempFile("go_game", ".go");
        game.saveGame(file);
        GoState loadedState = GoState.loadGame(file);
        assertEquals(game, loadedState);
        file.deleteOnExit();
    }

}
