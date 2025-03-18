package utils.json;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class JsonMalformationRulesFlags {

    public static final int TestNull = 0x01;

    public static final int TestRemove = 0x02;

    public static final int TestInteger = 0x04;

    public static final int TestString = 0x08;

    public static final int TestEmptyArray = 0x10;

    public static final int TestObject = 0x20;


    public static final Enumeration<Integer> enumeration = new BitMaskEnumeration(0x3f, 6);
}
