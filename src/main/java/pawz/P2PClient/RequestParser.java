package pawz.P2PClient;

import com.google.gson.*;
import org.bouncycastle.util.encoders.UTF8;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.IServiceSession;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final JsonParser jsonParser = new JsonParser();


    public static class ParsingException extends Exception{
        public ParsingException(Exception e){
            super(e);
        }

    }

    private Map<String, Object> parsePrimitives(JsonObject o) {
        Map<String, Object> result = new HashMap<>();
        for(Map.Entry<String, JsonElement> entry: o.entrySet()){
            String tag = entry.getKey();
            JsonElement element = entry.getValue();

            if(!element.isJsonPrimitive())
                continue;

            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if(primitive.isBoolean())
                result.put(tag, primitive.getAsBoolean());

            if(primitive.isJsonNull())
                result.put(tag, null);

            if(primitive.isString()){
                result.put(tag, primitive.getAsString());
            }

            if(primitive.isNumber())
                result.put(tag, primitive.getAsNumber());
        }

        return result;
    }

    private Map<String, Object> ParsePayloadFromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        DataInputStream stream1 = new DataInputStream(stream);
        String json = stream1.readUTF();
        JsonElement jsonElement = jsonParser.parse(json);

        if(!jsonElement.isJsonObject()){
            return new HashMap<>();
        }

        JsonObject payload = jsonElement.getAsJsonObject();

        return parsePrimitives(payload);
    };

    private URI parseUriFromBytes(byte[] bytes) throws IOException, URISyntaxException {

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        DataInputStream inputStream = new DataInputStream(stream);

        String uriString = inputStream.readUTF();

        URI result = null;
        result = new URI(null, null, uriString, null);

        return result;
    }

    public Request fromBytes(byte[] bytes, IServiceSession session) throws ParsingException {
        /*
            The byte layout of the Request is:

            int - Uri Size [in bytes]
            int - size of parameters json [in bytes], could be 0 if no payload

           byte * Uri Size - Actual URI
           byte * Param Size - Encoded json object of parameters used in the request
        */
        try {
            byte[] uriBytes;
            Map<String, Object> params = null;

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream stream = new DataInputStream(byteArrayInputStream);

            int uriSize = stream.readInt();
            int payloadSize = stream.readInt();
            uriBytes = stream.readNBytes(uriSize);

            if (payloadSize > 0) {
                byte[] payload = stream.readNBytes(payloadSize);
                params = ParsePayloadFromBytes(payload);
            } else
                params = new HashMap<>();

            return new Request(session, parseUriFromBytes(uriBytes), params);

        } catch (IOException | JsonSyntaxException | URISyntaxException e) {
            throw new ParsingException(e);
        }
    }
}
