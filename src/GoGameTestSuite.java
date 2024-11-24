import gogame.BoardSizeTest;
import gogame.GoStateTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses({
	BoardSizeTest.class,
    GoStateTest.class,
})
@Suite public class GoGameTestSuite {}
