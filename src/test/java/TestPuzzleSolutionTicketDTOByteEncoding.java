import AbstractBehavioralTests.TestByteDecoder;

import Mockups.*;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTOByteDecoder;
import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.PuzzleSolutionTicket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;


public class TestPuzzleSolutionTicketDTOByteEncoding
        extends TestByteDecoder<PuzzleSolutionTicketDTO<IntegerMove, SimpleArithmeticPuzzleState>> {


    protected TestPuzzleSolutionTicketDTOByteEncoding() {
        super(new PuzzleSolutionTicketDTOByteDecoder<>(
                        new IntegerMoveByteDecoder(),
                        new SimpleArithmeticPuzzleStateByteDecoder(),
                        new SimpleArithmeticGameDefinition()),
                createTestData()
        );
    }

    private static Collection<PuzzleSolutionTicketDTO<IntegerMove, SimpleArithmeticPuzzleState>> createTestData() {
        Collection<PuzzleSolutionTicketDTO<IntegerMove, SimpleArithmeticPuzzleState>> result = new ArrayList<>();

        int playerId = 7;

        try {
            // valid solution ticket
            PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> ticket =
                    new PuzzleSolutionTicket<>(playerId, 1, new SimpleArithmeticPuzzleState(10, 1), new SimpleArithmeticGameDefinition());

            MockedSolutionDeclarationManager<IntegerMove, SimpleArithmeticPuzzleState> solutionDeclarationManager = new MockedSolutionDeclarationManager<>(ticket);

            result.add(ticket.toDto());

            solutionDeclarationManager.prepare(Stream.of(3, 3, 3).map(IntegerMove::new).collect(Collectors.toList()));
            solutionDeclarationManager.declare();
            result.add(ticket.toDto());

            solutionDeclarationManager.commit();
            result.add(ticket.toDto());

            ticket.verifySolution();
            result.add(ticket.toDto());

            // Invalid solution ticket
            PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> ticketWithInvalidSolution =
                    new PuzzleSolutionTicket<>(playerId, 1, new SimpleArithmeticPuzzleState(10, 1), new SimpleArithmeticGameDefinition());

            MockedSolutionDeclarationManager<IntegerMove, SimpleArithmeticPuzzleState> invalidSolutionDeclaration = new MockedSolutionDeclarationManager<>(ticketWithInvalidSolution);
            invalidSolutionDeclaration.prepare(Stream.of(3, -2).map(IntegerMove::new).collect(Collectors.toList()));
            invalidSolutionDeclaration.declare();
            result.add(ticketWithInvalidSolution.toDto());

            invalidSolutionDeclaration.commit();
            result.add(ticketWithInvalidSolution.toDto());

            ticketWithInvalidSolution.verifySolution();
            result.add(ticketWithInvalidSolution.toDto());

        } catch (WrongStateException | NotPreparedException e) {
            fail(e);
        }


        return  result;
    }
}
