package pawz.Solitaire;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SudokuStateDecoder implements ByteDecoder<SudokuState> {
    @Override
    public SudokuState fromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        int[] fields = new int[81];

        for(int i = 0; i < 81; ++i)
            fields[i] = stream.readInt();

        return new SudokuState(fields);
    }
}
