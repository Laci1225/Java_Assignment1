package gogame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testGetStringValues() {
        assertArrayEquals(new String[]{"9x9", "13x13", "19x19"}, BoardSize.getStringValues());
    }

    @Test
    public void testFromStringFails() {
        assertThrows(IllegalArgumentException.class,
                () -> BoardSize.fromString("8X8"));
    }
}
