package pawz.Components.Control;


import org.jetbrains.annotations.NotNull;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderFrame;
import pawz.Components.SolutionBuilderFrame.SolutionFrameStore;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Replika.LocalPuzzleService;


import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SolutionFrameSelector<Move extends ByteEncodable, State extends ByteEncodable> {
    private final SolutionFrameStore<Move, State> frameStore;


    public SolutionFrameSelector(SolutionFrameStore<Move, State> frameStore) {
        this.frameStore = frameStore;
    }


    public Optional<SolutionBuilderFrame<Move, State>> selectFrame(int puzzleId){
       return frameStore.getById(puzzleId);
    }


}
