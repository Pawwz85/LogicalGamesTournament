package pawz.demo;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SudokuMove implements ByteEncodable {

    public final int value;
    public final int squareId;

    public SudokuMove(int value, int squareId) {
        this.value = value;
        this.squareId = squareId;
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
        try {
            stream.writeInt(value);
            stream.writeInt(squareId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
