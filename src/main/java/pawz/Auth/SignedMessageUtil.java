package pawz.Auth;

import java.io.*;
import java.util.function.Function;

public class SignedMessageUtil<Signature> {

    public byte[] toBytes(SignedMessage<Signature> message, Function<Signature, byte[]> serializer){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        try {
            stream.writeUTF(message.sessionToken());

            byte[] signature = serializer.apply(message.signature());
            stream.writeShort(signature.length);
            stream.write(signature);
            stream.writeShort(message.payload().length);
            stream.write(message.payload());
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public SignedMessage<Signature> fromBytes(byte[] bytes, Function<byte[], Signature> deserializer){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        SignedMessage<Signature> result;
        try {
            String token = stream.readUTF();
            byte[] signature = stream.readNBytes(stream.readShort());
            byte[] payload = stream.readNBytes(stream.readShort());

            return new SignedMessage<>(deserializer.apply(signature), token, payload);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
}
