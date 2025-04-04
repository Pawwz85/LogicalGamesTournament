package pawz.Components.NamedEvents;

import pawz.Components.Internals.TournamentEvent;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderFrame;
import pawz.PuzzleSolutionBuilderFrame;
import pawz.Tournament.Interfaces.ByteEncodable;

public class UserIssuedSolutionFrameUpdateEvent<Move extends ByteEncodable, State extends ByteEncodable> extends TournamentEvent {

    public final SolutionBuilderFrame<Move, State> frame;

    public UserIssuedSolutionFrameUpdateEvent(SolutionBuilderFrame<Move, State> frame) {
        super(NamedTournamentEvents.UserIssuedSolutionFrameUpdate);
        this.frame = frame;
    }
}
