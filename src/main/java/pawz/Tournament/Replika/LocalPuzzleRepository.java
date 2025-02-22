package pawz.Tournament.Replika;

import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IPuzzleRepository;
import pawz.Tournament.Exceptions.RepositoryException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LocalPuzzleRepository<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleRepository<Move, State> {

    private final Map<Integer, Puzzle<Move, State>> data = new HashMap<>();
    private final ByteDecoder<Puzzle<Move, State>> PuzzleDecoder;

    private Integer idSequence = 0;

    public LocalPuzzleRepository(ByteDecoder<Puzzle<Move, State>> puzzleDecoder) {
        PuzzleDecoder = puzzleDecoder;
    }

    private Integer generateId() {
        return ++idSequence;
    }


    @Override
    public boolean persists(Puzzle<Move, State> puzzle) throws RepositoryException {
        puzzle.puzzleId = generateId();
        return update(puzzle);
    }

    @Override
    public boolean update(Puzzle<Move, State> puzzle) throws RepositoryException {
        Puzzle<Move, State> copy = null;
        try {
            copy = PuzzleDecoder.fromBytes(puzzle.toBytes());
        } catch (IOException e) {
            throw new RepositoryException();
        }
        data.put(puzzle.puzzleId, copy);
        return true;
    }

    @Override
    public Optional<Puzzle<Move, State>> getByID(int id) {
        Puzzle<Move, State> result = data.get(id);
        return (result == null)?Optional.empty(): Optional.of(result);
    }

    @Override
    public Collection<Puzzle<Move, State>> getAll() {
        return data.values();
    }

    // package-private by design!
    // Designed to be used by ReplikaSynchronisation Service only
    void clear(){
            idSequence = 0;
            data.clear();
    }
}
