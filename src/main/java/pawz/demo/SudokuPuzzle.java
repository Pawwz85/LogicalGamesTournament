package pawz.demo;

import pawz.PuzzleSolutionBuilderFrame;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;

public class SudokuPuzzle {

    public final IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket;
    public final PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> puzzleSolutionBuilderFrame;

    public SudokuPuzzle(IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket, Puzzle<SudokuMove, SudokuState> puzzle, GameDefinition<SudokuMove, SudokuState> gameDefinition) {
        this.ticket = ticket;
        this.puzzleSolutionBuilderFrame = new PuzzleSolutionBuilderFrame<>(puzzle.state,
                gameDefinition);
    }

}
