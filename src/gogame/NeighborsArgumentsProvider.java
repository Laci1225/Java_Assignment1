package gogame;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class NeighborsArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(new Point(0, 0), new Point[]{new Point(0, 1), new Point(1, 0)}),
                Arguments.of(new Point(0, 8), new Point[]{new Point(1, 8), new Point(0, 7)}),
                Arguments.of(new Point(8, 0), new Point[]{new Point(7, 0), new Point(8, 1)}),
                Arguments.of(new Point(8, 8), new Point[]{new Point(7, 8), new Point(8, 7)}),
                Arguments.of(new Point(0, 4), new Point[]{new Point(0, 5), new Point(1, 4), new Point(0, 3)}),
                Arguments.of(new Point(4, 0), new Point[]{new Point(3, 0), new Point(4, 1), new Point(5, 0)}),
                Arguments.of(new Point(4, 8), new Point[]{new Point(3, 8), new Point(5, 8), new Point(4, 7)}),
                Arguments.of(new Point(8, 4), new Point[]{new Point(7, 4), new Point(8, 5), new Point(8, 3)}),
                Arguments.of(new Point(4, 4), new Point[]{new Point(3, 4), new Point(4, 5), new Point(5, 4), new Point(4, 3)})
        );
    }
}
