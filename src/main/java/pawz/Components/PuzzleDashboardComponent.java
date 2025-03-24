package pawz.Components;

import pawz.Components.Internals.Component;
import pawz.Components.NamedEvents.PuzzleDashboardEvent;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.ArrayList;
import java.util.List;


// TODO: PuzzleDashboard entries probably won't be puzzles, but they will be linked to SolutionBuilders
public class PuzzleDashboardComponent<Move extends ByteEncodable, State extends ByteEncodable> extends Component<List<Puzzle<Move, State>>> {

    private List<Puzzle<Move, State>> currentSnapshot = new ArrayList<>();

    @Override
    protected List<Puzzle<Move, State>> getCurrentSnapshot() {
        return currentSnapshot;
    }

    @Override
    protected Object onPuzzleDashboardEvent(PuzzleDashboardEvent<?, ?> event){
        currentSnapshot = new ArrayList<>();

        for(Puzzle<?, ?> puzzle: event.puzzles)
            currentSnapshot.add((Puzzle<Move, State>) puzzle);

        notifyObservers();
        return null;
    }

}

