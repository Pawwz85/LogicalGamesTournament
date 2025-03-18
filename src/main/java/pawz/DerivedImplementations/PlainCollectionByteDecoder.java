package pawz.DerivedImplementations;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlainCollectionByteDecoder<T> implements ByteDecoder<Collection<T>> {

    private final ByteDecoder<T> itemByteDecoder;

    public PlainCollectionByteDecoder(ByteDecoder<T> itemByteDecoder) {
        this.itemByteDecoder = itemByteDecoder;
    }

    @Override
    public Collection<T> fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        List<T> result = new ArrayList<>();
        int itemCount = stream.readInt();

        for(int i = 0; i<itemCount; ++i){
            int itemSize = stream.readInt();
            byte[] itemBytes = stream.readNBytes(itemSize);
            result.add(itemByteDecoder.fromBytes(itemBytes));
        }

        return result;
    }
}
