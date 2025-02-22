package pawz.Solitaire;

import org.jetbrains.annotations.NotNull;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketByteDecoder;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTOByteDecoder;
import pawz.Tournament.Exceptions.RepositoryException;

import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.PuzzleDecoder;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.Replika.LocalPuzzleRepository;
import pawz.Tournament.Replika.LocalSolutionTicketRepository;
import pawz.Tournament.Replika.TournamentReplika;

import java.util.Optional;

public class ReplikaStarter {

    private final int amountOfPuzzlesToSolve = 3;
    private final int playerId = 7;

    private final GameDefinition<SudokuMove, SudokuState> gameDefinition = SudokuGameDefinition.getInstance();

    public Optional<TournamentReplika<SudokuMove, SudokuState>> getBootedReplika(){
        try {
            LocalPuzzleRepository<SudokuMove, SudokuState> puzzleRepository = createPuzzleRepository();
            LocalSolutionTicketRepository<SudokuMove, SudokuState> ticketRepository = createFilledTicketRepository(puzzleRepository);
            TournamentReplika<SudokuMove, SudokuState> result = new TournamentReplika<>(puzzleRepository, ticketRepository, gameDefinition);
            return Optional.of(result);
        } catch (RepositoryException e){
            return Optional.empty();
        }
    }


    private Puzzle<SudokuMove, SudokuState> generatePuzzle(){
        return new Puzzle<>(new SudokuState());
    }

    private LocalPuzzleRepository<SudokuMove, SudokuState> createPuzzleRepository() throws RepositoryException {

        PuzzleDecoder<SudokuMove, SudokuState> puzzleDecoder = new PuzzleDecoder<>(new SudokuStateDecoder());
        LocalPuzzleRepository<SudokuMove, SudokuState> repository = new LocalPuzzleRepository<>(puzzleDecoder);

        for(SudokuState state: SudokuLoader.createSudokuStates()){
            Puzzle<SudokuMove, SudokuState> puzzle = new Puzzle<>(state);
            repository.persists(puzzle);
        }


        return repository;
    }
    private LocalSolutionTicketRepository<SudokuMove, SudokuState> createFilledTicketRepository(LocalPuzzleRepository<SudokuMove, SudokuState> puzzleRepository)
            throws RepositoryException{
        LocalSolutionTicketRepository<SudokuMove, SudokuState> repository = createSudokuSolutionTicketRepository();

        int tickedId = 0;
        for(Puzzle<SudokuMove, SudokuState> puzzle : puzzleRepository.getAll()){
            PuzzleSolutionTicket<SudokuMove, SudokuState> ticket = new PuzzleSolutionTicket<>(playerId, tickedId, puzzle.state, gameDefinition);
            repository.persists(ticket);
            tickedId += 1;
        }

        return repository;
    }

    @NotNull
    private LocalSolutionTicketRepository<SudokuMove, SudokuState> createSudokuSolutionTicketRepository() {
        PuzzleSolutionTicketDTOByteDecoder<SudokuMove, SudokuState> ticketDTOByteDecoder = new PuzzleSolutionTicketDTOByteDecoder<>(
                new SudokuMoveDecoder(),
                new SudokuStateDecoder(),
                gameDefinition);
        PuzzleSolutionTicketByteDecoder<SudokuMove, SudokuState> ticketByteDecoder = new PuzzleSolutionTicketByteDecoder<>(ticketDTOByteDecoder, gameDefinition);
        return new LocalSolutionTicketRepository<>(ticketByteDecoder);
    }
}
