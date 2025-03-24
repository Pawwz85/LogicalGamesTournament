package pawz.Components;

import pawz.Components.Internals.EventLoop;
import pawz.Tournament.Interfaces.ByteEncodable;

public class ComponentPack<Move extends ByteEncodable, State extends ByteEncodable> {
    public final EventLoop eventLoop;
    public final PuzzleDashboardComponent<Move, State> puzzleDashboardComponent;
    public final SolutionBuilderComponent<Move, State> solutionBuilderComponent;
    public final SubmissionDashboardComponent<Move, State> submissionDashboardComponent;

    public ComponentPack(EventLoop eventLoop, PuzzleDashboardComponent<Move, State> puzzleDashboardComponent, SolutionBuilderComponent<Move, State> solutionBuilderComponent, SubmissionDashboardComponent<Move, State> submissionDashboardComponent) {
        this.eventLoop = eventLoop;
        this.puzzleDashboardComponent = puzzleDashboardComponent;
        this.solutionBuilderComponent = solutionBuilderComponent;
        this.submissionDashboardComponent = submissionDashboardComponent;
    }
}
