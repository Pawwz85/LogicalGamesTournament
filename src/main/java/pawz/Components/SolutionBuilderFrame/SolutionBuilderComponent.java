package pawz.Components.SolutionBuilderFrame;

import org.jetbrains.annotations.Nullable;
import pawz.Components.Internals.Component;
import pawz.Components.Internals.EventLoop;
import pawz.Components.NamedEvents.UserIssuedSolutionFrameUpdateEvent;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderFrame;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.LinkedList;
import java.util.List;

public class SolutionBuilderComponent<Move extends ByteEncodable, State extends ByteEncodable> extends Component<SolutionBuilderFrame<Move, State>> {

    // eventLoop in which we will dispatch user frame updates
    private final EventLoop eventLoop;
    private final GameDefinition<Move, State> gameDefinition;

    private @Nullable Puzzle<Move, State> currentPuzzle = null;
    private @Nullable State currentState = null;

    private final List<Move> solution = new LinkedList<>();



    public SolutionBuilderComponent(EventLoop eventLoop, GameDefinition<Move, State> gameDefinition) {
        this.eventLoop = eventLoop;
        this.gameDefinition = gameDefinition;
    }

    @Override
    protected SolutionBuilderFrame<Move, State> getCurrentSnapshot() {
        if(currentState != null && currentPuzzle != null)
            return new SolutionBuilderFrame<>(currentPuzzle, solution, gameDefinition);
        else
            return null;
    }


    private void emitFrameUpdate(){
        eventLoop.addEvent(new UserIssuedSolutionFrameUpdateEvent<>(getCurrentSnapshot()));
    }

    public void setFrame(SolutionBuilderFrame<Move, State> frame){
        this.currentPuzzle = frame.puzzle;
        this.currentState = frame.currentState;
        solution.clear();
        solution.addAll(frame.getSolution());
        notifyObservers();
    }

    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public void addMove(Move m) {
        if(currentState != null && currentPuzzle != null && gameDefinition.isMoveLegal(currentState, m)){
            solution.add(m);
            currentState = gameDefinition.makeMove(currentState, m);
            notifyObservers();
            emitFrameUpdate();
        }
    }

    public void undo (){
        if(currentState != null && currentPuzzle != null && canUndo()) {
            solution.remove(solution.size() - 1);
            currentState = currentPuzzle.state;
            for(var m : solution)
                currentState = gameDefinition.makeMove(currentState, m);
            notifyObservers();
            emitFrameUpdate();
        }
    }

}
