package pawz.P2PClient;

import com.google.gson.JsonObject;
import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.DerivedImplementations.IDeriveByteUtils;
import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.DerivedImplementations.PlainCollectionByteEncoder;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;
import pawz.Tournament.Replika.LocalPuzzleService;
import pawz.Tournament.Replika.LocalSolutionTicketService;
import pawz.Tournament.Replika.ReplikaSynchronisationService;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SynchronisationController<Move extends ByteEncodable, State extends ByteEncodable> {

    private final IDeriveByteUtils<Puzzle<Move, State>> puzzleDeriveByteUtils = new DeriveByteUtils<>();
    private final IDeriveByteUtils<PuzzleSolutionTicketDTO<Move, State>> ticketDeriveByteUtils = new DeriveByteUtils<>();

    private final ReplikaSynchronisationService<Move, State> synchronisationService;
    private final LocalPuzzleService<Move, State> puzzleService;
    private final ByteEncoder<Collection<Puzzle<Move, State>>> puzzlesByteEncoder;

    private final LocalSolutionTicketService<Move, State> ticketService;

    private final ByteEncoder<Collection<PuzzleSolutionTicketDTO<Move, State>>> ticketsByteEncoder;

    public SynchronisationController(ReplikaSynchronisationService<Move, State> synchronisationService, LocalPuzzleService<Move, State> puzzleService, LocalSolutionTicketService<Move, State> ticketService) {
        this.synchronisationService = synchronisationService;
        this.puzzleService = puzzleService;
        this.puzzlesByteEncoder = puzzleDeriveByteUtils.collectionByteEncoder(new PlainByteEncoder<>());
        this.ticketService = ticketService;
        this.ticketsByteEncoder = ticketDeriveByteUtils.collectionByteEncoder(new PlainByteEncoder<>());
    }

    public JsonObject getChecksums(Request ignored) {
        String puzzlesChecksum = Base64.getEncoder().encodeToString(synchronisationService.calculatePuzzleRepositoryChecksum());
        String ticketsChecksum = Base64.getEncoder().encodeToString(synchronisationService.calculatePuzzleRepositoryChecksum());

        JsonObject response = new JsonObject();
        response.addProperty("http_status_code", 200);
        response.addProperty("puzzles_checksum", puzzlesChecksum);
        response.addProperty("tickets_checksum", ticketsChecksum);
        return response;
    }

    public JsonObject getReplicaSnapshot(Request ignored) {

        Function<byte[], String> toBase64 = b -> Base64.getEncoder().encodeToString(b);

        Collection<Puzzle<Move, State>> puzzles = puzzleService.getAllPuzzles();
        Collection<PuzzleSolutionTicketDTO<Move, State>> tickets = ticketService.getAllTicketsRecords();

        JsonObject response = new JsonObject();
        response.addProperty("http_status_code", 200);
        response.addProperty("puzzle", toBase64.apply(puzzlesByteEncoder.toBytes(puzzles)));
        response.addProperty("tickets", toBase64.apply(ticketsByteEncoder.toBytes(tickets)));

        return response;
    }
}
