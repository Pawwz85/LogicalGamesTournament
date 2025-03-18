package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.*;
import com.google.gson.JsonObject;
import pawz.Auth.DefaultSigner;
import pawz.Auth.SessionStore;
import pawz.Auth.SignedMessage;
import pawz.Boot.BootConfiguration;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IRequestRouter;
import pawz.Transport.SocketTransport;

public class ServiceReplikaFactory<Move extends ByteEncodable, State extends ByteEncodable> {
    private int replicaId;
    private int tolerance;
    private long timeout;
    private ReplicaMessageLog replicaMessageLog = new DefaultReplicaMessageLog(1000, 100, 50);
    private ReplicaEncoder<SignedMessage<byte[]>, JsonObject, String> encoder = new ReplikaEncoder();
    private ReplicaDigester<SignedMessage<byte[]>> digester = new ReplikaDigester();
    private ReplicaTransport<String> transport;

    private ClientTransport<String> clientTransport;

    private final SessionStore<byte[]> sessionStore;
    private IRequestRouter router;

    private final ClientEncoder<SignedMessage<byte[]>, String> clientEncoder = new pawz.P2PClient.ClientEncoder();

    public ServiceReplikaFactory(BootConfiguration configuration, TournamentSystem<Move, State> system){
        sessionStore = new SessionStore<>(new DefaultSigner());
        sessionStore.configure(configuration);

        SocketTransport socketTransport = new SocketTransport(configuration);
        transport  = socketTransport;
        clientTransport = socketTransport;

        router = new TournamentRequestRouter<>(system);
    }

    public ServiceReplikaFactory<Move, State> withMessageLog(ReplicaMessageLog messageLog){
        this.replicaMessageLog = messageLog;
        return this;
    }

    public ServiceReplikaFactory<Move, State> withEncoder(ReplicaEncoder<SignedMessage<byte[]>, JsonObject, String> encoder){
        this.encoder = encoder;
        return this;
    }

    public ServiceReplikaFactory<Move, State> withDigester(ReplicaDigester<SignedMessage<byte[]>> digester){
        this.digester = digester;
        return this;
    }

    public ServiceReplikaFactory<Move, State> withTransport(ReplicaTransport<String> transport) {
        this.transport = transport;
        return this;
    }

    public ServiceReplikaFactory<Move, State> withClientTransport(ClientTransport<String> transport) {
        this.clientTransport = transport;
        return this;
    }

    public ServiceReplikaFactory<Move, State> withRouter(IRequestRouter router){
        this.router = router;
        return this;
    }

    public ServiceReplika<Move, State> build(){
        return new ServiceReplika<>(replicaId, tolerance, timeout, replicaMessageLog, encoder, digester, transport, sessionStore, router);
    }

    public Client<SignedMessage<byte[]>, JsonObject, String> buildClient(String clientToken){
        return new DefaultClient<>(clientToken, tolerance, timeout, clientEncoder, clientTransport);
    }



}
