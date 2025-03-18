import Mockups.IntegerMove;
import Mockups.IntegerMoveByteDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.DerivedImplementations.IDeriveByteUtils;
import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TestCollectionByteDecoder {

    private Collection<IntegerMove> moves = null;

    private final IDeriveByteUtils<IntegerMove> byteUtils = new DeriveByteUtils<>();

    private final ByteDecoder<Collection<IntegerMove>> moveCollectionByteDecoder = byteUtils.collectionByteDecoder(new IntegerMoveByteDecoder());

    private final ByteEncoder<Collection<IntegerMove>> moveCollectionByteEncoder = byteUtils.collectionByteEncoder(new PlainByteEncoder<>());

    private void setUp(){
        moves = new ArrayList<>();
    }

    @Test
    public void testEmptyCollection() throws IOException {
        setUp();
        byte[] encoded = moveCollectionByteEncoder.toBytes(moves);
        var result = moveCollectionByteDecoder.fromBytes(encoded);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testNotEmptyCollection() throws IOException {

        var values = Set.of(2, 3, 4, 42);

        setUp();

        for(var v: values)
            moves.add(new IntegerMove(v));

        byte[] encoded = moveCollectionByteEncoder.toBytes(moves);
        var result = moveCollectionByteDecoder.fromBytes(encoded)
                .stream()
                .map(v -> v.value).collect(Collectors.toCollection(HashSet::new));

        Assertions.assertTrue(result.containsAll(values));
        Assertions.assertEquals(values.size(), result.size());
    }
}
