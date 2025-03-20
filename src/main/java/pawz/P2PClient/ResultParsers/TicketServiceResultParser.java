package pawz.P2PClient.ResultParsers;

import com.google.gson.JsonObject;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class TicketServiceResultParser<Move extends ByteEncodable, State extends ByteEncodable> {

    private final ByteDecoder<PuzzleSolutionTicketDTO<Move, State>> dtoByteDecoder;
    private final ByteDecoder<Collection<PuzzleSolutionTicketDTO<Move, State>>> collectionByteDecoder;

    public TicketServiceResultParser(ByteDecoder<PuzzleSolutionTicketDTO<Move, State>> dtoByteDecoder) {
        this.dtoByteDecoder = dtoByteDecoder;
        DeriveByteUtils<PuzzleSolutionTicketDTO<Move, State>> deriveByteUtils = new DeriveByteUtils<>();
        this.collectionByteDecoder = deriveByteUtils.collectionByteDecoder(this.dtoByteDecoder);
    }

    public Optional<PuzzleSolutionTicketDTO<Move, State>> getTicket(JsonObject APIResult){
        try{
            int statusCode = APIResult.get("http_status_code").getAsInt();
            if(statusCode == 200){
                String ticketBase64 = APIResult.get("ticket").getAsString();
                byte[] bytes = Base64.getDecoder().decode(ticketBase64);
                return Optional.of(dtoByteDecoder.fromBytes(bytes));
            }
        } catch (Exception ignored){}
        return Optional.empty();
    }

    public Collection<PuzzleSolutionTicketDTO<Move, State>> getAllTickets(JsonObject APIResult){
        try{
            int statusCode = APIResult.get("http_status_code").getAsInt();
            if(statusCode == 200){
                String ticketsBase64 = APIResult.get("tickets").getAsString();
                byte[] bytes = Base64.getDecoder().decode(ticketsBase64);
                return collectionByteDecoder.fromBytes(bytes);
            }
        } catch (Exception ignored){}
        return new ArrayList<>();
    }

    private RemoteTicketStateMachineActionResult parseException(JsonObject APIResult){
        String exception = null;
        try{
            int statusCode = APIResult.get("http_status_code").getAsInt();
            if(statusCode >= 200 && statusCode < 300){
                return new RemoteTicketStateMachineActionResult(true, false, false);
            } else {
            exception = APIResult.get("exception").getAsString();
            }
        } catch (Exception ignored){
            return new RemoteTicketStateMachineActionResult(false, false, false);
        }
        if(exception!=null && exception.equals("OwnershipException"))
            return new RemoteTicketStateMachineActionResult(false, true, false);
        if(exception!=null && exception.equals("WrongStateException"))
            return new RemoteTicketStateMachineActionResult(false, false, true);
        return new RemoteTicketStateMachineActionResult(false, false, false);
    }

    public RemoteTicketStateMachineActionResult declareTicket(JsonObject APIResult){
        return parseException(APIResult);
    }

    public RemoteTicketStateMachineActionResult submitTicket(JsonObject APIResult) {
        return parseException(APIResult);
    }

}
