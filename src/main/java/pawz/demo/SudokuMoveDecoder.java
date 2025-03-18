package pawz.demo;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SudokuMoveDecoder implements ByteDecoder<SudokuMove> {
    @Override
    public SudokuMove fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        return new SudokuMove(stream.readInt(), stream.readInt());
    }
}
