package pawz.TournamentClient;

import pawz.Puzzle;
import pawz.PuzzleSolutionBuilderFrame;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;

public class PuzzleFrame<Move extends ByteEncodable, State extends ByteEncodable> {

    public final IPuzzleSolutionTicketProxy<Move, State> ticket;
    public final PuzzleSolutionBuilderFrame<Move, State> puzzleSolutionBuilderFrame;
    public PuzzleFrame(Puzzle<Move, State> puzzle, IPuzzleSolutionTicketProxy<Move, State> ticket, GameDefinition<Move, State> gameDefinition){
        this.ticket = ticket;
        this.puzzleSolutionBuilderFrame = new PuzzleSolutionBuilderFrame<>(puzzle.state, gameDefinition);
    }

    public PuzzleFrame(State state, IPuzzleSolutionTicketProxy<Move, State> ticket, GameDefinition<Move, State> gameDefinition){
        this.ticket = ticket;
        this.puzzleSolutionBuilderFrame = new PuzzleSolutionBuilderFrame<>(state, gameDefinition);
    }
}
