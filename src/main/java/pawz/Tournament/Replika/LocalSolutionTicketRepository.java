package pawz.Tournament.Replika;

import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketRepository;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.Exceptions.RepositoryException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalSolutionTicketRepository<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleSolutionTicketRepository<Move, State> {
    private final Map<Integer, PuzzleSolutionTicket<Move, State>> idToTicket = new HashMap<>();

    private final ByteDecoder<PuzzleSolutionTicket<Move, State>> ticketByteDecoder;
    private Integer idSequence = 0;

    public LocalSolutionTicketRepository(ByteDecoder<PuzzleSolutionTicket<Move, State>> ticketByteDecoder) {
        this.ticketByteDecoder = ticketByteDecoder;
    }

    private Integer generateId() {
        return ++idSequence;
    }
    @Override
    public boolean persists(PuzzleSolutionTicket<Move, State> ticket) throws RepositoryException {
        ticket.ticketID = generateId();
        PuzzleSolutionTicket<Move, State> copy = null;

        try {
            copy = ticketByteDecoder.fromBytes(ticket.toDto().toBytes());
        } catch (IOException e) {
            throw new RepositoryException();
        }

        idToTicket.put(ticket.ticketID, copy);

        return true;
    }

    @Override
    public boolean update(PuzzleSolutionTicket<Move, State> ticket) throws RepositoryException {
        PuzzleSolutionTicket<Move, State> copy = null;
        try {
            copy = ticketByteDecoder.fromBytes(ticket.toDto().toBytes());
        } catch (IOException e) {
            throw new RepositoryException();
        }
        idToTicket.put(ticket.ticketID, copy);
        return true;
    }

    @Override
    public Collection<PuzzleSolutionTicket<Move, State>> getAllTickets() {
        return idToTicket.values();
    }

    @Override
    public Optional<PuzzleSolutionTicket<Move, State>> getByID(int tickedID) {
        PuzzleSolutionTicket<Move, State> result = idToTicket.get(tickedID);
        return (result == null)?Optional.empty(): Optional.of(result);
    }

    @Override
    public Collection<PuzzleSolutionTicket<Move, State>> getTicketsByPlayerID(int playerID) {
        Collection<PuzzleSolutionTicket<Move, State>> values = idToTicket.values();
        return values.stream().filter((ticket -> ticket.playerID == playerID)).collect(Collectors.toList());
    }
}

