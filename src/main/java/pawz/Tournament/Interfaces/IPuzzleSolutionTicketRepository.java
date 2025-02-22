package pawz.Tournament.Interfaces;

import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.Exceptions.RepositoryException;

import java.util.Collection;
import java.util.Optional;

/*
    A high level method for retrieving the puzzle Solution tickets
*/
public interface IPuzzleSolutionTicketRepository<Move extends ByteEncodable, State extends ByteEncodable>{

    boolean persists(PuzzleSolutionTicket<Move, State> ticket) throws RepositoryException;
    boolean update(PuzzleSolutionTicket<Move, State> ticket) throws RepositoryException;
    Collection<PuzzleSolutionTicket<Move, State>> getAllTickets();
    Optional<PuzzleSolutionTicket<Move, State>> getByID(int tickedID);
}
