package pawz.Tournament;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;

import java.util.*;

public class PuzzleSolutionTicket<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleSolutionTicketProxy<Move, State> {
    public final int playerID;
    public int ticketID;
    private @NotNull PuzzleSolutionTicketPhase phase;
    private long epochTimeTimestamp;
    private byte [] declaredHash;
    private List<Move> moveList;

    private final State initialState;

    private final GameDefinition<Move, State> gameDefinition;

    public PuzzleSolutionTicket(int playerID, int ticketID, State initialState, GameDefinition<Move, State> gameDefinition) {
        this.playerID = playerID;
        this.ticketID = ticketID;
        this.initialState = initialState;
        this.gameDefinition = gameDefinition;
        this.phase = PuzzleSolutionTicketPhase.NotSolved;
        declaredHash = new byte[1];
        moveList = new ArrayList<>(0);
    }

    public PuzzleSolutionTicket(PuzzleSolutionTicketDTO<Move, State> dto, GameDefinition<Move, State> gameDefinition){
        this.playerID = dto.playerID;
        this.ticketID = dto.ticketID;
        this.epochTimeTimestamp = dto.epochTimestamp;
        this.declaredHash = dto.declaredSolutionHash;
        this.moveList = dto.solution;
        this.initialState = dto.initialState;

        this.phase = switch (dto.phase){
            case 0 -> PuzzleSolutionTicketPhase.NotSolved;
            case 1 -> PuzzleSolutionTicketPhase.SolutionDeclared;
            case 2 -> PuzzleSolutionTicketPhase.SolutionSubmitted;
            case 3 -> PuzzleSolutionTicketPhase.SolutionRejected;
            case 4 -> PuzzleSolutionTicketPhase.SolutionVerified;
            default -> PuzzleSolutionTicketPhase.SolutionRejected;
        };

        this.gameDefinition = gameDefinition;
    }

    @Override
    public int getPlayerID() {
        return playerID;
    }

    @Override
    public int getID() {
        return ticketID;
    }

    @NotNull
    public PuzzleSolutionTicketPhase getPhase(){
        return this.phase;
    }

    private void setPhase(@NotNull PuzzleSolutionTicketPhase phase){
        this.phase = phase;
    }

    private byte[] computeActualHash() throws WrongStateException {
        switch (this.phase){
            case NotSolved, SolutionDeclared -> throw new WrongStateException();
        }
        PuzzleSolutionDigester<Move> digester = new PuzzleSolutionDigester<>();
        return digester.digestSolution(playerID, moveList);
    }

    public void declareSolution(@NotNull byte[] declaredSolutionHash, long epochTimeTimestamp) throws WrongStateException{

        if (!this.phase.equals(PuzzleSolutionTicketPhase.NotSolved))
            throw new WrongStateException();

        this.declaredHash = declaredSolutionHash;
        this.epochTimeTimestamp = epochTimeTimestamp;
        setPhase(PuzzleSolutionTicketPhase.SolutionDeclared);
    }

    private boolean checkIfSolutionIsValid(){
        State currentState = initialState;
        for (var m: moveList)
            if(gameDefinition.isMoveLegal(currentState, m))
                currentState = gameDefinition.makeMove(currentState, m);
            else
                return false;

        return gameDefinition.isAcceptable(currentState);
    }

    @Override
    public void commitSolution(@NotNull List<Move> moveList) throws WrongStateException{

        if (!this.phase.equals(PuzzleSolutionTicketPhase.SolutionDeclared))
            throw new WrongStateException();

        this.moveList = moveList;
        setPhase(PuzzleSolutionTicketPhase.SolutionSubmitted);
    }

    public void verifySolution() throws WrongStateException {
        if( !this.phase.equals(PuzzleSolutionTicketPhase.SolutionSubmitted))
            throw new WrongStateException();

        //Step 1. verify if declared hash of the solution matches the declared hash

        if (!Arrays.equals(declaredHash, computeActualHash())){
            setPhase(PuzzleSolutionTicketPhase.SolutionRejected);
            return;
        }

        // Step 2. Check if the actual solution is valid puzzle solution
        if(!checkIfSolutionIsValid())
            setPhase(PuzzleSolutionTicketPhase.SolutionRejected);
        else
            setPhase(PuzzleSolutionTicketPhase.SolutionVerified);
    }

    public long getEpochTimeTimestamp() throws WrongStateException{
        if (Objects.requireNonNull(phase) == PuzzleSolutionTicketPhase.NotSolved) {
            throw new WrongStateException();
        }
        return epochTimeTimestamp;
    }

    public List<Move> getMoveList() throws WrongStateException{
        switch (this.phase){
            case NotSolved, SolutionDeclared -> throw new WrongStateException();
        }
        return moveList;
    }

    @Override
    public State getState() {
        return initialState;
    }

    public PuzzleSolutionTicketDTO<Move, State> toDto() {
        PuzzleSolutionTicketDTO<Move, State> result = new PuzzleSolutionTicketDTO<>();
        result.playerID = playerID;
        result.ticketID = ticketID;
        result.epochTimestamp = epochTimeTimestamp;
        result.initialState = initialState;
        result.declaredSolutionHash = declaredHash;
        result.solution = moveList;
        result.phase = switch (phase){
            case NotSolved -> 0;
            case SolutionDeclared -> 1;
            case SolutionSubmitted -> 2;
            case SolutionRejected -> 3;
            case SolutionVerified -> 4;
        };

        return result;
    }
}
