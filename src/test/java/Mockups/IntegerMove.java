package Mockups;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class IntegerMove implements ByteEncodable {
    public final int value;

    public IntegerMove(int value) {
        this.value = value;
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(buff);
        try {
            stream.writeInt(this.value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toByteArray();
    }
}
