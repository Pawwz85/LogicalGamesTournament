package pawz.Tournament.Replika;

import org.jetbrains.annotations.NotNull;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IPuzzleRepository;
import pawz.Tournament.Interfaces.IPuzzleService;

import java.util.Collection;
import java.util.Optional;

public class LocalPuzzleService<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleService<Move, State> {

    private @NotNull IPuzzleRepository<Move, State> repo;

    public LocalPuzzleService(@NotNull IPuzzleRepository<Move, State> repo) {
        this.repo = repo;
    }

    public void setRepo(@NotNull IPuzzleRepository<Move, State> repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Puzzle<Move, State>> getPuzzleById(int id) {
        return repo.getByID(id);
    }

    @Override
    public Collection<Puzzle<Move, State>> getAllPuzzles() {
        return repo.getAll();
    }
}
