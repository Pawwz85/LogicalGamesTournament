package pawz.Tournament;

import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;

public class PlainByteEncoder<T extends ByteEncodable> implements ByteEncoder<T> {
    @Override
    public byte[] toBytes(T object) {
        return object.toBytes();
    }
}
