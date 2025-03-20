package pawz.P2PClient;

import com.gmail.woodyc40.pbft.message.ClientRequest;
import com.google.gson.JsonObject;
import pawz.Auth.SignedMessage;
import pawz.Auth.SignedMessageUtil;

import java.util.Base64;

public class ClientEncoder implements com.gmail.woodyc40.pbft.ClientEncoder<SignedMessage<byte[]>, String> {

    private final static SignedMessageUtil<byte[]> signedMsgUtils = new SignedMessageUtil<>();

    private String toBase64(SignedMessage<byte[]> message){
        byte[] bytes = signedMsgUtils.toBytes(message, b -> b);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public String encodeRequest(ClientRequest<SignedMessage<byte[]>> request) {

        SignedMessage<byte[]> op = request.operation();

        long timestamp = request.timestamp();
        String clientId = request.client().clientId();
        JsonObject root = new JsonObject();
        root.addProperty("type", "request");

        root.addProperty("operation", toBase64(op));
        root.addProperty("timestamp", timestamp);
        root.addProperty("client", clientId);
        return root.toString();
    }
}
