package pawz.Tournament;

import org.jetbrains.annotations.NotNull;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PuzzleDecoder<Move extends ByteEncodable, State extends ByteEncodable> implements ByteDecoder<Puzzle<Move, State>> {

    private @NotNull final ByteDecoder<State> stateByteDecoder;

    public PuzzleDecoder(@NotNull ByteDecoder<State> stateByteDecoder) {
        this.stateByteDecoder = stateByteDecoder;
    }

    @Override
    public Puzzle<Move, State> fromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);
        Puzzle<Move, State> result = new Puzzle<>();

        int puzzleId = stream.readInt();
        int stateLength = stream.readInt();
        byte[] state = stream.readNBytes(stateLength);

        result.puzzleId = puzzleId;
        result.state = stateByteDecoder.fromBytes(state);

        return result;
    }

}
