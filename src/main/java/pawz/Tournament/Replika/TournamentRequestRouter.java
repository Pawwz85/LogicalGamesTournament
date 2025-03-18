package pawz.Tournament.Replika;

import com.google.gson.JsonObject;
import pawz.P2PClient.PuzzleServiceController;
import pawz.P2PClient.Request;
import pawz.P2PClient.SynchronisationController;
import pawz.P2PClient.TicketServiceController;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IRequestRouter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TournamentRequestRouter<Move extends ByteEncodable, State extends ByteEncodable> implements IRequestRouter {

    private final PuzzleServiceController<Move, State> puzzleServiceController;

    private final TicketServiceController<Move, State> ticketServiceController;

    private final SynchronisationController<Move, State> synchronisationController;

    private final Map<URI, Function<Request, JsonObject>> requestConsumers = new HashMap<>();


    public boolean registerEndpoint(URI uri, Function<Request, JsonObject> handler){
        requestConsumers.put(uri, handler);
        return true;
    }

    public boolean registerEndpoint(String path, Function<Request, JsonObject> handler){
        try {
           URI uri = new URI(null, null, path, null, null);
           return registerEndpoint(uri, handler);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private JsonObject makeEndpointNotFoundResponse(){
        JsonObject object = new JsonObject();
        object.addProperty("http_status_code", 404);
        object.addProperty("cause", "Server did not manage to find specified resource.");
        return object;
    }

    private void registerEndpoints(){
        registerEndpoint("puzzles/get", puzzleServiceController::getPuzzle);
        registerEndpoint("puzzles/all", request ->  puzzleServiceController.getAllPuzzles());
        registerEndpoint("tickets/get", ticketServiceController::getTicket);
        registerEndpoint("tickets/declare", ticketServiceController::declareTicket);
        registerEndpoint("tickets/submit", ticketServiceController::submitSolution);
        registerEndpoint("tickets/all", ticketServiceController::getAllTickets);
        registerEndpoint("sync/checksums", synchronisationController::getChecksums);
        registerEndpoint("sync/snapshot", synchronisationController::getReplicaSnapshot);
    }

    public TournamentRequestRouter(TournamentSystem<Move, State> system) {

        this.puzzleServiceController = system.puzzleServiceController;
        this.ticketServiceController = system.ticketServiceController;
        this.synchronisationController = system.synchronisationController;
        registerEndpoints();
    }

    @Override
    public JsonObject routeRequest(Request request) {

        Function<Request, JsonObject> consumer = requestConsumers.get(request.uri);

        if(consumer == null)
            return makeEndpointNotFoundResponse();

        return consumer.apply(request);
    }
}
