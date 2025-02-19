package pawz.Tournament.Interfaces;

import java.io.IOException;

public interface ByteDecoder<T>{
    T fromBytes(byte[] bytes) throws IOException;
}
