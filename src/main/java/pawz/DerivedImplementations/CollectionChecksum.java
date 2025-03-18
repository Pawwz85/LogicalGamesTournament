package pawz.DerivedImplementations;

import org.bouncycastle.crypto.digests.MD5Digest;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionChecksum<T> {

    private final ByteEncoder<T> itemByteEncoder;

    public CollectionChecksum(ByteEncoder<T> itemByteEncoder) {
        this.itemByteEncoder = itemByteEncoder;
    }

    private static byte[] hashBytes(byte[] input) {
        MD5Digest digest = new MD5Digest();
        digest.update(input, 0, input.length);
        byte[] hash = new byte[digest.getDigestSize()]; // 16 bytes
        digest.doFinal(hash, 0);
        return hash;
    }

    private byte[] hashItem(T item){
        byte[] itemBytes = itemByteEncoder.toBytes(item);
        return hashBytes(itemBytes);
    }

    private static byte[] xorChecksums(List<byte[]> checksums) {
        if (checksums.isEmpty()) {
            return new byte[16]; // Return zeroed MD5-sized array if empty
        }

        byte[] result = new byte[16]; // MD5 produces 16-byte hashes
        for (byte[] checksum : checksums) {
            for (int i = 0; i < 16; ++i) {
                result[i] ^= checksum[i];
            }
        }
        return result;
    }

    public byte[] calculateChecksum(Collection<T> collection){
        var checksums = collection.stream()
                .map(this::hashItem)
                .collect(Collectors.toList());
        return xorChecksums(checksums);
    }

}
