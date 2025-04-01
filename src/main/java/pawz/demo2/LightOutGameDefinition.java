package pawz.demo2;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.Arrays;

public class LightOutGameDefinition implements GameDefinition<LightOutMove, LightOutState> {

    private final static ByteDecoder<LightOutState> stateByteDecoder = new LightOutStateDecoder();
    private final static ByteDecoder<LightOutMove> moveByteDecoder = new LightOutMoveDecoder();

    @Override
    public boolean isAcceptable(@NotNull LightOutState s) {
        return Arrays.stream(s.board).allMatch(i -> i == 1);
    }

    @Override
    public boolean isMoveLegal(@NotNull LightOutState s, @NotNull LightOutMove m) {
        return 0 <= m.cellID && 9 > m.cellID;
    }

    @NotNull
    @Override
    public LightOutState makeMove(@NotNull LightOutState s, @NotNull LightOutMove m) {
        LightOutState derived = new LightOutState();
        System.arraycopy(s.board, 0, derived.board, 0, 9);


        for(int i = 0; i < 9; ++i){
            int cabDistance = Math.abs((i%3) - (m.cellID%3)) + Math.abs((i/3) - (m.cellID/3));
            if(cabDistance <= 1)
                derived.board[i] = (derived.board[i] + 1) % 2;
        }


        return derived;
    }

    @Override
    public ByteDecoder<LightOutMove> moveByteDecoder() {
        return moveByteDecoder;
    }

    @Override
    public ByteDecoder<LightOutState> stateByteDecoder() {
        return stateByteDecoder;
    }
}
