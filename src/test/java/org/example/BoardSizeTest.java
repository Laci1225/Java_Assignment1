package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardSizeTest {

    @ParameterizedTest
    @CsvSource({"""
            NINE, 9x9,
            THIRTEEN, 13x13,
            NINETEEN, 19x19,
            """
    })
    public void testFromString(BoardSize boardSize, String name) {
        assertEquals(boardSize, BoardSize.fromString(name));
    }

    @Test
    public void testFailingFromString() {
        assertThrows(IllegalArgumentException.class,
                () -> BoardSize.fromString("8X8"));
    }
}
