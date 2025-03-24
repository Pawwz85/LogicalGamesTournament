package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.Client;
import com.gmail.woodyc40.pbft.Replica;
import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.Request;
import pawz.P2PClient.RequestParser;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Synchronisation.SynchronisationCriticalSection;
import pawz.Tournament.Synchronisation.SynchronisationManager;
import pawz.Tournament.Synchronisation.SynchronisationStatus;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PBFTThread<Move extends ByteEncodable, State extends ByteEncodable> extends Thread {

    private final ConcurrentLinkedQueue<String> incomingMessages;
    private final Replica<SignedMessage<byte[]>, JsonObject, String> replica;
    private final Client<SignedMessage<byte[]>, JsonObject, String> client;
    private final SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> criticalSection;

    public volatile boolean isRunning = true;

    private String syncRequestID = "placeholder";

    private final JsonParser jsonParser = new JsonParser();

    private final ReplikaDecoder decoder = new ReplikaDecoder();

    public PBFTThread(ConcurrentLinkedQueue<String> incomingMessages, Replica<SignedMessage<byte[]>, JsonObject, String> replica, Client<SignedMessage<byte[]>, JsonObject, String> client, SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> criticalSection) {
        this.incomingMessages = incomingMessages;
        this.replica = replica;
        this.client = client;
        this.criticalSection = criticalSection;
    }


    @Override
    public void run() {

        while (isRunning) {

            if(isHang()){
                continue;
            }


            String msg = incomingMessages.poll();
            if (msg != null) {
                receiveTransport(msg);
            }
        }
    }

    private boolean isHang() {
        boolean isHang = false;
        try(var syncManager = new SynchronisationManager<>(criticalSection)) {
            isHang = syncManager.isPBFTThreadHang();
            var status = syncManager.getStatus();
            if(status == SynchronisationStatus.SYNCHRONISATION_REQUEST_PREPARED){
                syncRequestID = syncManager.getSynchronisationRequestId();
                syncManager.acknowledgeRequest();
            }


        } catch (Exception ignored){}
        return isHang;
    }


    private void monitorRequestID(SignedMessage<byte[]> msg){
        RequestParser requestParser = new RequestParser();
        try {
            var payload = requestParser.fromBytes(msg.payload(), null);
            Object receivedRequestID = payload.params.get("request_id");

            if(receivedRequestID instanceof String && syncRequestID.equals(receivedRequestID)) {
                try (var syncManager = new SynchronisationManager<>(criticalSection)){
                    syncManager.hangPBFTThread();
                } catch (Exception ignored){}
            }


        } catch (RequestParser.ParsingException ignored
        ) {}
    }

    private void receiveTransport(String transport){
        try {
            JsonObject jsonObject = jsonParser.parse(transport).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type){
                case  "request":
                    ReplicaRequest<SignedMessage<byte[]>> request = decoder.parseRequest(jsonObject);
                    SignedMessage<byte[]> op = request.operation();
                    if(op != null)
                        monitorRequestID(op);
                    replica.recvRequest(request);
                    break;
                case "pre_prepare":
                    ReplicaPrePrepare<SignedMessage<byte[]>> prePrepare = decoder.parsePrePrepare(jsonObject);
                    replica.recvPrePrepare(prePrepare);
                    break;
                case "prepare":
                    ReplicaPrepare prepare = decoder.parsePrepare(jsonObject);
                    replica.recvPrepare(prepare);
                    break;
                case "commit":
                    ReplicaCommit commit = decoder.parseCommit(jsonObject);
                    replica.recvCommit(commit);
                    break;
                case "checkpoint":
                    ReplicaCheckpoint checkpoint = decoder.parseCheckpoint(jsonObject);
                    replica.recvCheckpoint(checkpoint);
                    break;
                case "view_change":
                    ReplicaViewChange viewChange = decoder.parseViewChange(jsonObject);
                    replica.recvViewChange(viewChange);
                    break;
                case "new_view":
                    ReplicaNewView newView = decoder.parseNewView(jsonObject);
                    replica.recvNewView(newView);
                    break;
                case "reply":
                    ReplicaReply<JsonObject> reply = decoder.parseReply(jsonObject);
                    DefaultClientReply<JsonObject> clientReply = new DefaultClientReply<>(
                            reply.viewNumber(), reply.timestamp(), client, reply.replicaId(), reply.result() );
                    client.recvReply(clientReply);
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {};
    }
}
