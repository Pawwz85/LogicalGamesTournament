package pawz.P2PClient.ResultParsers;

import com.google.gson.JsonObject;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

public class PuzzleServiceResultParser<Move extends ByteEncodable, State extends ByteEncodable> {

    private final ByteDecoder<Puzzle<Move, State>> puzzleByteDecoder;
    private final ByteDecoder<Collection<Puzzle<Move, State>>> collectionByteDecoder;

    public PuzzleServiceResultParser(ByteDecoder<Puzzle<Move, State>> puzzleByteDecoder) {
        this.puzzleByteDecoder = puzzleByteDecoder;
        DeriveByteUtils<Puzzle<Move, State>> deriveByteUtils = new DeriveByteUtils<>();
        this.collectionByteDecoder = deriveByteUtils.collectionByteDecoder(puzzleByteDecoder);
    }

    public Optional<Puzzle<Move, State>> getPuzzleById(JsonObject APIResult){
        try{
            int statusCode = APIResult.get("http_status_code").getAsInt();
            if(statusCode == 200){
                String puzzleBase64 = APIResult.get("puzzle").getAsString();
                byte[] bytes = Base64.getDecoder().decode(puzzleBase64);
                return Optional.of(puzzleByteDecoder.fromBytes(bytes));
            }
        } catch (Exception ignored){}
        return Optional.empty();
    }

    public Collection<Puzzle<Move, State>> getAllPuzzles(JsonObject APIResult){
        try{
            int statusCode = APIResult.get("http_status_code").getAsInt();
            if(statusCode == 200){
                String puzzleBase64 = APIResult.get("puzzles").getAsString();
                byte[] bytes = Base64.getDecoder().decode(puzzleBase64);
                return collectionByteDecoder.fromBytes(bytes);
            }
        } catch (Exception ignored){}
        return new ArrayList<>();
    }

}
