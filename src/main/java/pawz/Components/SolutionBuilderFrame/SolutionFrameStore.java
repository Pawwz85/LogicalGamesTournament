package pawz.Components.SolutionBuilderFrame;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SolutionFrameStore<Move extends ByteEncodable, State extends ByteEncodable> {

    // frames are stored by their puzzle id
    private final Map<Integer, SolutionBuilderFrame<Move, State>> store = new ConcurrentHashMap<>();


    public Optional<SolutionBuilderFrame<Move, State>> getById(int id){
        return Optional.ofNullable(store.get(id));
    }

    public Collection<SolutionBuilderFrame<Move, State>> getAll(){
        return store.values();
    }

    public void update(SolutionBuilderFrame<Move, State> frame){
        store.put(frame.getPuzzleId(), frame);
    }

    public void clear(){
        store.clear();
    }

}
