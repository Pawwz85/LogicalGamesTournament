package TestBehavioralsForMockups;

import AbstractBehavioralTests.TestByteDecoder;
import Mockups.IntegerMove;
import Mockups.IntegerMoveByteDecoder;

import java.util.ArrayList;
import java.util.Collection;

public class TestIntegerMoveByteEncoding extends  TestByteDecoder<IntegerMove>  {

    protected TestIntegerMoveByteEncoding() {
        super(new IntegerMoveByteDecoder(), createTestData());
    }

    private static Collection<IntegerMove> createTestData(){
        ArrayList<IntegerMove> moves = new ArrayList<>(10);

        for (int i = 0; i< 7; ++i)
            moves.add(new IntegerMove(i));

        moves.add(new IntegerMove(-1));
        moves.add(new IntegerMove(0));
        moves.add(new IntegerMove(101010));
        return moves;
    }
}
