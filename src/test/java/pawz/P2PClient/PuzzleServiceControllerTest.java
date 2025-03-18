package pawz.P2PClient;

import Mockups.IntegerMove;
import Mockups.SimpleArithmeticPuzzleState;
import Mockups.SimpleArithmeticPuzzleStateByteDecoder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import pawz.Puzzle;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Interfaces.*;
import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.Tournament.PuzzleDecoder;
import pawz.Tournament.Replika.LocalPuzzleRepository;
import pawz.Tournament.Replika.LocalPuzzleService;
import pawz.DerivedImplementations.PlainCollectionByteDecoder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleServiceControllerTest {

    private final PuzzleServiceController<IntegerMove, SimpleArithmeticPuzzleState> controller;
    private final PuzzleDecoder<IntegerMove, SimpleArithmeticPuzzleState> puzzleByteDecoder;
    private final LocalPuzzleRepository<IntegerMove, SimpleArithmeticPuzzleState> repository;

    public PuzzleServiceControllerTest() throws RepositoryException {
        SimpleArithmeticPuzzleStateByteDecoder stateByteDecoder = new SimpleArithmeticPuzzleStateByteDecoder();
        puzzleByteDecoder = new PuzzleDecoder<>(stateByteDecoder);
        ByteEncoder<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>> puzzleByteEncoder = new PlainByteEncoder<>();

        repository = new LocalPuzzleRepository<>(puzzleByteDecoder);
        LocalPuzzleService<IntegerMove, SimpleArithmeticPuzzleState> service = new LocalPuzzleService<>(repository);

        this.controller = new PuzzleServiceController<>(service, puzzleByteEncoder);

        fillRepository(repository);
    }

    private void fillRepository(LocalPuzzleRepository<IntegerMove, SimpleArithmeticPuzzleState> repository) throws RepositoryException {
        int[] targets = {10, 5, 42};
        var puzzles = Arrays.stream(targets)
                .boxed()
                .map(Integer -> new SimpleArithmeticPuzzleState(Integer, 0))
                .map(state -> new Puzzle<IntegerMove, SimpleArithmeticPuzzleState>(state))
                .collect(Collectors.toList());

        for (var p : puzzles)
            repository.persists(p);
    }

    @Test
    public void testGetPuzzleByID() throws URISyntaxException {
        Map<String, Object> params = new HashMap<>();
        params.put("puzzle_id", 1);
        Request request = new Request(null, "", params);
        JsonObject response = controller.getPuzzle(request);
        int status_code = response.get("http_status_code").getAsInt();
        assertEquals(200, status_code);
    }

    @Test
    public void testGetPuzzleByIDWhenTicketDoesntExist() throws URISyntaxException {
        Map<String, Object> params = new HashMap<>();
        params.put("puzzle_id", 100);
        Request request = new Request(null, "", params);
        JsonObject response = controller.getPuzzle(request);
        int status_code = response.get("http_status_code").getAsInt();
        assertTrue(status_code >= 400 & status_code < 500);
    }

    @Test
    public void testGetPuzzleByIDWhenNoIDIsGiven() throws URISyntaxException {
        Map<String, Object> params = new HashMap<>();
        Request request = new Request(null, "", params);
        JsonObject response = controller.getPuzzle(request);
        int status_code = response.get("http_status_code").getAsInt();
        assertTrue(status_code >= 400 & status_code < 500);
    }

    @Test
    public void testGetPuzzleByIdReturnsEncodedPuzzle() throws URISyntaxException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("puzzle_id", 1);
        Request request = new Request(null, "", params);
        JsonObject response = controller.getPuzzle(request);
        String encodedPuzzle = response.get("puzzle").getAsString();
        Puzzle<IntegerMove, SimpleArithmeticPuzzleState> parsedPuzzle = puzzleByteDecoder.fromBytes(Base64.getDecoder().decode(encodedPuzzle));
        assertEquals(1, parsedPuzzle.puzzleId);
        assertEquals(10, parsedPuzzle.state.goal);
    }

    @Test void testGetALlPuzzles() throws IOException {
        JsonObject response = controller.getAllPuzzles();

        ByteDecoder<Collection<Puzzle<IntegerMove, SimpleArithmeticPuzzleState>>> collectionByteDecoder
                = new PlainCollectionByteDecoder<>(
                        new PuzzleDecoder<>(
                                new SimpleArithmeticPuzzleStateByteDecoder()
                        ));

        var puzzles = collectionByteDecoder.fromBytes(
                Base64.getDecoder().decode(response.get("puzzles").getAsString())
        );

        assertEquals(repository.getAll().size(), puzzles.size());
    }
}