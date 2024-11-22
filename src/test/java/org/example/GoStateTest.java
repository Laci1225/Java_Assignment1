package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GoStateTest {

    @ParameterizedTest
    @ArgumentsSource(NeighborsArgumentsProvider.class)
    public void testGetNeighbors(Point point, Point[] expected) {
        GoState game = new GoState(BoardSize.NINE);
        assertEquals(Arrays.toString(expected), Arrays.toString(game.getNeighbors(point)));
    }
    @ParameterizedTest
    @CsvSource({"""
            4,4,
            4,5,
            5,4,
            """})
    public void testIsLegalMove(int x, int y) {
        GoState game = new GoState(BoardSize.NINE);
        game.placeStone(new Point(4, 4));
        game.placeStone(new Point(5, 4));

        assertAll(
                () -> assertFalse(game.isLegalMove(new Point(4, 4))),
                () -> assertFalse(game.isLegalMove(new Point(5, 4))),
                () -> assertTrue(game.isLegalMove(new Point(4, 5)))
        );
    }
    @Test
    public void testIsLegalMoveSuicide() {
        GoState game = new GoState(BoardSize.NINE);
        game.placeStone(new Point(4, 3));
        game.placeStone(new Point(3, 4));
        game.placeStone(new Point(4, 5));
        game.placeStone(new Point(5, 4));
        game.setCurrentPlayer(game.getCurrentPlayer().opposite());
        assertFalse(game.isLegalMove(new Point(4, 4)));
    }
    @Test
    public void testIsLegalMoveRepeatedStates() {
        GoState game = new GoState(BoardSize.NINE);
        game.placeStone(new Point(4, 3));
        game.placeStone(new Point(3, 4));
        game.placeStone(new Point(4, 5));
        game.placeStone(new Point(5, 4));
        //todo
    }

    @Test
    public void testCheckCaptured(){
        var game = new GoState(BoardSize.NINE);
        game.placeStone(new Point(0, 1));
        game.placeStone(new Point(0, 2));
        game.placeStone(new Point(1, 0));
        game.placeStone(new Point(2, 1));
        game.placeStone(new Point(2, 2));

        game.setCurrentPlayer(game.getCurrentPlayer().opposite());
        game.placeStone(new Point(1, 1));
        game.placeStone(new Point(1, 2));
        game.setCurrentPlayer(game.getCurrentPlayer().opposite());
        game.placeStone(new Point(1, 3));
        game.checkCaptured(new Point(1, 3));
        assertEquals(game.getBlackCaptured(), 2);
        assertTrue(game.getBoard()[1][1] == BoardSpace.EMPTY);
        assertTrue(game.getBoard()[1][2] == BoardSpace.EMPTY);
    }

    @Test
    void testGetLiberties(){
        var game = new GoState(BoardSize.NINE);
        game.makeMove(new Point(0,0));
        assertArrayEquals(game.getLiberties(game.getCurrentPlayer().opposite(),new Point(0,0),new HashSet<>()),
                new Point[]{new Point(1,0), new Point(0,1)});
        assertArrayEquals(game.getLiberties(game.getCurrentPlayer().opposite(),new Point(1,0),new HashSet<>()),
                new Point[]{new Point(1,0), new Point(1,1),new Point(0,1), new Point(2,0)});
    }

    @Test
    void testMakeMove(){
        var game = new GoState(BoardSize.NINE);
        game.makeMove(new Point(0,0));
        assertTrue(game.makeMove(null));
        assertTrue(game.makeMove(null));
        assertFalse(game.makeMove(null));
    }
}
