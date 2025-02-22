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
        IServiceSession session = new demoSession();
        SolitaireTournament demo = new SolitaireTournament(replika.getTicketService(), session);
        demo.run();

    }
}
