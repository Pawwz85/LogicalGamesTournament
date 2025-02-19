package AbstractBehavioralTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class TestByteDecoder<T extends ByteEncodable> {
    public final ByteDecoder<T> byteDecoder;
    public final Collection<T> testData;

    protected TestByteDecoder(ByteDecoder<T> byteDecoder, Collection<T> testData) {
        this.byteDecoder = byteDecoder;
        this.testData = testData;
    }

    @Test
    public void decoderCanCloneObjects(){

        for(T dataPoint: testData){
            try {
                T clone = byteDecoder.fromBytes(dataPoint.toBytes());
                Assertions.assertArrayEquals(dataPoint.toBytes(), clone.toBytes());
                Assertions.assertNotSame(clone, dataPoint);
            } catch (IOException e) {
                fail(e);
            }
        }
    }

}
