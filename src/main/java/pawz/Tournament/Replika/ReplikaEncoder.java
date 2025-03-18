package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.ReplicaEncoder;
import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pawz.Auth.SignedMessage;
import pawz.Auth.SignedMessageUtil;


import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ReplikaEncoder implements ReplicaEncoder<SignedMessage<byte[]>, JsonObject, String> {

    private final static SignedMessageUtil<byte[]> signedMessageUtil = new SignedMessageUtil<>();
    private final static Function<byte[], byte[]> identity = bytes -> bytes;

    private static String encodeOperation(SignedMessage<byte[]> operation){
        byte[] bytes = signedMessageUtil.toBytes(operation, identity);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private JsonObject toJson (ReplicaRequest<SignedMessage<byte[]>> replicaRequest) {
        JsonObject result = new JsonObject();
        result.addProperty("type","request");
        result.addProperty("client_id", replicaRequest.clientId());
        result.addProperty("timestamp", replicaRequest.timestamp());
        result.addProperty("operation", encodeOperation(replicaRequest.operation()));
        return result;
    }

    private JsonObject toJson(ReplicaPrePrepare<SignedMessage<byte[]>> replicaPrePrepare) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "pre_prepare");
        result.addProperty("seq_number", replicaPrePrepare.seqNumber());
        result.addProperty("view", replicaPrePrepare.viewNumber());
        result.addProperty("digest", Base64.getEncoder().encodeToString(replicaPrePrepare.digest()));
        result.add("request", toJson(replicaPrePrepare.request()));
        return result;
    }

    private JsonObject toJson(ReplicaPrepare replicaPrepare) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "prepare");
        result.addProperty("seq_number", replicaPrepare.seqNumber());
        result.addProperty("view", replicaPrepare.viewNumber());
        result.addProperty("digest", Base64.getEncoder().encodeToString(replicaPrepare.digest()));
        result.addProperty("replica_id", replicaPrepare.replicaId());
        return result;
    }

    private JsonObject toJson (ReplicaCommit replicaCommit) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "commit");
        result.addProperty("seq_number", replicaCommit.seqNumber());
        result.addProperty("view", replicaCommit.viewNumber());
        result.addProperty("digest", Base64.getEncoder().encodeToString(replicaCommit.digest()));
        result.addProperty("replica_id", replicaCommit.replicaId());
        return result;
    }

    private JsonObject toJson(ReplicaReply<JsonObject> replicaReply) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "reply");
        result.add("result", replicaReply.result());
        result.addProperty("view", replicaReply.viewNumber());
        result.addProperty("timestamp", replicaReply.timestamp());
        result.addProperty("replica_id", replicaReply.replicaId());
        result.addProperty("client_id", replicaReply.clientId());
        return result;
    }

    private JsonObject toJson(ReplicaCheckpoint replicaCheckpoint) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "checkpoint");
        result.addProperty("last_seq_number", replicaCheckpoint.lastSeqNumber());
        result.addProperty("digest", Base64.getEncoder().encodeToString(replicaCheckpoint.digest()));
        result.addProperty("replica_id", replicaCheckpoint.replicaId());
        return result;
    }

    private JsonObject toJson(ReplicaViewChange replicaViewChange) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "view_change");
        result.addProperty("last_seq_number", replicaViewChange.lastSeqNumber());
        result.addProperty("replica_id", replicaViewChange.replicaId());
        result.addProperty("new_view_number", replicaViewChange.newViewNumber());

        JsonArray checkpointProofs = new JsonArray();
        for(var proof: replicaViewChange.checkpointProofs())
            checkpointProofs.add(toJson(proof));
        result.add("checkpoint_proofs", checkpointProofs);

        JsonArray preparedProofs = new JsonArray();

        for(Map.Entry<Long, Collection<ReplicaPhaseMessage>> entry: replicaViewChange.preparedProofs().entrySet()){
            JsonObject proof = new JsonObject();
            JsonArray messages = new JsonArray();

            proof.addProperty("seq_number", entry.getKey());
            for(ReplicaPhaseMessage msg : entry.getValue()){
                if (msg instanceof ReplicaPrePrepare){
                    messages.add(toJson((ReplicaPrePrepare<SignedMessage<byte[]>>) msg));
                } else if (msg instanceof ReplicaPrepare) {
                    messages.add(encodePrepare((ReplicaPrepare) msg));
                }
            }
            proof.add("messages", messages);
            preparedProofs.add(proof);
        }
        result.add("prepared_proofs", preparedProofs);

        return result;
    }

    private JsonObject toJson(ReplicaNewView replicaNewView) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "new_view");
        result.addProperty("new_view_number", replicaNewView.newViewNumber());

        JsonArray preparedProofs =  new JsonArray();
        for(ReplicaPrePrepare<?> proof: replicaNewView.preparedProofs()){
            preparedProofs.add(toJson((ReplicaPrePrepare<SignedMessage<byte[]>>) proof));
        }
        result.add("prepared_proofs", preparedProofs);

        JsonArray viewChangeProofs = new JsonArray();
        for(ReplicaViewChange proof: replicaNewView.viewChangeProofs()){
            viewChangeProofs.add(toJson(proof));
        }
        result.add("view_change_proofs", viewChangeProofs);


        return result;
    }

    @Override
    public String encodeRequest(ReplicaRequest<SignedMessage<byte[]>> replicaRequest) {
        return toJson(replicaRequest).toString();
    }

    @Override
    public String encodePrePrepare(ReplicaPrePrepare<SignedMessage<byte[]>> replicaPrePrepare) {
        return toJson(replicaPrePrepare).toString();
    }

    @Override
    public String encodePrepare(ReplicaPrepare replicaPrepare) {
        return toJson(replicaPrepare).toString();
    }

    @Override
    public String encodeCommit(ReplicaCommit replicaCommit) {
        return toJson(replicaCommit).toString();
    }

    @Override
    public String encodeReply(ReplicaReply<JsonObject> replicaReply) {
        return toJson(replicaReply).toString();
    }

    @Override
    public String encodeCheckpoint(ReplicaCheckpoint replicaCheckpoint) {
        return toJson(replicaCheckpoint).toString();
    }

    @Override
    public String encodeViewChange(ReplicaViewChange replicaViewChange) {
        return toJson(replicaViewChange).toString();
    }

    @Override
    public String encodeNewView(ReplicaNewView replicaNewView) {
        return toJson(replicaNewView).toString();
    }
}
