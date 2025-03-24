package pawz.Components;

import org.jetbrains.annotations.Nullable;
import pawz.Components.Internals.Component;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.LinkedList;
import java.util.List;

public class SolutionBuilderComponent<Move extends ByteEncodable, State extends ByteEncodable> extends Component<SolutionBuilderFrame<Move, State>> {

private final GameDefinition<Move, State> gameDefinition;

    private @Nullable State initialState = null;
    private @Nullable State currentState = null;

    private final List<Move> solution = new LinkedList<>();



    public SolutionBuilderComponent(GameDefinition<Move, State> gameDefinition) {
        this.gameDefinition = gameDefinition;
    }

    @Override
    protected SolutionBuilderFrame<Move, State> getCurrentSnapshot() {
        if(currentState != null && initialState != null)
            return new SolutionBuilderFrame<>(initialState, currentState, solution);
        else
            return null;
    }

    public void setFrame(SolutionBuilderFrame<Move, State> frame){
        this.initialState = frame.initialState;
        this.currentState = frame.currentState;
        solution.clear();
        solution.addAll(frame.getSolution());
        notifyObservers();
    }

    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public void addMove(Move m) {
        if(currentState != null && initialState != null){
            solution.add(m);
            currentState = gameDefinition.makeMove(currentState, m);
            notifyObservers();
        }
    }

    public void undo (){
        if(currentState != null && initialState != null && canUndo()) {
            solution.remove(solution.size() - 1);
            currentState = initialState;
            for(var m : solution)
                currentState = gameDefinition.makeMove(currentState, m);
        }
    }

}
