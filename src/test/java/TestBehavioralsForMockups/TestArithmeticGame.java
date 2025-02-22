package TestBehavioralsForMockups;

import AbstractBehavioralTests.TestGameDefinition;
import Mockups.IntegerMove;
import Mockups.SimpleArithmeticGameDefinition;
import Mockups.SimpleArithmeticPuzzleState;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.ArrayList;
import java.util.Collection;

public class TestArithmeticGame extends TestGameDefinition<IntegerMove, SimpleArithmeticPuzzleState> {
    protected TestArithmeticGame() {
        super(new SimpleArithmeticGameDefinition());
    }

    @Override
    protected Collection<TestCase> generateTestcases() {
        Collection<TestCase> result = new ArrayList<>();

        SimpleArithmeticPuzzleState acceptableState = new SimpleArithmeticPuzzleState(10, 10);
        SimpleArithmeticPuzzleState notAcceptableState = new SimpleArithmeticPuzzleState(10, 1);

        IntegerMove legalMove1 = new IntegerMove(3);
        IntegerMove legalMove2 = new IntegerMove(-2);
        IntegerMove illegalMove = new IntegerMove(9);

        result.add(new TestCase(acceptableState, legalMove1, initialStateIsAcceptable));
        result.add(new TestCase(notAcceptableState, legalMove2, initialStateIsNotAcceptable));
        result.add(new TestCase(notAcceptableState, illegalMove, TestGameDefinition.illegalMove));
        return result;
    }
}
