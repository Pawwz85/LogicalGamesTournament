package pawz.Components;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.ArrayList;
import java.util.List;

public class SolutionBuilderFrame<Move extends ByteEncodable, State extends ByteEncodable> {
    public final @NotNull State initialState;
    public final @NotNull State currentState;

    private final List<Move> solution;

    public SolutionBuilderFrame(@NotNull State initialState, @NotNull State currentState, List<Move> solution) {
        this.initialState = initialState;
        this.currentState = currentState;
        this.solution = solution;
    }

    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public List<Move> getSolution(){
        return List.copyOf(solution);
    }



}
