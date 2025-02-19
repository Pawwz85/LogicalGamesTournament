package pawz.Tournament.Interfaces;

import pawz.Puzzle;

import java.util.Collection;
import java.util.Optional;

public interface IPuzzleService<Move extends ByteEncodable, State extends ByteEncodable> {
    Optional<Puzzle<Move, State>> getPuzzleById(int id);
    Collection<Puzzle<Move, State>> getAllPuzzles();
}

