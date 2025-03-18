package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pawz.Auth.SignedMessage;
import pawz.Auth.SignedMessageUtil;

import java.util.*;


// Todo: test encoding/decoding logic and its various corner case
public class ReplikaDecoder {

    public static class ParsingException extends Exception{}

    private static final SignedMessageUtil<byte[]> signedMessageUtil = new SignedMessageUtil<>();

    public ReplicaRequest<SignedMessage<byte[]>> parseRequest(JsonObject jsonObject) throws ParsingException {

        if(!jsonObject.has("operation") || !jsonObject.has("timestamp") || !jsonObject.has("client_id"))
            throw new ParsingException();

        String operationBase64;
        long timestamp;
        String clientId;

        try{
            operationBase64 = jsonObject.get("operation").getAsString();
            timestamp = jsonObject.get("timestamp").getAsLong();
            clientId = jsonObject.get("client_id").getAsString();
        } catch (Exception e){
            throw new ParsingException();
        }

        SignedMessage<byte[]> op;
        try{
            byte[] opBytes = Base64.getDecoder().decode(operationBase64);
            op = signedMessageUtil.fromBytes(opBytes, b -> b);
        } catch (Exception e){
            throw new ParsingException();
        }

        return new DefaultReplicaRequest<>(op, timestamp, clientId);
    }

    public ReplicaPrePrepare<SignedMessage<byte[]>> parsePrePrepare(JsonObject jsonObject) throws ParsingException {
        long seqNumber;
        int viewNumber;
        String digestBase64;
        JsonObject request;

        if(!jsonObject.has("seq_number") || !jsonObject.has("view")
                || !jsonObject.has("digest") || !jsonObject.has("request"))
            throw new ParsingException();

        try {
            seqNumber = jsonObject.get("seq_number").getAsLong();
            viewNumber = jsonObject.get("view").getAsInt();
            digestBase64 = jsonObject.get("digest").getAsString();
            request = jsonObject.get("request").getAsJsonObject();
        } catch (Exception e){
            throw new ParsingException();
        }

        byte[] digest;
        try{
            digest = Base64.getDecoder().decode(digestBase64);
        } catch (Exception e){
            throw  new ParsingException();
        }

        return new DefaultReplicaPrePrepare<>(viewNumber, seqNumber, digest, parseRequest(request));
    }

    public ReplicaPrepare parsePrepare(JsonObject jsonObject) throws ParsingException {
        HelperClass result = parsePhaseMessage(jsonObject);
        return new DefaultReplicaPrepare(result.viewNumber, result.seqNumber, result.digest, result.replicaId);
    }

    public ReplicaCommit parseCommit(JsonObject jsonObject) throws ParsingException {
        HelperClass result = parsePhaseMessage(jsonObject);
        return new DefaultReplicaCommit(result.viewNumber, result.seqNumber, result.digest, result.replicaId);
    }

    @NotNull
    private static HelperClass parsePhaseMessage(JsonObject jsonObject) throws ParsingException {
        long seqNumber;
        int viewNumber;
        String digestBase64;
        int replicaId;

        if(!jsonObject.has("seq_number") || !jsonObject.has("view")
                || !jsonObject.has("digest") || !jsonObject.has("replica_id"))
            throw new ParsingException();

        try {
            seqNumber = jsonObject.get("seq_number").getAsLong();
            viewNumber = jsonObject.get("view").getAsInt();
            digestBase64 = jsonObject.get("digest").getAsString();
            replicaId = jsonObject.get("replica_id").getAsInt();
        } catch (Exception e){
            throw new ParsingException();
        }

        byte[] digest;
        try{
            digest = Base64.getDecoder().decode(digestBase64);
        } catch (Exception e){
            throw  new ParsingException();
        }
        return new HelperClass(seqNumber, viewNumber, replicaId, digest);
    }

    private static class HelperClass {
        public final long seqNumber;
        public final int viewNumber;
        public final int replicaId;
        public final byte[] digest;

        public HelperClass(long seqNumber, int viewNumber, int replicaId, byte[] digest) {
            this.seqNumber = seqNumber;
            this.viewNumber = viewNumber;
            this.replicaId = replicaId;
            this.digest = digest;
        }
    }

    public ReplicaReply<JsonObject> parseReply(JsonObject jsonObject) throws ParsingException {
        int viewNumber;
        long timestamp;
        String clientId;
        int replicaId;
        JsonObject result;


        if(!jsonObject.has("view") || !jsonObject.has("timestamp")
        || !jsonObject.has("client_id") || !jsonObject.has("replica_id")
        || !jsonObject.has("result"))
            throw new ParsingException();

        try{
            viewNumber = jsonObject.get("view").getAsInt();
            timestamp = jsonObject.get("timestamp").getAsLong();
            clientId = jsonObject.get("client_id").getAsString();
            replicaId = jsonObject.get("replica_id").getAsInt();
            result = jsonObject.get("result").getAsJsonObject();
        } catch (Exception e){
            throw new ParsingException();
        }

        return new DefaultReplicaReply<> (viewNumber, timestamp, clientId, replicaId, result);
    }

    public ReplicaCheckpoint parseCheckpoint(JsonObject jsonObject) throws ParsingException {
        long lastSeqNumber;
        String digestBase64;
        int replicaId;

        if(!jsonObject.has("last_seq_number") || !jsonObject.has("digest")
            || !jsonObject.has("replica_id"))
            throw new ParsingException();

        try{
            lastSeqNumber = jsonObject.get("last_seq_number").getAsInt();
            digestBase64 = jsonObject.get("digest").getAsString();
            replicaId = jsonObject.get("replica_id").getAsInt();
        } catch (Exception e){
            throw new ParsingException();
        }

        byte[] digest;

        try{
            digest = Base64.getDecoder().decode(digestBase64);
        } catch (Exception e){
            throw new ParsingException();
        }

        return new DefaultReplicaCheckpoint(lastSeqNumber, digest, replicaId);
    }

    public ReplicaViewChange parseViewChange(JsonObject jsonObject) throws ParsingException {
        int newViewNumber;
        long lastSeqNumber;
        Collection<ReplicaCheckpoint> checkpointProofs = new ArrayList<>();
        Map<Long, Collection<ReplicaPhaseMessage>> preparedProofs = new HashMap<>();

        int replicaId;
        JsonArray checkpointProofsArray;
        JsonArray preparedProofsArray;

        if(!jsonObject.has("new_view_number") || !jsonObject.has("last_seq_number")
        || !jsonObject.has("checkpoint_proofs") || !jsonObject.has("prepared_proofs")
        || !jsonObject.has("replica_id"))
            throw new ParsingException();

        try {
            newViewNumber = jsonObject.get("new_view_number").getAsInt();
            lastSeqNumber = jsonObject.get("last_seq_number").getAsLong();
            replicaId = jsonObject.get("replica_id").getAsInt();
            checkpointProofsArray = jsonObject.getAsJsonArray("checkpoint_proofs");
            preparedProofsArray = jsonObject.getAsJsonArray("prepared_proofs");

            for(int i = 0; i< checkpointProofsArray.size(); ++i)
                checkpointProofs.add(parseCheckpoint(checkpointProofsArray.get(i).getAsJsonObject()));

            for(int i = 0; i< preparedProofsArray.size(); ++i){
                JsonObject proof = preparedProofsArray.get(i).getAsJsonObject();

                if(!proof.has("seq_number"))
                    throw new ParsingException();

                long seq_number = proof.get("seq_number").getAsLong();
                JsonArray msgs = proof.getAsJsonArray("messages");

                List<ReplicaPhaseMessage> messages = new ArrayList<>(msgs.size());
                for(int j = 0; j< msgs.size(); ++j){
                    JsonObject msg = msgs.get(j).getAsJsonObject();
                    if(!msg.has("type"))
                        continue;

                    if(msg.get("type").getAsString().equals("pre_prepare"))
                        messages.add(parsePrePrepare(msg));
                    else if (msg.get("type").getAsString().equals("pre_prepare")) {
                        messages.add(parsePrepare(msg));
                    }
                }
                preparedProofs.put(seq_number, messages);
            }

        } catch (Exception e){
            throw new ParsingException();
        }

        return new DefaultReplicaViewChange(newViewNumber, lastSeqNumber, checkpointProofs, preparedProofs , replicaId);
    }

    public ReplicaNewView parseNewView(JsonObject jsonObject) throws ParsingException{
        int newViewNumber;
        Collection<ReplicaViewChange> viewChangeProofs;
        Collection<ReplicaPrePrepare<?>> preparedProofs;

        if(!jsonObject.has("new_view_number") ||
            !jsonObject.has("view_change_proofs") ||
            !jsonObject.has("prepared_proofs"))
            throw new ParsingException();

        try {
            newViewNumber = jsonObject.get("new_view_number").getAsInt();
            JsonArray viewChangeProofsArray = jsonObject.getAsJsonArray("view_change_proofs");
            JsonArray preparedProofsArray = jsonObject.getAsJsonArray("prepared_proofs");

            viewChangeProofs = new ArrayList<>(viewChangeProofsArray.size());
            for(int i = 0; i < viewChangeProofsArray.size(); ++i)
                viewChangeProofs.add(parseViewChange(viewChangeProofsArray.get(i).getAsJsonObject()));

            preparedProofs = new ArrayList<>(preparedProofsArray.size());
            for(int i = 0; i < preparedProofsArray.size() ; ++i)
                preparedProofs.add(parsePrePrepare(preparedProofsArray.get(i).getAsJsonObject()));
        } catch (Exception e){
            throw new ParsingException();
        }

        return new DefaultReplicaNewView(newViewNumber, viewChangeProofs, preparedProofs);
    }


}
