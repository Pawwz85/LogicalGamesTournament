package pawz.Tournament.Replika;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Interfaces.*;
import pawz.Tournament.PuzzleSolutionTicket;

import java.util.Collection;
import java.util.Optional;

public class LocalSolutionTicketService<Move extends ByteEncodable, State extends ByteEncodable>
        implements IPuzzleSolutionTicketService<Move, State> {

    private @NotNull IPuzzleSolutionTicketRepository<Move, State> repo;

    public LocalSolutionTicketService(@NotNull IPuzzleSolutionTicketRepository<Move, State> repo) {
        this.repo = repo;
    }

    public void setRepo(@NotNull IPuzzleSolutionTicketRepository<Move, State> repo) {
        this.repo = repo;
    }

    private  void checkOwnership(IServiceSession session, PuzzleSolutionTicket<Move, State> ticket) throws OwnershipException{
        if(!session.isAuthenticated() || session.getSessionId() != ticket.playerID)
            throw new OwnershipException();
    }

    @Override
    public Optional<IPuzzleSolutionTicketProxy<Move, State>> getTickedById(IServiceSession session, int ticketId) throws OwnershipException {
        Optional<PuzzleSolutionTicket<Move, State>> ticket = repo.getByID(ticketId);

        if(ticket.isEmpty())
            return Optional.empty();

        checkOwnership(session, ticket.get());
        return Optional.of(ticket.get());
    }

    @Override
    public Collection<IPuzzleSolutionTicketProxy<Move, State>> getAllOwnedTickets(IServiceSession session) {
        return this.repo.getTicketsByPlayerID(session.getSessionId()).stream().map(ticket -> (IPuzzleSolutionTicketProxy<Move, State>)ticket).toList();
    }

    @Override
    public Optional<PuzzleSolutionTicketDTO<Move, State>> getTicketRecordById(int ticketId) {
        Optional<PuzzleSolutionTicket<Move, State>> ticket = repo.getByID(ticketId);
        return ticket.map(PuzzleSolutionTicket::toDto);
    }

    @Override
    public Collection<PuzzleSolutionTicketDTO<Move, State>> getAllTicketsRecords() {
        return repo.getAllTickets().stream().map(PuzzleSolutionTicket::toDto).toList();
    }
}
