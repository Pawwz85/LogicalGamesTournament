package TestBehavioralsForMockups;

import AbstractBehavioralTests.TestByteDecoder;
import Mockups.SimpleArithmeticPuzzleState;
import Mockups.SimpleArithmeticPuzzleStateByteDecoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSimpleArithmeticPuzzleStateByteEncoding extends TestByteDecoder<SimpleArithmeticPuzzleState> {

    protected TestSimpleArithmeticPuzzleStateByteEncoding() {
        super(new SimpleArithmeticPuzzleStateByteDecoder(),
                createTestData());
    }

    private static Collection<SimpleArithmeticPuzzleState> createTestData(){
        List<SimpleArithmeticPuzzleState> result = new ArrayList<>();

        result.add( new SimpleArithmeticPuzzleState(1, 3));
        result.add( new SimpleArithmeticPuzzleState(5, 0));
        result.add( new SimpleArithmeticPuzzleState(8, 0));
        result.add( new SimpleArithmeticPuzzleState(3, 1));
        return result;
    }
}
