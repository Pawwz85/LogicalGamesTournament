package pawz.Tournament.Interfaces;

public interface ByteEncoder<T> {
    byte[] toBytes(T object);
}
