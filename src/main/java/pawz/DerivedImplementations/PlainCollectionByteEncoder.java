package pawz.DerivedImplementations;

import pawz.Tournament.Interfaces.ByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class PlainCollectionByteEncoder<T> implements ByteEncoder<Collection<T>> {

    private final ByteEncoder<T> itemEncoder;

    public PlainCollectionByteEncoder(ByteEncoder<T> itemEncoder) {
        this.itemEncoder = itemEncoder;
    }

    @Override
    public byte[] toBytes(Collection<T> collection) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        try {
            stream.writeInt(collection.size());

            for(var item: collection){
                byte[] itemBytes = itemEncoder.toBytes(item);
                stream.writeInt(itemBytes.length);
                byteArrayOutputStream.write(itemBytes);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
