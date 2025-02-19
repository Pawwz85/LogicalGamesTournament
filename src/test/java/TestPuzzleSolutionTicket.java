import Mockups.IntegerMove;
import Mockups.MockedSolutionDeclarationManager;
import Mockups.SimpleArithmeticGameDefinition;
import Mockups.SimpleArithmeticPuzzleState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.PuzzleSolutionTicketPhase;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class TestPuzzleSolutionTicket {

    private int ticketIdSequence = 0;

    private int generateTickedId(){
        return ++ticketIdSequence;
    }

    private PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> createTicket(){
        SimpleArithmeticPuzzleState state = new SimpleArithmeticPuzzleState(10, 1);
        int playerID = 7;
        return new PuzzleSolutionTicket<>(playerID, generateTickedId(), state, new SimpleArithmeticGameDefinition());
    }

    private PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> createTicketInSpecificPhase(@NotNull PuzzleSolutionTicketPhase phase){
        PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> result = createTicket();
        MockedSolutionDeclarationManager<IntegerMove> solutionDeclarationManager = new MockedSolutionDeclarationManager<IntegerMove>(result);

        List<IntegerMove> validSolution = Stream.of(3, 3, 3).map(IntegerMove::new).toList();

        try {
            switch (phase) {
                case NotSolved -> {
                }
                case SolutionDeclared -> {
                    solutionDeclarationManager.prepare(validSolution);
                    solutionDeclarationManager.declare();
                }

                case SolutionSubmitted -> {
                    solutionDeclarationManager.prepare(validSolution);
                    solutionDeclarationManager.declare();
                    solutionDeclarationManager.commit();
                }
                case SolutionVerified -> {
                    solutionDeclarationManager.prepare(validSolution);
                    solutionDeclarationManager.declare();
                    solutionDeclarationManager.commit();
                    result.verifySolution();
                }
                case SolutionRejected -> {
                    result.declareSolution("Incorrect hash".getBytes(), 0);
                    result.commitSolution(validSolution);
                    result.verifySolution();
                }
            }
        } catch (WrongStateException | NotPreparedException e){
            fail(e);
        }
        return result;

    }
    @Test
    void testValidSolutionVerification(){
        PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> ticket = createTicket();
        MockedSolutionDeclarationManager<IntegerMove> solutionDeclarationManager = new MockedSolutionDeclarationManager<>(ticket);
        List<IntegerMove> solution = Stream.of(3, 3, 3).map(IntegerMove::new).toList();

        solutionDeclarationManager.prepare(solution);

        try {
            solutionDeclarationManager.declare();
            solutionDeclarationManager.commit();
            ticket.verifySolution();
        } catch (WrongStateException | NotPreparedException e) {
            fail(e);
        }

        Assertions.assertEquals(PuzzleSolutionTicketPhase.SolutionVerified, ticket.getPhase());
    }

    @Test
    void testInvalidSolutionRejection() {
        PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> ticket = createTicket();
        MockedSolutionDeclarationManager<IntegerMove> solutionDeclarationManager = new MockedSolutionDeclarationManager<>(ticket);
        List<IntegerMove> solution = Stream.of(3, -2, 3).map(IntegerMove::new).toList();

        solutionDeclarationManager.prepare(solution);

        try {
            solutionDeclarationManager.declare();
            solutionDeclarationManager.commit();
            ticket.verifySolution();
        } catch (WrongStateException | NotPreparedException e) {
            fail(e);
        }

        Assertions.assertEquals(PuzzleSolutionTicketPhase.SolutionRejected, ticket.getPhase());
    }

    @Test
    void testInvalidHash(){
        PuzzleSolutionTicket<IntegerMove, SimpleArithmeticPuzzleState> ticket = createTicket();
        List<IntegerMove> solution = Stream.of(3, 3, 3).map(IntegerMove::new).toList();
        try {
            ticket.declareSolution("BullshitHash".getBytes(), 0);
            ticket.commitSolution(solution);
            ticket.verifySolution();
        } catch (WrongStateException e) {
            fail(e);
        }

        Assertions.assertEquals(PuzzleSolutionTicketPhase.SolutionRejected, ticket.getPhase());
    }

    @Test
    void TicketThrowsExceptionWhenMethodIsCalledInWrongPhase(){
        /*
            Check if method called outside its desired phase throws WrongStateException.

              method / desired phase:
              declareSolution / NotSolved
              commitSolution / SolutionDeclared
              verifySolution / SolutionSubmitted
        */
        List<IntegerMove> solution = Stream.of(3, 3, 3).map(IntegerMove::new).toList();
        for (PuzzleSolutionTicketPhase ticketPhase : PuzzleSolutionTicketPhase.values()){
            var ticket = createTicketInSpecificPhase(ticketPhase);

            if(ticketPhase != PuzzleSolutionTicketPhase.NotSolved)
                Assertions.assertThrows(WrongStateException.class, ()->ticket.declareSolution("Pretend this is hash".getBytes(), 0));

            if (ticketPhase != PuzzleSolutionTicketPhase.SolutionDeclared)
                Assertions.assertThrows(WrongStateException.class, ()->ticket.commitSolution(solution));

            if (ticketPhase != PuzzleSolutionTicketPhase.SolutionSubmitted)
                Assertions.assertThrows(WrongStateException.class, ticket::verifySolution);

        }

    }

}
