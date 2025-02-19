package Mockups;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SimpleArithmeticPuzzleStateByteDecoder implements ByteDecoder<SimpleArithmeticPuzzleState> {
    @Override
    public SimpleArithmeticPuzzleState fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream buff = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(buff);
        int goal = stream.readInt();
        int currentValue = stream.readInt();
        return new SimpleArithmeticPuzzleState(goal, currentValue);
    }
}
