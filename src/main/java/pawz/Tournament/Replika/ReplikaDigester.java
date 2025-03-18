package pawz.Tournament.Replika;

import com.gmail.woodyc40.pbft.ReplicaDigester;
import com.gmail.woodyc40.pbft.message.ReplicaRequest;
import pawz.Auth.SignedMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReplikaDigester implements ReplicaDigester<SignedMessage<byte[]>> {

    private static final MessageDigest digester;

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] digest(ReplicaRequest<SignedMessage<byte[]>> replicaRequest) {
        digester.reset();

        try {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        stream.writeUTF(replicaRequest.clientId());
        stream.writeLong(replicaRequest.timestamp());

        SignedMessage<byte[]> operation = replicaRequest.operation();
        if (operation != null) {
            byteArrayOutputStream.write(operation.signature());
            byteArrayOutputStream.write(operation.payload());
        }

        byte[] rawRequest = byteArrayOutputStream.toByteArray();
            digester.update(rawRequest, 0, rawRequest.length);
        } catch (IOException e){
            throw new RuntimeException();
        }

        return digester.digest();
    }
}
