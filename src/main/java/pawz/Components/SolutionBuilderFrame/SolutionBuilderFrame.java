package pawz.Components.SolutionBuilderFrame;

import org.jetbrains.annotations.NotNull;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.ArrayList;
import java.util.List;

public class SolutionBuilderFrame<Move extends ByteEncodable, State extends ByteEncodable> {
    public final @NotNull State initialState;
    public final @NotNull State currentState;

    private final List<Move> solution;

    private final GameDefinition<Move, State> gameDefinition;

    final Puzzle<Move, State> puzzle;


    public SolutionBuilderFrame(@NotNull Puzzle<Move, State> puzzle, List<Move> solution, GameDefinition<Move, State> gameDefinition) {
        this.puzzle = puzzle;
        this.initialState = puzzle.state;
        this.solution = new ArrayList<>(solution);
        this.gameDefinition = gameDefinition;

        State s = puzzle.state;
        for(var m: solution)
            s = gameDefinition.makeMove(s, m);

        this.currentState = s;
    }

    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public List<Move> getSolution(){
       //return solution;
         return List.copyOf(solution);
    }

    public boolean isAcceptable(){
        return gameDefinition.isAcceptable(currentState);
    }

    public int getPuzzleId(){
        return puzzle.puzzleId;
    }
}
