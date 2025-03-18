package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.Replica;
import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.Request;
import pawz.Tournament.Synchronisation.SynchronisationCriticalSection;
import pawz.Tournament.Synchronisation.SynchronisationManager;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PBFTThread implements Runnable {

    private final ConcurrentLinkedQueue<String> incomingMessages;
    private final Replica<SignedMessage<byte[]>, JsonObject, String> replica;

    public boolean isRunning = true;

    private final JsonParser jsonParser = new JsonParser();

    private final ReplikaDecoder decoder = new ReplikaDecoder();

    public PBFTThread(ConcurrentLinkedQueue<String> incomingMessages, Replica<SignedMessage<byte[]>, JsonObject, String> replica) {
        this.incomingMessages = incomingMessages;
        this.replica = replica;
    }


    @Override
    public void run() {

        while (isRunning) {
            String msg = incomingMessages.poll();
            if (msg != null) {
                receiveTransport(msg);
            }
        }
    }

    private void receiveTransport(String transport){
        try {
            JsonObject jsonObject = jsonParser.parse(transport).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type){
                case  "request":
                    ReplicaRequest<SignedMessage<byte[]>> request = decoder.parseRequest(jsonObject);
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
                default:
                    break;
            }
        } catch (Exception ignored) {};
    }
}
