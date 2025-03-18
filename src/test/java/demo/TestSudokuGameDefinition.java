package demo;

import AbstractBehavioralTests.TestGameDefinition;
import pawz.demo.SudokuGameDefinition;
import pawz.demo.SudokuMove;
import pawz.demo.SudokuState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSudokuGameDefinition extends TestGameDefinition<SudokuMove, SudokuState> {

    protected TestSudokuGameDefinition() {
        super(SudokuGameDefinition.getInstance());
    }

    private static SudokuState createAcceptableSolvedBoard(){
        int[] fields = {
                1, 2, 3,    4, 5, 6,    7, 8, 9,
                4, 5, 6,    7, 8, 9,    1, 2, 3,
                7, 8, 9,    1, 2, 3,    4, 5, 6,

                2, 3, 1,    5, 6, 4,    8, 9, 7,
                5, 6, 4,    8, 9, 7,    2, 3, 1,
                8, 9, 7,    2, 3, 1,    5, 6, 4,

                3, 1, 2,    6, 4, 5,    9, 7, 8,
                6, 4, 5,    9, 7, 8,    3, 1, 2,
                9, 7, 8,    3, 1, 2,    6, 4, 5};

        return new SudokuState(fields);
    }

    private static SudokuState createIncorrectlySolvedBoard(){
        int[] fields = {
                1, 2, 3,    4, 1, 6,    7, 8, 9,
                4, 5, 6,    7, 2, 9,    1, 2, 3,
                7, 8, 9,    1, 3, 3,    4, 5, 6,

                2, 3, 1,    5, 4, 4,    8, 9, 7,
                5, 6, 4,    8, 5, 7,    2, 3, 1,
                8, 9, 7,    2, 6, 1,    6, 7, 4,

                3, 1, 2,    6, 7, 5,    9, 7, 8,
                6, 4, 5,    9, 8, 8,    3, 1, 2,
                9, 7, 8,    3, 9, 2,    6, 4, 5
        };

        return new SudokuState(fields);
    }

    private static SudokuState createTestBoard(){
        int[] fields = {
                1, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0,

                0, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0,

                0, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0,
                0, 0, 0,    0, 0, 0,    0, 0, 0
        };

        return new SudokuState(fields);
    }

    @Override
    protected Collection<TestGameDefinition<SudokuMove, SudokuState>.TestCase> generateTestcases() {
        List<TestCase> result = new ArrayList<>();
        result.add(new TestCase(createAcceptableSolvedBoard(), new SudokuMove(1, 1) ,doNotTestMoveLegality | initialStateIsAcceptable));
        result.add(new TestCase(createIncorrectlySolvedBoard(), new SudokuMove(1, 1), doNotTestMoveLegality | initialStateIsNotAcceptable ));
        result.add(new TestCase(createTestBoard(), new SudokuMove(1, 1), doNotTestMoveLegality | initialStateIsNotAcceptable));
        result.add(new TestCase(createTestBoard(), new SudokuMove(2, 1), 0));
        result.add(new TestCase(createTestBoard(), new SudokuMove(2, 0), illegalMove));

        result.add(new TestCase(createTestBoard(), new SudokuMove(2, -1), illegalMove));
        result.add(new TestCase(createTestBoard(), new SudokuMove(2, 81), illegalMove));
        result.add(new TestCase(createTestBoard(), new SudokuMove(0, 1), illegalMove));
        result.add(new TestCase(createTestBoard(), new SudokuMove(10, 1), illegalMove));
        result.add(new TestCase(createTestBoard(), new SudokuMove(-1, 1), illegalMove));
        return result;
    }
}
