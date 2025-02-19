package pawz.TournamentClient;

import pawz.Puzzle;
import pawz.Tournament.Interfaces.*;
import pawz.Tournament.PuzzleSolutionTicket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogicalTournamentClient<Move extends ByteEncodable, State extends ByteEncodable> {
    private final IPuzzleSolutionTicketService<Move, State> ticketService;
    private final IPuzzleService<Move, State> puzzleService;
    private final GameDefinition<Move, State> gameDefinition;

    public LogicalTournamentClient(IPuzzleSolutionTicketService<Move, State> ticketService, IPuzzleService<Move, State> puzzleService, GameDefinition<Move, State> gameDefinition) {
        this.ticketService = ticketService;
        this.puzzleService = puzzleService;
        this.gameDefinition = gameDefinition;
    }

    Collection<PuzzleFrame<Move, State>> getPuzzleFrames(IServiceSession session){
        Collection<IPuzzleSolutionTicketProxy<Move, State>> tickets = ticketService.getAllOwnedTickets(session);
        Collection<Puzzle<Move, State>> puzzles = puzzleService.getAllPuzzles();

        List<PuzzleFrame<Move, State>> result = new ArrayList<>();

        for( var ticket : tickets){
            State state = ticket.getState();
            result.add(new PuzzleFrame<>(state, ticket, gameDefinition));
        }

        return result;
    }
}
