package pawz.Tournament;

import pawz.Tournament.Interfaces.ByteEncodable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PuzzleSolutionDigester<Move extends ByteEncodable> {

    public byte[] digestSolution(int playerID, List<Move> moves){
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        digester.update(String.valueOf(playerID).getBytes(StandardCharsets.UTF_8));

        for (ByteEncodable m : moves)
            digester.update(m.toBytes());

        return digester.digest();
    }
}
