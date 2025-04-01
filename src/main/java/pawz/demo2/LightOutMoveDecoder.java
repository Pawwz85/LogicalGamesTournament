package pawz.demo2;

import pawz.Tournament.Interfaces.ByteDecoder;

import java.io.IOException;

public class LightOutMoveDecoder implements ByteDecoder<LightOutMove> {
    @Override
    public LightOutMove fromBytes(byte[] bytes) throws IOException {
        return new LightOutMove(bytes[0]);
    }
}
