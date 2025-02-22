package AbstractBehavioralTests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.Collection;

public abstract class TestGameDefinition<Move extends ByteEncodable, State extends ByteEncodable> {

    public static final int initialStateIsAcceptable = 0x1;
    public static final int initialStateIsNotAcceptable = 0x2;
    public static final int illegalMove = 0x4;

    public static final int doNotTestMoveLegality = 0x8;

    private final GameDefinition<Move, State> gameDefinition;

    protected TestGameDefinition(GameDefinition<Move, State> gameDefinition) {
        this.gameDefinition = gameDefinition;
    }


    protected class TestCase {
       public int flags;
       public  @NotNull State state;
       public @NotNull Move m;

       public TestCase(@NotNull State s, @Nullable Move m, int flags){
           this.flags = flags;
           this.state = s;
           this.m = m;
       }
    }

    protected abstract Collection<TestCase> generateTestcases();


    private void testTestCaseFlags(TestCase t){
        int f = t.flags;
        if((f&initialStateIsAcceptable) != 0 && (f&initialStateIsNotAcceptable) != 0)
            Assertions.fail("Test case expectations are not possible to satisfy");
    }

    private void testInitialState(TestCase t){
        boolean value = gameDefinition.isAcceptable(t.state);
        if((t.flags&initialStateIsAcceptable) != 0)
            Assertions.assertTrue(value, "Expected acceptable state, found unacceptable");

        if((t.flags&initialStateIsNotAcceptable) != 0)
            Assertions.assertFalse(value, "Expected unacceptable state, found acceptable");
    }

    private void testMoveLegality(TestCase t){
        if((t.flags & doNotTestMoveLegality) == 0){
            boolean isMoveLegal = gameDefinition.isMoveLegal(t.state, t.m);
            Assertions.assertNotEquals((t.flags & illegalMove) != 0, isMoveLegal);
        }
    }

    @Test
    public void testInitialStateAcceptance(){
        Collection<TestCase> cases = generateTestcases();
        for (TestCase t: cases){
            testTestCaseFlags(t);
            testInitialState(t);
        }
    }

    @Test
    public void testIllegalMoves(){
        for(TestCase t: generateTestcases())
            testMoveLegality(t);
    }
}
