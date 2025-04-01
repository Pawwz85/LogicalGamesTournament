package pawz.demo2;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

public class LightOutState implements ByteEncodable {

    // The state is 3x3 board that contains value in the 0/1 range

    public final int[] board = new int[9];


    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(9);
        for(int i = 0; i < 9; ++i)
            byteArrayOutputStream.write(board[i]);
        return byteArrayOutputStream.toByteArray();
    }
}
