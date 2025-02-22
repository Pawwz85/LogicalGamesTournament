package pawz.P2PClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;
import pawz.Tournament.Replika.LocalPuzzleService;

import java.util.Base64;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class PuzzleServiceController<Move extends ByteEncodable, State extends ByteEncodable> {
    private final LocalPuzzleService<Move, State> service;
    private final ByteEncoder<Puzzle<Move, State>> puzzleByteEncoder;

    public PuzzleServiceController(LocalPuzzleService<Move, State> service, ByteEncoder<Puzzle<Move, State>> puzzleByteEncoder) {
        this.service = service;
        this.puzzleByteEncoder = puzzleByteEncoder;
    }

    public JsonObject getPuzzle(int puzzleId){
        Optional<Puzzle<Move, State>> puzzle = service.getPuzzleById(puzzleId);
        JsonObject response = new JsonObject();
        if(puzzle.isEmpty()){
            response.addProperty("http_status_code", 404);
            response.addProperty("cause", "Specified puzzle do not exists");
        } else {
            response.addProperty("http_status_code", 200);
            response.addProperty("puzzle", Base64.getEncoder().encodeToString(puzzleByteEncoder.toBytes(puzzle.get())));
        }

        return response;
    }

    public JsonObject getPuzzle(Request request){
        Object idObject = request.params.get("puzzle_id");

        if(!(idObject instanceof Integer puzzleId)){
            JsonObject response = new JsonObject();
            response.addProperty("http_status_code", 400);
            response.addProperty("cause", "The fields 'puzzle_id' should be an integer");
            return  response;
        }
        return getPuzzle(puzzleId);
    }

    public JsonObject getAllPuzzles(){
        Collection<Puzzle<Move, State>> puzzles = service.getAllPuzzles();
        JsonObject response = new JsonObject();
        response.addProperty("http_status_code", 200);
        JsonArray array = new JsonArray();

        for(var puzzle: puzzles)
            array.add(Base64.getEncoder().encodeToString(puzzleByteEncoder.toBytes(puzzle)));

        response.add("puzzles", array);

        return response;
    }

}
