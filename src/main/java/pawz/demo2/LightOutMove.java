package pawz.demo2;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.io.ByteArrayOutputStream;

public class LightOutMove implements ByteEncodable {
    public final int cellID;

    public LightOutMove(int cellID) {
        this.cellID = cellID;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[1];
        result[0] = (byte)cellID;
        return result;
    }
}
