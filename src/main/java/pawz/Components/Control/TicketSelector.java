package pawz.Components.Control;

import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;
import pawz.Tournament.Interfaces.IServiceSession;
import pawz.Tournament.Replika.LocalPuzzleService;
import pawz.TournamentClient.RemoteTicketService;

import java.util.Optional;

public class TicketSelector<Move extends ByteEncodable, State extends ByteEncodable> {

    private final IServiceSession userSession;
    private final LocalPuzzleService<Move, State> puzzleService;
    private final RemoteTicketService<Move, State> ticketService;

    public TicketSelector(IServiceSession userSession, LocalPuzzleService<Move, State> puzzleService, RemoteTicketService<Move, State> ticketService) {
        this.userSession = userSession;
        this.puzzleService = puzzleService;
        this.ticketService = ticketService;
    }


    public Optional<IPuzzleSolutionTicketProxy<Move, State>> selectById(int ticketId){
        try {
            return ticketService.getTickedById(userSession, ticketId);
        } catch (OwnershipException e) {
            return Optional.empty();
        }
    }

    public Optional<IPuzzleSolutionTicketProxy<Move, State>> selectByPuzzleId(int puzzleId){
        return puzzleService.getPuzzleById(puzzleId).flatMap(s -> selectByInitialState(s.state));
    }

    public Optional<IPuzzleSolutionTicketProxy<Move, State>> selectByInitialState(State s){
        return ticketService.getTicketByState(userSession, s).map(t -> t);
    }

}
