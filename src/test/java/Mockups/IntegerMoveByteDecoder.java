package Mockups;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class IntegerMoveByteDecoder implements ByteDecoder<IntegerMove> {
    @Override
    public IntegerMove fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);
        int value = stream.readInt();
        return new IntegerMove(value);
    }
}
