package pawz.P2PClient;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pawz.Auth.DefaultSigner;
import pawz.Auth.SignedMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PrivateKey;

public class SignedMessageFactory {

    private final String sessionToken;

    private final PrivateKey privateKey;

    private final static DefaultSigner signer = new DefaultSigner();

    public SignedMessageFactory(String sessionToken, PrivateKey privateKey) {
        this.sessionToken = sessionToken;
        this.privateKey = privateKey;
    }


    private byte[] encodeString(String uri){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        try {
            stream.writeUTF(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }



    private byte[] encodePayload(String uri, @Nullable JsonObject payload){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        byte[] uriBytes = encodeString(uri);
        byte[] payloadBytes = (payload != null)? encodeString(payload.toString()): new byte[0];

        int uriSize = uriBytes.length;
        int payloadSize = uriBytes.length;

        try{
            stream.writeInt(uriSize);
            stream.writeInt(payloadSize);
            byteArrayOutputStream.write(uriBytes);
            byteArrayOutputStream.write(payloadBytes);
        }catch (IOException e){
            throw new RuntimeException();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public SignedMessage<byte[]> makeSignedRequest(
            @NotNull String path,
            @Nullable JsonObject payload
    ){
        byte[] payloadBytes = encodePayload(path, payload);
        byte[] signature = signer.sign(payloadBytes, privateKey);
        return new SignedMessage<>(signature, sessionToken, payloadBytes);
    }

}
