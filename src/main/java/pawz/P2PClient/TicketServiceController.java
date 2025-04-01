package pawz.P2PClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.DerivedImplementations.IDeriveByteUtils;
import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.*;
import pawz.Tournament.Replika.LocalSolutionTicketService;

import java.io.IOException;
import java.util.*;


// TODO: add method for adding a new ticket
public class TicketServiceController<Move extends ByteEncodable, State extends ByteEncodable> {

    private final LocalSolutionTicketService<Move, State> service;

    private final ByteDecoder<Collection<Move>> solutionDecoder;

    private final ByteEncoder<PuzzleSolutionTicketDTO<Move, State>> ticketByteEncoder;

    private final IDeriveByteUtils<PuzzleSolutionTicketDTO<Move, State>> deriveByteUtils = new DeriveByteUtils<>();

    private final ByteEncoder<Collection<PuzzleSolutionTicketDTO<Move, State>>> collectionByteEncoder;

    public TicketServiceController(LocalSolutionTicketService<Move, State> service, ByteDecoder<Move> moveByteDecoder) {
        this.service = service;
        this.solutionDecoder = new DeriveByteUtils<Move>().collectionByteDecoder(moveByteDecoder);
        this.ticketByteEncoder = new PlainByteEncoder<>();
        this.collectionByteEncoder = deriveByteUtils.collectionByteEncoder(ticketByteEncoder);
    }

    public JsonObject getAllTickets(){
        JsonArray records = new JsonArray();
        Base64.Encoder encoder = Base64.getEncoder();

        String tickets = encoder.encodeToString(collectionByteEncoder.toBytes(service.getAllTicketsRecords()));

        JsonObject response = new JsonObject();
        response.addProperty("http_status_code", 200);
        response.addProperty("tickets", tickets);
        return response;
    }

    public JsonObject getAllTickets(Request request){
        return getAllTickets();
    }

    public JsonObject getTicket(int id){

        Optional<PuzzleSolutionTicketDTO<Move, State>> ticket = service.getTicketRecordById(id);
        JsonObject response = new JsonObject();


        if(ticket.isEmpty()){
            response.addProperty("http_status_code", 404);
            response.add("ticket", JsonNull.INSTANCE);
            response.addProperty("cause", "Specified ticket do not exist");
        } else {
            response.addProperty("http_status_code", 200);
            response.addProperty("ticket", Base64.getEncoder().encodeToString(ticketByteEncoder.toBytes(ticket.get())));
        }
        return response;
    }

    public JsonObject getTicket(Request request){
        Object idObject = request.params.get("ticket_id");

        if(!(idObject instanceof Integer)) {
            JsonObject response = new JsonObject();
            response.addProperty("http_status_code", 400);
            response.addProperty("cause", "Field 'ticket_id' should be an integer");
            return response;
        }
        Integer id = (Integer) idObject;
        return getTicket(id);
    }

    public JsonObject declareTicket(int ticketId, IServiceSession session, @NotNull byte[] declaredHash){
        JsonObject response = new JsonObject();
        try{
            Optional<IPuzzleSolutionTicketProxy<Move, State>> ticket = service.getTickedById(session, ticketId);

            if(ticket.isPresent()){
                ticket.get().declareSolution(declaredHash, System.currentTimeMillis());
                response.addProperty("http_status_code", 204);
            } else {
                response.addProperty("http_status_code", 404);
                response.addProperty("cause", "Specified ticket do not exist");
            }

        } catch (OwnershipException e){
            response.addProperty("http_status_code", 403);
            response.addProperty("exception", "OwnershipException");
            response.addProperty("cause", "User is not an owner of this solution ticket");
        } catch (WrongStateException e){
            response.addProperty("http_status_code", 403);
            response.addProperty("exception", "WrongStateException");
            response.addProperty("cause", "This ticket was already declared.");
        }

        return  response;
    }

    public JsonObject declareTicket(Request request){
        Object IdObject = request.params.get("ticket_id");
        Object hashObject = request.params.get("declared_hash");

        if(!(IdObject instanceof Integer) || !(hashObject instanceof String)){
            JsonObject response = new JsonObject();
            response.addProperty("http_status_code", 400);
            response.addProperty("cause", "Field 'ticket_id' should be an integer and field " +
                    "'declared_hash' should be string");
            return response;
        }

        Integer ticketID = (Integer) IdObject;
        String hashBase64 = (String) hashObject;
        return declareTicket(ticketID, request.session, Base64.getDecoder().decode(hashBase64));
    }

    public JsonObject submitSolution(int ticketId, IServiceSession session, List<Move> solution){
        JsonObject response = new JsonObject();

        try{
            Optional<IPuzzleSolutionTicketProxy<Move, State>> ticket = service.getTickedById(session, ticketId);

            if(ticket.isPresent()){
                ticket.get().commitSolution(solution);
                response.addProperty("http_status_code", 204);
            } else {
                response.addProperty("http_status_code", 404);
                response.addProperty("cause", "Specified ticket do not exist");
            }
        } catch (OwnershipException e){
            response.addProperty("http_status_code", 403);
            response.addProperty("exception", "OwnershipException");
            response.addProperty("cause", "User is not an owner of this solution ticket");
        } catch (WrongStateException e){
            response.addProperty("http_status_code", 403);
            response.addProperty("exception", "WrongStateException");
            response.addProperty("cause", "Solution can not be submitted now");
        }
        return response;
    }

    public JsonObject submitSolution(Request request){
        Object IdObject = request.params.get("ticket_id");
        Object solutionObject = request.params.get("solution");

        if(!(IdObject instanceof Integer) || !(solutionObject instanceof String)){
            JsonObject response = new JsonObject();
            response.addProperty("http_status_code", 400);
            response.addProperty("cause", "Field 'ticket_id' should be an integer and field " +
                    "'declared_hash' should be string");
            return response;
        }

        Integer ticketID = (Integer) IdObject;
        String solutionBase64 = (String) solutionObject;

        try {
            List<Move> solution = new ArrayList<>(solutionDecoder.fromBytes(Base64.getDecoder().decode(solutionBase64)));
            return submitSolution(ticketID, request.session, solution);
        } catch (IOException e){
            JsonObject response = new JsonObject();
            response.addProperty("http_status_code", 400);
            response.addProperty("cause", "Failed to decode solution");
            return response;
        }
    }

}
