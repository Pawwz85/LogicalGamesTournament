import Mockups.IntegerMove;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pawz.DerivedImplementations.CollectionChecksum;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.DerivedImplementations.PlainByteEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCollectionChecksum {
    private final CollectionChecksum<IntegerMove> checksum = new DeriveByteUtils<IntegerMove>().collectionChecksum(new PlainByteEncoder<IntegerMove>());

    @Test
    public void testEmptyCollectionThrowsNoException(){
        List<IntegerMove> collection = new ArrayList<>();
        Assertions.assertDoesNotThrow(() -> checksum.calculateChecksum(collection));
    }

    @Test
    public void testOrderDoesNotChangeChecksum(){
        int[] moves = {1, 2, 3};

        List<IntegerMove> list1 = new ArrayList<>();
        list1.add(new IntegerMove(1));
        list1.add(new IntegerMove(2));
        list1.add(new IntegerMove(3));

        List<IntegerMove> list2 = new ArrayList<>();
        list2.add(new IntegerMove(3));
        list2.add(new IntegerMove(2));
        list2.add(new IntegerMove(1));

        Assertions.assertArrayEquals(checksum.calculateChecksum(list1), checksum.calculateChecksum(list2));
    }

    @Test
    public void testChecksumDifferNumber(){
        int[] moves = {1, 2, 3};

        List<IntegerMove> list1 = new ArrayList<>();
        list1.add(new IntegerMove(1));
        list1.add(new IntegerMove(2));
        list1.add(new IntegerMove(3));

        List<IntegerMove> list2 = new ArrayList<>();
        list2.add(new IntegerMove(3));
        list2.add(new IntegerMove(42));
        list2.add(new IntegerMove(1));

        Assertions.assertFalse(Arrays.equals(checksum.calculateChecksum(list1), checksum.calculateChecksum(list2)));
    }

    @Test
    public void testChecksumMissingNumber(){
        int[] moves = {1, 2, 3};

        List<IntegerMove> list1 = new ArrayList<>();
        list1.add(new IntegerMove(1));
        list1.add(new IntegerMove(2));
        list1.add(new IntegerMove(3));

        List<IntegerMove> list2 = new ArrayList<>();
        list2.add(new IntegerMove(3));
        list2.add(new IntegerMove(1));

        Assertions.assertFalse(Arrays.equals(checksum.calculateChecksum(list1), checksum.calculateChecksum(list2)));
    }
}
