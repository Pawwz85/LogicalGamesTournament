package pawz.Components;

import pawz.Components.DTO.SolutionSubmissionRecord;
import pawz.Components.Internals.Component;
import pawz.Components.NamedEvents.SubmissionDashboardEvent;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.ArrayList;
import java.util.List;

public class SubmissionDashboardComponent<Move extends ByteEncodable, State extends ByteEncodable> extends Component<List<SolutionSubmissionRecord<Move, State>>> {

    private List<SolutionSubmissionRecord<Move, State>> currentSnapshot = new ArrayList<>();

    @Override
    protected List<SolutionSubmissionRecord<Move, State>> getCurrentSnapshot() {
        return currentSnapshot;
    }

    @Override
    protected Object onSubmissionDashboardEvent(SubmissionDashboardEvent<?, ?> event){
        currentSnapshot = new ArrayList<>(event.submissionsRecords.size());

        // TODO: calculate solving time based on incoming list
        for(var record: event.submissionsRecords){
            SolutionSubmissionRecord<Move, State> r =
                    new SolutionSubmissionRecord<Move, State>(
                    (PuzzleSolutionTicketDTO<Move, State>) record, null);
            currentSnapshot.add(r);
        }

        notifyObservers();
        return null;
    }
}
