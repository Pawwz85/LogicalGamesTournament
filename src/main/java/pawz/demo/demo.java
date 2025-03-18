package pawz.demo;

import pawz.demo.CLI.*;
import pawz.Tournament.Interfaces.IServiceSession;
import pawz.Tournament.Replika.TournamentSystem;
import pawz.Tournament.SessionType;

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
        TournamentSystem<SudokuMove, SudokuState> replika = starter.getBootedReplika().get();
        IServiceSession session = new demoSession();
        SolitaireTournament demo = new SolitaireTournament(replika.getTicketService(), session);
        demo.run();

    }
}
