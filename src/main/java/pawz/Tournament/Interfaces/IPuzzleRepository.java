package pawz.Tournament.Interfaces;

import pawz.Puzzle;
import pawz.Tournament.Exceptions.RepositoryException;

import java.util.Collection;
import java.util.Optional;

public interface IPuzzleRepository<Move extends ByteEncodable, State extends ByteEncodable> {
    boolean  persists(Puzzle<Move, State> puzzle) throws RepositoryException;
    boolean update(Puzzle<Move, State> puzzle) throws RepositoryException;

    Optional<Puzzle<Move, State>> getByID(int id);
    Collection<Puzzle<Move, State>> getAll();
}
