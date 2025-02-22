package Replika;

import Mockups.IntegerMove;
import Mockups.SimpleArithmeticPuzzleState;
import Mockups.SimpleArithmeticPuzzleStateByteDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Puzzle;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.PuzzleDecoder;
import pawz.Tournament.Replika.LocalPuzzleRepository;
import pawz.Tournament.Replika.LocalPuzzleService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestLocalPuzzleService {


    private final LocalPuzzleService<IntegerMove, SimpleArithmeticPuzzleState> service;
    private final int idOfPuzzleWithGoalValueOf7;

    public TestLocalPuzzleService() throws RepositoryException {
        LocalPuzzleRepository<IntegerMove, SimpleArithmeticPuzzleState> repo = new LocalPuzzleRepository<>(
                new PuzzleDecoder<>(new SimpleArithmeticPuzzleStateByteDecoder())
        );
        service = new LocalPuzzleService<>(repo);

        int[] currentValues = {2, 3, 7};
        List<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>> puzzles = Arrays.stream(currentValues)
                .boxed()
                .map(I -> new SimpleArithmeticPuzzleState(10, I))
                .map(state-> new Puzzle<IntegerMove, SimpleArithmeticPuzzleState>(state))
                .toList();

        repo.persists(puzzles.get(0));
        repo.persists(puzzles.get(1));
        repo.persists(puzzles.get(2));
        idOfPuzzleWithGoalValueOf7 = puzzles.get(2).puzzleId;
    }

    @Test
    public void testGetAll(){
        Set<Integer> currentValues = service.getAllPuzzles().
                stream().map(puzzle -> puzzle.state.currentValue)
                .collect(Collectors.toSet());

        Assertions.assertTrue(currentValues.containsAll(Arrays.asList(2, 3, 7)));
    }

    @Test
    public void testGetById(){
        var optionalPuzzle = service.getPuzzleById(idOfPuzzleWithGoalValueOf7);
        if(optionalPuzzle.isEmpty())
            Assertions.fail("Unexpected empty return from puzzle service");
        Assertions.assertEquals(7, optionalPuzzle.get().state.currentValue);
    }
}
