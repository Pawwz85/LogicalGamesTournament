package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.*;
import com.google.gson.JsonObject;
import pawz.Auth.SessionStore;
import pawz.Auth.SignedMessage;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IServiceSession;

import java.util.Optional;

public class ServiceReplika<Move extends ByteEncodable, State extends ByteEncodable> extends DefaultReplica<SignedMessage<byte[]>, JsonObject, String> {

    private final SessionStore<byte[]> sessionStore;

    private final TournamentReplika<Move, State> tournamentReplika;

    public ServiceReplika(int replicaId, int tolerance, long timeout, ReplicaMessageLog log, ReplicaEncoder<SignedMessage<byte[]>, JsonObject, String> encoder, ReplicaDigester<SignedMessage<byte[]>> digester, ReplicaTransport<String> transport, SessionStore<byte[]> sessionStore, TournamentReplika<Move, State> tournamentReplika) {
        super(replicaId, tolerance, timeout, log, encoder, digester, transport);
        this.sessionStore = sessionStore;
        this.tournamentReplika = tournamentReplika;
    }

    @Override
    public JsonObject compute(SignedMessage<byte[]> o) {

        Optional<IServiceSession> sessionOptional = sessionStore.authenticate(o);

        // TODO: parse msg 'o' into a 'Request' Object and route it to correct service

        return null;
    }
}
