package utils.json;

import java.util.Enumeration;
import java.util.Iterator;

public class BitMaskEnumeration implements Enumeration<Integer> {

    private int mask = 1;
    private final int valueSet;
    private final int bitLimit;

    BitMaskEnumeration(int valueSet, int bitLimit) {
        this.valueSet = valueSet;
        this.bitLimit = bitLimit;
    }

    @Override
    public boolean hasMoreElements() {
        return mask != (1<<bitLimit);
    }

    @Override
    public Integer nextElement() {
        int result = mask & valueSet;
        mask <<= 1;
        return result;
    }

    @Override
    public Iterator<Integer> asIterator() {
        return Enumeration.super.asIterator();
    }
}