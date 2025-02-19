package pawz.Tournament.Interfaces;

import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.OwnershipException;

import java.util.Collection;
import java.util.Optional;

public interface IPuzzleSolutionTicketService<Move extends ByteEncodable, State extends ByteEncodable> {
    Optional<IPuzzleSolutionTicketProxy<Move, State>> getTickedById(IServiceSession session, int ticketId) throws OwnershipException;
    Collection<IPuzzleSolutionTicketProxy<Move, State>> getAllOwnedTickets(IServiceSession session);

    Optional<PuzzleSolutionTicketDTO<Move, State>> getTicketRecordById(int tickedId);
    Collection<PuzzleSolutionTicketDTO<Move, State>> getAllTicketsRecords();
}
