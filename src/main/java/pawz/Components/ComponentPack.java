package pawz.Components;

import pawz.Components.Internals.EventLoop;
import pawz.Components.SolutionBuilderFrame.FrameDashboardComponent;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderComponent;
import pawz.Tournament.Interfaces.ByteEncodable;

public class ComponentPack<Move extends ByteEncodable, State extends ByteEncodable> {
    public final EventLoop eventLoop;
    public final PuzzleDashboardComponent<Move, State> puzzleDashboardComponent;
    public final SolutionBuilderComponent<Move, State> solutionBuilderComponent;
    public final SubmissionDashboardComponent<Move, State> submissionDashboardComponent;

    public final FrameDashboardComponent<Move, State> frameDashboardComponent;

    public ComponentPack(EventLoop eventLoop, PuzzleDashboardComponent<Move, State> puzzleDashboardComponent, SolutionBuilderComponent<Move, State> solutionBuilderComponent, SubmissionDashboardComponent<Move, State> submissionDashboardComponent, FrameDashboardComponent<Move, State> frameDashboardComponent) {
        this.eventLoop = eventLoop;
        this.puzzleDashboardComponent = puzzleDashboardComponent;
        this.solutionBuilderComponent = solutionBuilderComponent;
        this.submissionDashboardComponent = submissionDashboardComponent;
        this.frameDashboardComponent = frameDashboardComponent;
    }
}
