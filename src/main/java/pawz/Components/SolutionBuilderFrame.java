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

    private final GameDefinition<Move, State> gameDefinition;


    public SolutionBuilderFrame(@NotNull State initialState, @NotNull State currentState, List<Move> solution, GameDefinition<Move, State> gameDefinition) {
        this.initialState = initialState;
        this.currentState = currentState;
        this.solution = solution;
        this.gameDefinition = gameDefinition;
    }

    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public List<Move> getSolution(){
        return List.copyOf(solution);
    }

    public boolean isAcceptable(){
        return gameDefinition.isAcceptable(currentState);
    }

}
