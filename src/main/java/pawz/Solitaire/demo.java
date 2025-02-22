package pawz.Solitaire;

import pawz.PuzzleSolutionBuilderFrame;
import pawz.Puzzle;
import pawz.Solitaire.CLI.*;
import pawz.Tournament.Interfaces.IServiceSession;
import pawz.Tournament.Replika.TournamentReplika;
import pawz.Tournament.SessionType;

import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class demo {

    private static class demoSession implements IServiceSession{

        @Override
        public SessionType getSessionType() {
            return SessionType.PlayerSession;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public int getSessionId() {
            return 7;
        }

    }

    public static void main(String[] args) throws NotEnoughArgumentsException {



        ReplikaStarter starter = new ReplikaStarter();

        TournamentReplika<SudokuMove, SudokuState> replika = starter.getBootedReplika().get();

        TicketDashboard ticketDashboard = new TicketDashboard(replika.getTicketService(), System.currentTimeMillis());

        IServiceSession session = new demoSession();

        Collection<Puzzle<SudokuMove, SudokuState>> puzzles =  replika.getPuzzleService().getAllPuzzles();
        PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> puzzleSolutionBuilderFrame = new PuzzleSolutionBuilderFrame<>(puzzles.stream().toList().getFirst().state, new SudokuGameDefinition());

        SudokuPuzzleUIView view = new SudokuPuzzleUIView();
        SudokuPuzzleUIModel model = new SudokuPuzzleUIModel(view, puzzleSolutionBuilderFrame);
        SudokuPuzzleUIController controller = new SudokuPuzzleUIController(model);


        BasicCommandHandler showTickets = new BasicCommandHandlerBuilder().withKeywordMatcher("tickets")
                .withDescription("Displays tickets present in the tournament")
                .withRunner(pair -> {ticketDashboard.displayTickets(session); return 2;})
                .build();


        Scanner scanner = new Scanner(System.in);
        CommandParser commandParser = new CommandParser();

        model.show();
        while(true){
            ImmutablePair<String[], Map<String, String>> pair = commandParser.parseLine(scanner.nextLine());

            if(showTickets.match(pair.first(), pair.second())) {
                showTickets.execute(pair.first(), pair.second());
                continue;
            }

            controller.tryExecute(pair.first(), pair.second());
        }

    }
}
