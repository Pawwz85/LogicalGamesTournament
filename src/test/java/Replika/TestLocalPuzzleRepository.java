package Replika;

import Mockups.IntegerMove;
import Mockups.SimpleArithmeticPuzzleState;
import Mockups.SimpleArithmeticPuzzleStateByteDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Puzzle;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.PuzzleDecoder;
import pawz.Tournament.Replika.LocalPuzzleRepository;

import java.util.Optional;

public class TestLocalPuzzleRepository {

    private LocalPuzzleRepository<IntegerMove, SimpleArithmeticPuzzleState> puzzleRepository = null;

    private final ByteDecoder<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>> puzzleByteDecoder;

    public TestLocalPuzzleRepository(){
        this.puzzleByteDecoder = new PuzzleDecoder<>(new SimpleArithmeticPuzzleStateByteDecoder());
        setUp();
        tearDown();
    }

    private void setUp(){
        puzzleRepository = new LocalPuzzleRepository<>(puzzleByteDecoder);
    }

    private void tearDown(){
        puzzleRepository = null;
    }

    @Test
    public void testPersists(){
            setUp();

            Puzzle<IntegerMove, SimpleArithmeticPuzzleState> puzzle = new Puzzle<>(new SimpleArithmeticPuzzleState(10, 5));

        try {
            boolean result =  puzzleRepository.persists(puzzle);

            Assertions.assertTrue(result);
        } catch (RepositoryException e) {
            Assertions.fail("Failed to save puzzle");
        }

        tearDown();
    }

    @Test void testPersistThenGet(){
        setUp();

        Puzzle<IntegerMove, SimpleArithmeticPuzzleState> puzzle1 = new Puzzle<>(new SimpleArithmeticPuzzleState(10, 5));
        Optional<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>> puzzle2;

        try{
            puzzleRepository.persists(puzzle1);
            puzzle2 = puzzleRepository.getByID(puzzle1.puzzleId);

            Assertions.assertTrue(puzzle2.isPresent(), "Unexpected empty return");
            SimpleArithmeticPuzzleState state = puzzle2.get().state;
            Assertions.assertEquals(puzzle1.state.currentValue, state.currentValue);
            Assertions.assertEquals(puzzle1.state.goal, state.goal);
        } catch (RepositoryException e){
            Assertions.fail("Failed to save puzzle");
        }

        tearDown();
    }

    @Test void testUpdate(){
        setUp();
        Puzzle<IntegerMove, SimpleArithmeticPuzzleState> puzzle1 = new Puzzle<>(new SimpleArithmeticPuzzleState(10, 5));
        Optional<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>> puzzle2;

        try{
        puzzleRepository.persists(puzzle1);
        puzzle1.state = new SimpleArithmeticPuzzleState(10, 8);
        puzzleRepository.update(puzzle1);
        puzzle2 = puzzleRepository.getByID(puzzle1.puzzleId);
        Assertions.assertTrue(puzzle2.isPresent(), "Unexpected empty return");
        SimpleArithmeticPuzzleState state = puzzle2.get().state;
        Assertions.assertEquals(puzzle1.state.currentValue, state.currentValue);
        Assertions.assertEquals(puzzle1.state.goal, state.goal);
    } catch (RepositoryException e){
        Assertions.fail("Failed to save puzzle");
    }
        tearDown();
    }

    @Test public void updateWithoutMatchingItem(){
        setUp();
        Puzzle<IntegerMove, SimpleArithmeticPuzzleState> puzzle = new Puzzle<>(new SimpleArithmeticPuzzleState(10, 5));

        Assertions.assertThrows(RepositoryException.class,()-> puzzleRepository.update(puzzle));

        tearDown();
    }

    @Test public void testSaveAndRetrieveMultipleItems(){
        setUp();

        try{
            for(int i = 0; i<10; ++i) {
                Puzzle<IntegerMove, SimpleArithmeticPuzzleState> puzzle = new Puzzle<>(new SimpleArithmeticPuzzleState(10, i));
                puzzleRepository.persists(puzzle);
            }
            Assertions.assertEquals(10, puzzleRepository.getAll().size());
        } catch (RepositoryException e){
            Assertions.fail(e);
        }

        tearDown();
    }
}
