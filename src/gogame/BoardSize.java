package gogame;

import java.util.Arrays;

public enum BoardSize {
    NINE(9), THIRTEEN(13), NINETEEN(19);

    private final Integer size;
    BoardSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    public static BoardSize fromString(String s) {
        return Arrays.stream(values())
                .filter(e -> e.toString().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
    public static String[] getStringValues() {
        return Arrays.stream(values())
                .map(BoardSize::toString)
                .toArray(String[]::new);
    }
    public String toString() {
        return size + "x" + size;
    }
}
