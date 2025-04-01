package pawz.Components.Control;

import com.gmail.woodyc40.pbft.Client;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pawz.Auth.SignedMessage;
import pawz.Components.SolutionBuilderFrame;
import pawz.P2PClient.SignedMessageFactory;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Replika.LocalPuzzleService;


import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SolutionFrameSelector<Move extends ByteEncodable, State extends ByteEncodable> {
    private final Map<Integer, SolutionBuilderFrame<Move, State>> frameStore = new ConcurrentHashMap<>();

    private final LocalPuzzleService<Move, State> puzzleService;

    private final GameDefinition<Move, State> gameDefinition;

    private final Client<SignedMessage<byte[]>, JsonObject, String> client;
    private final SignedMessageFactory messageFactory;

    public SolutionFrameSelector(LocalPuzzleService<Move, State> puzzleService, GameDefinition<Move, State> gameDefinition, Client<SignedMessage<byte[]>, JsonObject, String> client, SignedMessageFactory messageFactory) {
        this.puzzleService = puzzleService;
        this.gameDefinition = gameDefinition;
        this.client = client;
        this.messageFactory = messageFactory;
    }

    private @Nullable Puzzle<Move, State> selectPuzzle(int puzzleID){
        return puzzleService.getPuzzleById(puzzleID).orElse(null);
    }

    private @NotNull SolutionBuilderFrame<Move, State> createNewFrame(@NotNull Puzzle<Move, State> puzzle){
        var result =  new SolutionBuilderFrame<>(puzzle.state, puzzle.state, new ArrayList<>() , gameDefinition);
        frameStore.put(puzzle.puzzleId, result);
        registerTicket(puzzle);
        return result;
    }

    private void registerTicket(@NotNull Puzzle<Move, State> puzzle){
        // TODO: commit creation of ticket associated with this puzzle
    }

    public Optional<SolutionBuilderFrame<Move, State>> selectFrame(int puzzleId){
        if(frameStore.containsKey(puzzleId)){
            return Optional.ofNullable(frameStore.get(puzzleId));
        } else {
            var puzzle = puzzleService.getPuzzleById(puzzleId);
            return puzzle.map(this::createNewFrame);
        }
    }


}
