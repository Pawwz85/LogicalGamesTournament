package pawz.Tournament.Interfaces;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.PuzzleSolutionTicketPhase;
import pawz.Tournament.Exceptions.WrongStateException;

import java.util.List;

public interface IPuzzleSolutionTicketProxy<Move extends ByteEncodable, State extends ByteEncodable> {
    int getPlayerID();
    int getID();
    PuzzleSolutionTicketPhase getPhase();
    void declareSolution(@NotNull byte[] declaredSolutionHash, long epochTimeTimestamp) throws WrongStateException;

    void commitSolution(@NotNull List<Move> moveList) throws WrongStateException;
    void verifySolution() throws WrongStateException;
    long getEpochTimeTimestamp() throws WrongStateException;
    List<Move> getMoveList() throws WrongStateException;
    State getState();
}
