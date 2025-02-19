package Mockups;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.GameDefinition;

public class SimpleArithmeticGameDefinition implements GameDefinition<IntegerMove, SimpleArithmeticPuzzleState> {
    @Override
    public boolean isAcceptable(@NotNull SimpleArithmeticPuzzleState s) {
        return s.goal == s.currentValue;
    }

    @Override
    public boolean isMoveLegal(@NotNull SimpleArithmeticPuzzleState s, @NotNull IntegerMove m) {
        return m.value == -2 || m.value == 3;
    }

    @NotNull
    @Override
    public SimpleArithmeticPuzzleState makeMove(@NotNull SimpleArithmeticPuzzleState s, @NotNull IntegerMove m) {
        return new SimpleArithmeticPuzzleState(s.goal, s.currentValue + m.value);
    }
}
