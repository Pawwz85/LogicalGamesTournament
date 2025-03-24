package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.*;
import com.google.gson.JsonObject;
import pawz.Auth.SessionStore;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.Request;
import pawz.P2PClient.RequestParser;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IRequestRouter;
import pawz.Tournament.Interfaces.IServiceSession;

import java.util.Optional;

public class ServiceReplika<Move extends ByteEncodable, State extends ByteEncodable> extends DefaultReplica<SignedMessage<byte[]>, JsonObject, String> {

    private final SessionStore<byte[]> sessionStore;

    private final RequestParser requestParser = new RequestParser();

    private final IRequestRouter router;

    public ServiceReplika(int replicaId, int tolerance, long timeout, ReplicaMessageLog log, ReplicaEncoder<SignedMessage<byte[]>, JsonObject, String> encoder, ReplicaDigester<SignedMessage<byte[]>> digester, ReplicaTransport<String> transport, SessionStore<byte[]> sessionStore,  IRequestRouter router) {
        super(replicaId, tolerance, timeout, log, encoder, digester, transport);
        this.sessionStore = sessionStore;
        this.router = router;
    }

    private JsonObject createErrorCodeResponse(String cause, int http_code){
        JsonObject object = new JsonObject();
        object.addProperty("http_status_code", http_code);
        object.addProperty("cause", cause);
        return object;
    }


    @Override
    public JsonObject compute(SignedMessage<byte[]> o) {

        Optional<IServiceSession> sessionOptional = sessionStore.authenticate(o);

        if(sessionOptional.isEmpty())
            return createErrorCodeResponse("Authentication failure", 401);

        Request request;
        try{
            request = requestParser.fromBytes(o.payload(), sessionOptional.get());

        } catch (RequestParser.ParsingException e){
            return createErrorCodeResponse(e.toString(), 400);
        }

        return router.routeRequest(request);
    }
}
