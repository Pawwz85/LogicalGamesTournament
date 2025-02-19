package pawz;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Flow;

public class PuzzleSolutionBuilderFrame<Move extends ByteEncodable, State extends ByteEncodable> implements Flow.Publisher<PuzzleSolutionBuilderFrame> {

    private final @NotNull State initialState;
    private @NotNull State currentState;
    private final @NotNull GameDefinition<Move, State> gameDefinition;


    private final List<Move> solution = new ArrayList<>();

    private final Collection<Flow.Subscriber<? super PuzzleSolutionBuilderFrame<Move, State>>> subscribers = new ArrayList<>();


    private void notifyObservers(){
        for(var subscriber: subscribers)
            subscriber.onNext(this);

    }

    public PuzzleSolutionBuilderFrame(@NotNull State initialState, @NotNull GameDefinition<Move, State> gameDefinition) {
        this.initialState = initialState;
        currentState = initialState;
        this.gameDefinition = gameDefinition;
    }


    public boolean canUndo(){
        return !solution.isEmpty();
    }

    public void undoMove(){
        if(canUndo()){
            solution.removeLast();
            currentState = initialState;
            for(Move m: solution)
                currentState = gameDefinition.makeMove(currentState, m);
            notifyObservers();
        }
    }

    public void addMove(Move m){

        if(!gameDefinition.isMoveLegal(currentState, m))
            return; // No-op

        solution.add(m);
        currentState = gameDefinition.makeMove(currentState, m);
        notifyObservers();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super PuzzleSolutionBuilderFrame> subscriber) {
        subscribers.add(subscriber);
    }

    @NotNull
    public State getCurrentState(){
        return currentState;
    }
}
