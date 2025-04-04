package pawz.TournamentClient;


import com.gmail.woodyc40.pbft.Client;
import com.google.gson.JsonObject;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.RemoteSolutionTicket;
import pawz.P2PClient.ResultParsers.TicketServiceResultParser;
import pawz.P2PClient.SignedMessageFactory;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Interfaces.*;
import pawz.Tournament.Replika.LocalSolutionTicketService;

import java.util.*;
import java.util.stream.Collectors;

// A selector class that allows user select a
public class RemoteTicketService<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleSolutionTicketService<Move, State> {

    /*
        Selects a record from the local puzzle service, and returns the proxy that points to the selected ticket.
    */

    private final LocalSolutionTicketService<Move, State> localService;
    private final Client<SignedMessage<byte[]>, JsonObject, String> client;
    private final SignedMessageFactory signedMessageFactory;
    private final TicketServiceResultParser<Move, State> ticketServiceResultParser;
    private final ByteEncoder<Collection<Move>> solutionEncoder;


    public RemoteTicketService(LocalSolutionTicketService<Move, State> localService, Client<SignedMessage<byte[]>, JsonObject, String> client, SignedMessageFactory signedMessageFactory, TicketServiceResultParser<Move, State> ticketServiceResultParser, ByteEncoder<Collection<Move>> solutionEncoder) {
        this.localService = localService;
        this.client = client;
        this.signedMessageFactory = signedMessageFactory;
        this.ticketServiceResultParser = ticketServiceResultParser;
        this.solutionEncoder = solutionEncoder;
    }

    private RemoteSolutionTicket<Move, State> remoteFromLocal(PuzzleSolutionTicketDTO<Move, State> record){
        return new RemoteSolutionTicket<>(record, client,signedMessageFactory,ticketServiceResultParser,solutionEncoder,localService);
    }


    @Override
    public Optional<IPuzzleSolutionTicketProxy<Move, State>> getTickedById(IServiceSession session, int ticketId) throws OwnershipException {
        // Result is NOT used, instead we make this call to check for ownership
        localService.getTickedById(session, ticketId);
        return localService.getTicketRecordById(ticketId).map(this::remoteFromLocal);
    }

    @Override
    public Collection<IPuzzleSolutionTicketProxy<Move, State>> getAllOwnedTickets(IServiceSession session) {

        var ownedTickets = localService.getAllOwnedTickets(session);

        List<PuzzleSolutionTicketDTO<Move, State>> records = new ArrayList<>(ownedTickets.size());
        for (var ticket : ownedTickets){
            var optionalSubResult = localService.getTicketRecordById(ticket.getID());
            optionalSubResult.ifPresent(records::add);
        }

        return records.stream().map(this::remoteFromLocal).collect(Collectors.toList());
    }

    @Override
    public Optional<PuzzleSolutionTicketDTO<Move, State>> getTicketRecordById(int tickedId) {
        return localService.getTicketRecordById(tickedId);
    }

    @Override
    public Collection<PuzzleSolutionTicketDTO<Move, State>> getAllTicketsRecords() {
        return localService.getAllTicketsRecords();
    }

    public Optional<RemoteSolutionTicket<Move, State>> getTicketByState(IServiceSession session, State s){
        /*
            It would be much better to grab ticket by puzzle ID instead,
            but this should be enough for small examples
         */

        var ownedTickets = localService.getAllOwnedTickets(session);
        IPuzzleSolutionTicketProxy<Move, State> matchedTicket = null;
        for( var ticket: ownedTickets)
            if (Arrays.equals(ticket.getState().toBytes(), s.toBytes())){
                matchedTicket = ticket;
        }

        if (matchedTicket != null)
            return localService.getTicketRecordById(matchedTicket.getID()).map(this::remoteFromLocal);
        else
            return Optional.empty();
    }
}
