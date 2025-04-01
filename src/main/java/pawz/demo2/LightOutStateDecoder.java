package pawz.demo2;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.IOException;
import java.util.Arrays;

public class LightOutStateDecoder implements ByteDecoder<LightOutState> {
    @Override
    public LightOutState fromBytes(byte[] bytes) throws IOException {
        LightOutState result = new LightOutState();

        for(int i = 0; i < 9; ++i){
            result.board[i] = bytes[i];
        }

        return result;
    }
}
