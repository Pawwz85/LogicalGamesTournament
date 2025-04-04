package pawz.demo2.GUI;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pawz.Components.PuzzleDashboardComponent;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderComponent;
import pawz.Components.SubmissionDashboardComponent;
import pawz.TournamentFacade;
import pawz.demo2.LightOutMove;
import pawz.demo2.LightOutState;

public class ApplicationModel {

    // top level variables


    // Components

    private final Theme theme = new Theme();
    private final PuzzleDashboardComponent<LightOutMove, LightOutState> puzzleDashboardComponent;
    private final SubmissionDashboardComponent<LightOutMove, LightOutState> submissionDashboardComponent;
    private final SolutionBuilderComponent<LightOutMove, LightOutState> solutionBuilderComponent;


    // views

    private final LightOutPuzzleDashboardView puzzleDashboardView = new LightOutPuzzleDashboardView();
    private final LightOutSolutionBuilderView solutionBuilderView;
    private final SubmissionDashboardView submissionDashboardView = new SubmissionDashboardView();

    private final Stage stage;

    private final Pane pane = new Pane();


    public ApplicationModel(TournamentFacade<LightOutMove, LightOutState> facade, Stage stage){
        var pack = facade.getComponentPack();
        puzzleDashboardComponent = pack.puzzleDashboardComponent;
        solutionBuilderComponent = pack.solutionBuilderComponent;
        submissionDashboardComponent = pack.submissionDashboardComponent;
        this.stage = stage;

        solutionBuilderView =  new LightOutSolutionBuilderView(500., 500, theme, solutionBuilderComponent);

        puzzleDashboardComponent.registerObserver(puzzleDashboardView);
        solutionBuilderComponent.registerObserver(solutionBuilderView);
        submissionDashboardComponent.registerObserver(submissionDashboardView);


        puzzleDashboardView.repaintCallback = this::repaint;
        submissionDashboardView.repaintCallback = this::repaint;
        solutionBuilderView.repaintCallback = this::repaint;

        puzzleDashboardView.onPuzzleSelected = puzzle -> {
            if (puzzle.isPresent()){
                var frame = facade.frameSelector.selectFrame(puzzle.get().puzzleId);
                frame.ifPresent(solutionBuilderComponent::setFrame);
            }
            return null;
        } ;

        solutionBuilderView.getAsNode().setLayoutY(100);

        puzzleDashboardView.getAsNode().setLayoutX(500);
        puzzleDashboardView.getAsNode().setLayoutY(100);

        submissionDashboardView.getAsNode().setLayoutX(800);
        submissionDashboardView.getAsNode().setLayoutY(100);

        pane.getChildren().addAll(puzzleDashboardView.getAsNode(),solutionBuilderView.getAsNode(), submissionDashboardView.getAsNode());
        pane.setBackground(new Background(new BackgroundFill(theme.getPrimaryBackgroundColor(), null, null)));
    }

    private Object repaint(Object o){

        Platform.runLater(new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                stage.show();
                return null;
            }
        });


        return null;
    }

    public Parent getAsParent(){
        return pane;
    }
}
