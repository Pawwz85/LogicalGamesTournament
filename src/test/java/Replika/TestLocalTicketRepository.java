package Replika;

import Mockups.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Tournament.DTO.PuzzleSolutionTicketByteDecoder;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTOByteDecoder;
import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.Replika.LocalSolutionTicketRepository;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestLocalTicketRepository {

    private final static ByteDecoder<SimpleArithmeticPuzzleState> stateDecoder = new SimpleArithmeticPuzzleStateByteDecoder();
    private final static PuzzleSolutionTicketDTOByteDecoder<IntegerMove, SimpleArithmeticPuzzleState> dtoDecoder = new PuzzleSolutionTicketDTOByteDecoder<>(new IntegerMoveByteDecoder(), stateDecoder, SimpleArithmeticGameDefinition.getInstance());
    private final static ByteDecoder<PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState>>
        ticketByteDecoder = new PuzzleSolutionTicketByteDecoder<>(dtoDecoder, SimpleArithmeticGameDefinition.getInstance());


    private LocalSolutionTicketRepository<IntegerMove, SimpleArithmeticPuzzleState> repository = null;
    private PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> exampleTicket;

    private void setUp(){
        repository = new LocalSolutionTicketRepository<>(ticketByteDecoder);
        exampleTicket = new PuzzleSolutionTicket<>(0, -1, new SimpleArithmeticPuzzleState(3, 0), SimpleArithmeticGameDefinition.getInstance());
    }

    private void tearDown(){
        repository = null;
        exampleTicket = null;
    }

    @Test
    public void testPersists() throws RepositoryException {
        setUp();
        repository.persists(exampleTicket);
        var storedTicket = repository.getByID(exampleTicket.ticketID);

        Assertions.assertTrue(storedTicket.isPresent(), "Unexpected empty return from repository");

        Assertions.assertArrayEquals(exampleTicket.toDto().toBytes(), storedTicket.get().toDto().toBytes());
        tearDown();
    }

    @Test
    public void testUpdate() throws RepositoryException {
        setUp();
        // Declaration manager are the most convenient way of modifying a ticket
        MockedSolutionDeclarationManager<IntegerMove, SimpleArithmeticPuzzleState> manager =
                new MockedSolutionDeclarationManager<>(exampleTicket);

        repository.persists(exampleTicket);

        List<IntegerMove> moveList = new ArrayList<>();
        moveList.add(new IntegerMove(3));

        manager.prepare(moveList);
        try {
            manager.declare();
        } catch (WrongStateException | NotPreparedException e) {
            throw new RuntimeException(e);
        }

        repository.update(exampleTicket);

        var storedTicket = repository.getByID(exampleTicket.ticketID);

        if(storedTicket.isEmpty())
            Assertions.fail("Repository returned unexpectedly empty result");

        Assertions.assertArrayEquals(exampleTicket.toDto().toBytes(), storedTicket.get().toDto().toBytes());
        tearDown();
    }

    @Test
    public void testUpdateNonExistingItem(){
        setUp();
        Assertions.assertThrows(RepositoryException.class, ()->repository.update(exampleTicket));
        tearDown();
    }

    @Test
    public void testGetAll() throws RepositoryException {
        setUp();
        List<Integer> currentValues = new ArrayList<>();
        for(int i = 0; i< 10; ++i)
            currentValues.add(i);

        List<PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState>> tickets = currentValues.stream()
                .map(Integer -> new SimpleArithmeticPuzzleState(10, Integer))
                .map(state -> new PuzzleSolutionTicket<>(0, -1, state, SimpleArithmeticGameDefinition.getInstance()))
                .toList();

        for(var t: tickets)
            repository.persists(t);

        Assertions.assertEquals(10, tickets.size());
        tearDown();
    }
}