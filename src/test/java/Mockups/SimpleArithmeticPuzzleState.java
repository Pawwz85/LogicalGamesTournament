package Mockups;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class SimpleArithmeticPuzzleState implements ByteEncodable {

    public final int goal;
    public final int currentValue;

    public SimpleArithmeticPuzzleState(int goal, int currentValue) {
        this.goal = goal;
        this.currentValue = currentValue;
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(buff);
        try {
            stream.writeInt(goal);
            stream.writeInt(currentValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toByteArray();
    }
}
