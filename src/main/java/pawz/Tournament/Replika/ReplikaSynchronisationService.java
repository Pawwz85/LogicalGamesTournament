package pawz.Tournament.Replika;

import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Exceptions.SynchronisationError;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Interfaces.IPuzzleService;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketService;
import pawz.Tournament.PuzzleSolutionTicket;

import java.util.Collection;

public class ReplikaSynchronisationService<Move extends ByteEncodable, State extends ByteEncodable> {
    private final LocalPuzzleRepository<Move, State> puzzleRepository;
    private final LocalSolutionTicketRepository<Move, State> ticketRepository;

    private final GameDefinition<Move, State> gameDefinition;

    public ReplikaSynchronisationService(LocalPuzzleRepository<Move, State> puzzleRepository, LocalSolutionTicketRepository<Move, State> ticketRepository, GameDefinition<Move, State> gameDefinition) {
        this.puzzleRepository = puzzleRepository;
        this.ticketRepository = ticketRepository;
        this.gameDefinition = gameDefinition;
    }


    private void forcefullySetTicketRepository(Collection<PuzzleSolutionTicketDTO<Move, State>> ticketRecords) throws RepositoryException {
        ticketRepository.clear();
        for(var record: ticketRecords){
            ticketRepository.update(new PuzzleSolutionTicket<>(record, gameDefinition));
        }
    }

    private void forcefullySetPuzzleRepository(Collection<Puzzle<Move, State>> puzzles) throws RepositoryException {
        puzzleRepository.clear();

        for(var record: puzzles)
            puzzleRepository.update(record);
    }

    public void syncTicketRepository(IPuzzleSolutionTicketService<Move, State> trustedService) throws SynchronisationError {
        try {
            forcefullySetTicketRepository(trustedService.getAllTicketsRecords());
        } catch (RepositoryException e) {
            /*
                If we ever run into this scenario:
                1. The repository was out of sync to begin with
                2. The repository was cleaned out
                3. There was an error during repository update

                Which means that repository is in inconsistent state. Since, we would throw a synchronisation
                error and let the caller decide what to do with this mess.
             */
            throw new SynchronisationError();
        }
    }

    public void syncPuzzleRepository(IPuzzleService<Move, State> trustedService) throws SynchronisationError{
        try{
            forcefullySetPuzzleRepository(trustedService.getAllPuzzles());
        } catch (RepositoryException e){
            throw new SynchronisationError();
        }
    }

}
