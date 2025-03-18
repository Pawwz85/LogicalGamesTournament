package Replika;

import com.gmail.woodyc40.pbft.message.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Auth.SignedMessage;
import pawz.Tournament.Replika.ReplikaDecoder;
import pawz.Tournament.Replika.ReplikaEncoder;
import utils.json.JsonFieldMalformationAPI;
import utils.json.JsonMalformationGenerator;
import utils.json.JsonMalformationRulesFlags;

import java.util.HashMap;
import java.util.Map;

public class TestReplicaDecoder {

    private final ReplikaDecoder decoder = new ReplikaDecoder();
    private final ReplikaEncoder encoder = new ReplikaEncoder();

    private final JsonParser jsonParser = new JsonParser();

    @Test
    public void parseValidRequest() throws ReplikaDecoder.ParsingException {
        byte[] payload = "Example payload".getBytes();
        byte[] signature = "Pretend this is signature".getBytes();
        SignedMessage<byte[]> message = new SignedMessage<>(signature, "someToken", payload);
        ReplicaRequest<SignedMessage<byte[]>> expected = new DefaultReplicaRequest<>(
                message,
                System.currentTimeMillis(),
                "example_id"
        );

        String encodedRequest = encoder.encodeRequest(expected);
        JsonObject temp = jsonParser.parse(encodedRequest).getAsJsonObject();

        var parsedRequest = decoder.parseRequest(temp);

        assertRequestEquals(expected, parsedRequest);
    }

    private static void assertRequestEquals(ReplicaRequest<SignedMessage<byte[]>> expected, ReplicaRequest<SignedMessage<byte[]>> parsedRequest) {
        SignedMessage<byte[]> message = expected.operation();
        Assertions.assertEquals(expected.clientId(), parsedRequest.clientId());
        Assertions.assertEquals(expected.timestamp(), parsedRequest.timestamp());

        Assertions.assertEquals(message.sessionToken(), parsedRequest.operation().sessionToken());
        Assertions.assertArrayEquals(message.payload(), parsedRequest.operation().payload());
        Assertions.assertArrayEquals(message.signature(), parsedRequest.operation().signature());
    }

    @Test
    public void parseInvalidRequest() {
        byte[] payload = "Example payload".getBytes();
        byte[] signature = "Pretend this is signature".getBytes();
        SignedMessage<byte[]> message = new SignedMessage<>(signature, "someToken", payload);
        ReplicaRequest<SignedMessage<byte[]>> validReference = new DefaultReplicaRequest<>(
                message,
                System.currentTimeMillis(),
                "example_id"
        );
        String encodedRequest = encoder.encodeRequest(validReference);
        JsonObject temp = jsonParser.parse(encodedRequest).getAsJsonObject();

        Map<String, JsonFieldMalformationAPI> rules = new HashMap<>();

        var clientIdRules = new JsonFieldMalformationAPI();
        clientIdRules.clearFlag(JsonMalformationRulesFlags.TestString);
        clientIdRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var timestampRules = new JsonFieldMalformationAPI();
        timestampRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        rules.put("client_id", clientIdRules);
        rules.put("timestamp", clientIdRules);
        rules.put("operation", new JsonFieldMalformationAPI());

        JsonMalformationGenerator generator = new JsonMalformationGenerator(temp, rules);

        for(var malformation: generator.generateMalformations()){
            Assertions.assertThrows(ReplikaDecoder.ParsingException.class, () -> decoder.parseRequest(malformation.MalformedJson), malformation.Context);
        }
    }

    @Test
    public void parseValidPrePrepare() throws ReplikaDecoder.ParsingException {

        byte[] payload = "Example payload".getBytes();
        byte[] signature = "Pretend this is signature".getBytes();
        SignedMessage<byte[]> message = new SignedMessage<>(signature, "someToken", payload);

        ReplicaRequest<SignedMessage<byte[]>> request = new DefaultReplicaRequest<>(
                message,
                System.currentTimeMillis(),
                "example_id"
        );

        ReplicaPrePrepare<SignedMessage<byte[]>> expected = new DefaultReplicaPrePrepare<>(
                2,
                42,
                "Pretend this is a valid digest".getBytes(),
                request
        );


        String encodedMessage = encoder.encodePrePrepare(expected);
        JsonObject temp = jsonParser.parse(encodedMessage).getAsJsonObject();

        var parsedMsg = decoder.parsePrePrepare(temp);

        Assertions.assertEquals(expected.viewNumber(), parsedMsg.viewNumber());
        Assertions.assertEquals(expected.seqNumber(), parsedMsg.seqNumber());
        Assertions.assertArrayEquals(expected.digest(), parsedMsg.digest());
        assertRequestEquals(expected.request(), parsedMsg.request());
    }

    @Test
    public void testMalformedPrePrepare(){
        byte[] payload = "Example payload".getBytes();
        byte[] signature = "Pretend this is signature".getBytes();
        SignedMessage<byte[]> message = new SignedMessage<>(signature, "someToken", payload);

        ReplicaRequest<SignedMessage<byte[]>> request = new DefaultReplicaRequest<>(
                message,
                System.currentTimeMillis(),
                "example_id"
        );

        ReplicaPrePrepare<SignedMessage<byte[]>> reference = new DefaultReplicaPrePrepare<>(
                2,
                42,
                "Pretend this is a valid digest".getBytes(),
                request
        );

        String encodedMessage = encoder.encodePrePrepare(reference);
        JsonObject temp = jsonParser.parse(encodedMessage).getAsJsonObject();

        Map<String, JsonFieldMalformationAPI> rules = new HashMap<>();
        var viewNumberRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var seqNumberRules = new JsonFieldMalformationAPI();
        seqNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        rules.put("view_number", viewNumberRules);
        rules.put("seq_number", seqNumberRules);
        rules.put("digest", new JsonFieldMalformationAPI());

        JsonMalformationGenerator generator = new JsonMalformationGenerator(temp, rules);
        for(var malformation: generator.generateMalformations()){
            Assertions.assertThrows(ReplikaDecoder.ParsingException.class, () -> decoder.parseRequest(malformation.MalformedJson), malformation.Context);
        }

    }

    @Test
    public void parseValidPrepare() throws ReplikaDecoder.ParsingException {
        ReplicaPrepare expected = new DefaultReplicaPrepare(7, 42, "Pretend this is digest ;)".getBytes(), 13);
        String encoded = encoder.encodePrepare(expected);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        var parsedPrepare = decoder.parsePrepare(temp);

        Assertions.assertEquals(expected.viewNumber(), parsedPrepare.viewNumber());
        Assertions.assertEquals(expected.seqNumber(), parsedPrepare.seqNumber());
        Assertions.assertEquals(expected.replicaId(), parsedPrepare.replicaId());
        Assertions.assertArrayEquals(expected.digest(), parsedPrepare.digest());
    }

    @Test
    public void parseMalformedPrepare() {
        ReplicaPrepare reference = new DefaultReplicaPrepare(7, 42, "Pretend this is digest ;)".getBytes(), 13);
        String encoded = encoder.encodePrepare(reference);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        Map<String, JsonFieldMalformationAPI> rules = new HashMap<>();
        var viewNumberRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var seqNumberRules = new JsonFieldMalformationAPI();
        seqNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var replicaIdRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        rules.put("view_number", viewNumberRules);
        rules.put("seq_number", seqNumberRules);
        rules.put("replica_id", replicaIdRules);
        rules.put("digest", new JsonFieldMalformationAPI());

        JsonMalformationGenerator generator = new JsonMalformationGenerator(temp, rules);
        for(var malformation: generator.generateMalformations()){
            Assertions.assertThrows(ReplikaDecoder.ParsingException.class, () -> decoder.parseRequest(malformation.MalformedJson), malformation.Context);
        }
    }

    @Test
    public void parseValidCommit() throws ReplikaDecoder.ParsingException {
        ReplicaCommit expected = new DefaultReplicaCommit(7, 42, "Pretend this is digest ;)".getBytes(), 13);
        String encoded = encoder.encodeCommit(expected);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        var parsedPrepare = decoder.parsePrepare(temp);

        Assertions.assertEquals(expected.viewNumber(), parsedPrepare.viewNumber());
        Assertions.assertEquals(expected.seqNumber(), parsedPrepare.seqNumber());
        Assertions.assertEquals(expected.replicaId(), parsedPrepare.replicaId());
        Assertions.assertArrayEquals(expected.digest(), parsedPrepare.digest());
    }

    @Test
    public void parseMalformedCommit() {
        ReplicaCommit reference = new DefaultReplicaCommit(7, 42, "Pretend this is digest ;)".getBytes(), 13);
        String encoded = encoder.encodeCommit(reference);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        Map<String, JsonFieldMalformationAPI> rules = new HashMap<>();
        var viewNumberRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var seqNumberRules = new JsonFieldMalformationAPI();
        seqNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var replicaIdRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        rules.put("view_number", viewNumberRules);
        rules.put("seq_number", seqNumberRules);
        rules.put("replica_id", replicaIdRules);
        rules.put("digest", new JsonFieldMalformationAPI());

        JsonMalformationGenerator generator = new JsonMalformationGenerator(temp, rules);
        for(var malformation: generator.generateMalformations()){
            Assertions.assertThrows(ReplikaDecoder.ParsingException.class, () -> decoder.parseRequest(malformation.MalformedJson), malformation.Context);
        }
    }

    @Test
    public void parseValidReply() throws ReplikaDecoder.ParsingException {
        ReplicaReply<JsonObject> expected = new DefaultReplicaReply<>(2, System.currentTimeMillis(), "client_id", 2, new JsonObject());
        String encoded = encoder.encodeReply(expected);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        var parsedReply = decoder.parseReply(temp);

        Assertions.assertEquals(expected.clientId(), parsedReply.clientId());
        Assertions.assertEquals(expected.replicaId(), parsedReply.replicaId());
        Assertions.assertEquals(expected.viewNumber(), parsedReply.viewNumber());
        Assertions.assertEquals(expected.timestamp(), parsedReply.timestamp());
    }

    @Test
    public void parseMalformedReply(){
        ReplicaReply<JsonObject> reference =  new DefaultReplicaReply<>(2, System.currentTimeMillis(), "client_id", 2, new JsonObject());
        String encoded = encoder.encodeReply(reference);
        JsonObject temp = jsonParser.parse(encoded).getAsJsonObject();

        Map<String, JsonFieldMalformationAPI> rules = new HashMap<>();
        var viewNumberRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var timestampRules = new JsonFieldMalformationAPI();
        timestampRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var replicaIdRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestInteger);

        var resultRules = new JsonFieldMalformationAPI();
        viewNumberRules.clearFlag(JsonMalformationRulesFlags.TestObject);

        rules.put("view_number", viewNumberRules);
        rules.put("timestamp", timestampRules);
        rules.put("replica_id", replicaIdRules);
        rules.put("result", resultRules);

        JsonMalformationGenerator generator = new JsonMalformationGenerator(temp, rules);
        for(var malformation: generator.generateMalformations()){
            Assertions.assertThrows(ReplikaDecoder.ParsingException.class, () -> decoder.parseRequest(malformation.MalformedJson), malformation.Context);
        }
    }

    // TODO: write similar test for checkpoint, newView and view change
}
