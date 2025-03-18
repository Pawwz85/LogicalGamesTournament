package pawz.DerivedImplementations;

import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.util.Collection;

public class DeriveByteUtils<T> implements IDeriveByteUtils<T>{
    @Override
    public ByteEncoder<Collection<T>> collectionByteEncoder(ByteEncoder<T> itemByteEncoder) {
        return new PlainCollectionByteEncoder<>(itemByteEncoder);
    }

    @Override
    public ByteDecoder<Collection<T>> collectionByteDecoder(ByteDecoder<T> itemByteDecoder) {
        return new PlainCollectionByteDecoder<>(itemByteDecoder);
    }

    @Override
    public CollectionChecksum<T> collectionChecksum(ByteEncoder<T> itemByteEncoder) {
        return new CollectionChecksum<>(itemByteEncoder);
    }
}
