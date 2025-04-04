package pawz.Components.SolutionBuilderFrame;

import pawz.Components.Internals.Component;
import pawz.Components.NamedEvents.NamedTournamentEvents;
import pawz.Components.NamedEvents.PuzzleDashboardEvent;
import pawz.Components.NamedEvents.UserIssuedSolutionFrameUpdateEvent;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.*;

public class FrameDashboardComponent<Move extends ByteEncodable, State extends ByteEncodable>
    extends Component<Collection<SolutionBuilderFrame<Move, State>>> {

    private final GameDefinition<Move, State> gameDefinition;

    private final SolutionFrameStore<Move, State> frameStore;

    public FrameDashboardComponent(GameDefinition<Move, State> gameDefinition, SolutionFrameStore<Move, State> frameStore) {
        this.gameDefinition = gameDefinition;
        this.frameStore = frameStore;

        // register handler for user issued events
        this.eventFilter.registerEvent(NamedTournamentEvents.UserIssuedSolutionFrameUpdate,
                e -> {this.handleUserIssuedFrameUpdate((UserIssuedSolutionFrameUpdateEvent<Move, State>) e); return  null;});
    }

    private SolutionBuilderFrame<Move, State> validateFrame(Puzzle<Move, State> puzzle, SolutionBuilderFrame<Move, State> frame){
        /*
            Check if provided puzzle matches current frame
        */
        boolean valid = Arrays.equals(puzzle.state.toBytes(), frame.initialState.toBytes());
        return (valid)? frame : new SolutionBuilderFrame<>(puzzle, new ArrayList<>(),  gameDefinition);
    }

    @Override
    protected Collection<SolutionBuilderFrame<Move, State>> getCurrentSnapshot() {
        return frameStore.getAll();
    }

    /*
        We are listening on puzzleDashboardEvents, so we can validate currently stored frames
        as newly synchronised puzzles are being fetch.
    */
    @Override
    protected Object onPuzzleDashboardEvent(PuzzleDashboardEvent<?, ?> event){
        PuzzleDashboardEvent<Move, State> castedEvent = (PuzzleDashboardEvent<Move, State>) event;

        /*
            We want to delete frames that for some reason are now not tied to any puzzle, so we will
            use this temporary map in order to decide with frames to spare.

            Now the real question, why are we deleting left over frames? There is not even a
            possibility of deleting a puzzle during tournament. The answer is simple:
            local puzzle repository might be corrupted, leading to local replica to perceive
            extra puzzles compared to others in the network.
         */
        Map<Integer, SolutionBuilderFrame<Move, State>> tempMap = new HashMap<>();

        boolean updated = false;

        for(var puzzle: castedEvent.puzzles){
            var cachedFrame = frameStore.getById(puzzle.puzzleId);

            if (cachedFrame.isEmpty()){
                tempMap.put(puzzle.puzzleId, new SolutionBuilderFrame<>(puzzle, new ArrayList<>(), gameDefinition));
                updated = true;
            } else {
                var f1 = cachedFrame.get();
                var f2 = validateFrame(puzzle, f1);
                updated = updated || (f1 != f2);
                tempMap.put(puzzle.puzzleId, f2);
            }
        }

        if(updated){
            frameStore.clear();
            for(var frame: tempMap.values())
                frameStore.update(frame);
            notifyObservers();
        }

        return null;
    }

    protected void handleUserIssuedFrameUpdate(UserIssuedSolutionFrameUpdateEvent<Move, State> event){
        frameStore.update(event.frame);
        notifyObservers();
    }
}
