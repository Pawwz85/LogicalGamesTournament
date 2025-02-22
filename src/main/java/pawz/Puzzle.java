package pawz;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Puzzle<Move extends ByteEncodable, State extends ByteEncodable> implements ByteEncodable{
    public State  state;
    public int puzzleId;

    public Puzzle(@NotNull State state){
        this.state = state;
        puzzleId = -1;
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        byte[] stateBytes = state.toBytes();
        try {
            dataOutputStream.writeInt(puzzleId);
            dataOutputStream.writeInt(stateBytes.length);
            outputStream.write(stateBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }
}
