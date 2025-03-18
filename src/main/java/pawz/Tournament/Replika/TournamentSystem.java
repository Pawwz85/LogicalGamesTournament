package pawz.Tournament.Replika;

import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.DerivedImplementations.PlainCollectionByteDecoder;
import pawz.P2PClient.PuzzleServiceController;
import pawz.P2PClient.SynchronisationController;
import pawz.P2PClient.TicketServiceController;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;


public class TournamentSystem<Move extends ByteEncodable, State extends ByteEncodable> {
    private final LocalPuzzleRepository<Move, State> puzzleRepository;
    private final LocalSolutionTicketRepository<Move, State> ticketRepository;

    private final GameDefinition<Move, State> gameDefinition;

    public final ReplikaSynchronisationService<Move, State> synchronisationService;

    private final LocalSolutionTicketService<Move, State> ticketService;

    private final LocalPuzzleService<Move, State> puzzleService;

    public LocalSolutionTicketService<Move, State> getTicketService() {
        return ticketService;
    }

    public LocalPuzzleService<Move, State> getPuzzleService() {
        return puzzleService;
    }

    public final PuzzleServiceController<Move, State> puzzleServiceController;
    public final TicketServiceController<Move, State> ticketServiceController;
    public final SynchronisationController<Move, State> synchronisationController;

    public TournamentSystem(LocalPuzzleRepository<Move, State> puzzleRepository, LocalSolutionTicketRepository<Move, State> ticketRepository, GameDefinition<Move, State> gameDefinition) {
        this.puzzleRepository = puzzleRepository;
        this.ticketRepository = ticketRepository;
        this.gameDefinition = gameDefinition;
        this.synchronisationService = new ReplikaSynchronisationService<>(puzzleRepository, ticketRepository, gameDefinition);
        this.puzzleService = new LocalPuzzleService<>(this.puzzleRepository);
        this.ticketService = new LocalSolutionTicketService<>(this.ticketRepository);

        this.puzzleServiceController = new PuzzleServiceController<>(puzzleService, new PlainByteEncoder<>());
        this.ticketServiceController = new TicketServiceController<>(ticketService, gameDefinition.moveByteDecoder());
        this.synchronisationController = new SynchronisationController<>(synchronisationService, puzzleService, ticketService);
    }


}
