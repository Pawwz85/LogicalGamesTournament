package pawz.DerivedImplementations;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.util.Collection;

public interface IDeriveByteUtils<T> {

    ByteEncoder<Collection<T>> collectionByteEncoder(ByteEncoder<T> itemByteEncoder);
    ByteDecoder<Collection<T>> collectionByteDecoder(ByteDecoder<T> itemByteDecoder);
    CollectionChecksum<T> collectionChecksum(ByteEncoder<T> itemByteEncoder);

}
