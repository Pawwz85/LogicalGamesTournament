package pawz.Components.NamedEvents;

import pawz.Components.Internals.TournamentEvent;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.List;

public class SubmissionDashboardEvent<Move extends ByteEncodable, State extends ByteEncodable> extends TournamentEvent {

    public final List<PuzzleSolutionTicketDTO<Move, State>> submissionsRecords;

    public SubmissionDashboardEvent(List<PuzzleSolutionTicketDTO<Move, State>> submissionsRecords) {
        super(NamedTournamentEvents.SubmissionDashboardEventCode);
        this.submissionsRecords = submissionsRecords;
    }
}
