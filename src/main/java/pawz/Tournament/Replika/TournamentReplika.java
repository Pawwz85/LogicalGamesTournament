package pawz.Tournament.Replika;

import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;


public class TournamentReplika<Move extends ByteEncodable, State extends ByteEncodable> {
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

    public TournamentReplika(LocalPuzzleRepository<Move, State> puzzleRepository, LocalSolutionTicketRepository<Move, State> ticketRepository, GameDefinition<Move, State> gameDefinition) {
        this.puzzleRepository = puzzleRepository;
        this.ticketRepository = ticketRepository;
        this.gameDefinition = gameDefinition;
        this.synchronisationService = new ReplikaSynchronisationService<>(puzzleRepository, ticketRepository, gameDefinition);
        this.puzzleService = new LocalPuzzleService<>(this.puzzleRepository);
        this.ticketService = new LocalSolutionTicketService<>(this.ticketRepository);
    }


}
