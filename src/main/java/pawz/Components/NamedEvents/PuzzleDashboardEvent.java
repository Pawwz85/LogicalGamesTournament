package pawz.Components.NamedEvents;

import pawz.Components.Internals.TournamentEvent;
import pawz.Puzzle;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.List;

public class PuzzleDashboardEvent<Move extends ByteEncodable, State extends ByteEncodable> extends TournamentEvent {

    public final List<Puzzle<Move, State>> puzzles;

    public PuzzleDashboardEvent(List<Puzzle<Move, State>> submissionsRecords) {
        super(NamedTournamentEvents.PuzzleDashboardEventCode);
        this.puzzles = submissionsRecords;
    }
}
