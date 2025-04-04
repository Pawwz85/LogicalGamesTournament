package pawz.P2PClient;

import com.gmail.woodyc40.pbft.Client;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.ResultParsers.TicketServiceResultParser;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.MalformedResponseException;
import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.PuzzleSolutionTicketPhase;
import pawz.Tournament.Replika.LocalSolutionTicketRepository;
import pawz.Tournament.Replika.LocalSolutionTicketService;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RemoteSolutionTicket<Move extends ByteEncodable, State extends ByteEncodable> implements IPuzzleSolutionTicketProxy<Move, State> {
    
    private PuzzleSolutionTicketDTO<Move, State> cachedDTO;
    private final Client<SignedMessage<byte[]>, JsonObject, String> client;

    private final SignedMessageFactory signedMessageFactory;

    private final TicketServiceResultParser<Move, State> resultParser;
    private final ByteEncoder<Collection<Move>> solutionEncoder;

    private final LocalSolutionTicketService<Move, State> localService;

    public RemoteSolutionTicket(PuzzleSolutionTicketDTO<Move, State> cachedDTO, Client<SignedMessage<byte[]>, JsonObject, String> client, SignedMessageFactory signedMessageFactory, TicketServiceResultParser<Move, State> resultParser, ByteEncoder<Collection<Move>> solutionEncoder, LocalSolutionTicketService<Move, State> localService) {
        this.cachedDTO = cachedDTO;
        this.client = client;
        this.signedMessageFactory = signedMessageFactory;
        this.resultParser = resultParser;
        this.solutionEncoder = solutionEncoder;
        this.localService = localService;
    }

    @Override
    public int getPlayerID() {
        sync();
        return cachedDTO.playerID;
    }

    @Override
    public int getID() {
        sync();
        return cachedDTO.ticketID;
    }

    @Override
    public PuzzleSolutionTicketPhase getPhase() {
        sync();
        switch (cachedDTO.phase){
            case 0 : return PuzzleSolutionTicketPhase.NotSolved;
            case 1 : return PuzzleSolutionTicketPhase.SolutionDeclared;
            case 2 : return PuzzleSolutionTicketPhase.SolutionSubmitted;
            case 3 : return PuzzleSolutionTicketPhase.SolutionRejected;
            case 4 : return PuzzleSolutionTicketPhase.SolutionVerified;
            default : return PuzzleSolutionTicketPhase.SolutionRejected;
        }
    }

    private SignedMessage<byte[]> createDeclareSolutionMsg(@NotNull byte[] declaredSolutionHash){
        JsonObject payload = new JsonObject();
        payload.addProperty("ticket_id", cachedDTO.ticketID);
        payload.addProperty("declared_hash", Base64.getEncoder().encodeToString(declaredSolutionHash));
        return signedMessageFactory.makeSignedRequest("tickets/declare", payload);
    }

    @Override
    public void declareSolution(@NotNull byte[] declaredSolutionHash, long epochTimeTimestamp) throws WrongStateException {
        var future = client.sendRequest(createDeclareSolutionMsg(declaredSolutionHash)).result();
        try {
            JsonObject apiResponse = future.get(5, TimeUnit.SECONDS);
            resultParser.declareTicket(apiResponse).assertSuccessOrThrowErrors();
        } catch (InterruptedException | ExecutionException | TimeoutException | OwnershipException | MalformedResponseException e) {
            throw new WrongStateException();
        }
    }


    private SignedMessage<byte[]> createCommitSolutionMsg(@NotNull List<Move> moveList){
        JsonObject payload = new JsonObject();
        payload.addProperty("ticket_id", cachedDTO.ticketID);
        byte[] solutionBytes = solutionEncoder.toBytes(moveList);
        payload.addProperty("solution", Base64.getEncoder().encodeToString(solutionBytes));
        return signedMessageFactory.makeSignedRequest("tickets/commit", payload);
    }

    @Override
    public void commitSolution(@NotNull List<Move> moveList) throws WrongStateException {
        var future = client.sendRequest(createCommitSolutionMsg(moveList)).result();
        try {
            JsonObject apiResponse = future.get(5, TimeUnit.SECONDS);
            resultParser.declareTicket(apiResponse).assertSuccessOrThrowErrors();
        } catch (InterruptedException | ExecutionException | TimeoutException | OwnershipException | MalformedResponseException e) {
           throw new WrongStateException();
        }
    }

    @Override
    public void verifySolution() throws WrongStateException {
      // user should not call this method of a proxy
      throw new WrongStateException();
    }

    @Override
    public long getEpochTimeTimestamp() throws WrongStateException {
        sync();

        if(cachedDTO.phase <= 0)
            throw new WrongStateException();

        return cachedDTO.epochTimestamp;
    }

    @Override
    public List<Move> getMoveList() throws WrongStateException {
        sync();

        if(cachedDTO.phase <= 2)
            throw new WrongStateException();
        return cachedDTO.solution;
    }

    @Override
    public State getState() {

        sync();
        return cachedDTO.initialState;
    }


    private SignedMessage<byte[]> createGetRecordMessage(int ticketID){
        JsonObject payload = new JsonObject();
        payload.addProperty("ticket_id", ticketID);
        return signedMessageFactory.makeSignedRequest("tickets/get", payload);
    }

    public void syncWithNetwork(int ticketID){
        var future = client.sendRequest(createGetRecordMessage(ticketID)).result();

        try {
            JsonObject apiResponse = future.get(5, TimeUnit.SECONDS);
            Optional<PuzzleSolutionTicketDTO<Move, State>> dtoOptional = resultParser.getTicket(apiResponse);
            dtoOptional.ifPresent(ticketDTO -> cachedDTO = ticketDTO);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {}
    }

    /*
        Under the assumption the service is synchronised,
    */
    public void syncWithLocalRepository(int ticketID){
        localService.getTicketRecordById(ticketID).ifPresent(ticketDTO -> cachedDTO = ticketDTO);
    }

    private void sync(){
        syncWithLocalRepository(cachedDTO.ticketID);
    };
}
