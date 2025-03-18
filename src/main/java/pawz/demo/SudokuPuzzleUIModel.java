package pawz.demo;

import org.jetbrains.annotations.NotNull;
import pawz.PuzzleSolutionBuilderFrame;

import java.util.concurrent.Flow;

public class SudokuPuzzleUIModel implements Flow.Subscriber<PuzzleSolutionBuilderFrame<SudokuMove, SudokuState>> {

    private final SudokuPuzzleUIView view;

    private @NotNull PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> puzzleSolutionBuilderFrame;
    private String additionalText;

    private Flow.Subscription subscription;

    public SudokuPuzzleUIModel(SudokuPuzzleUIView view, PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> puzzleSolutionBuilderFrame) {
        this.view = view;
        this.puzzleSolutionBuilderFrame = puzzleSolutionBuilderFrame;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void onNext(PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> item) {
        puzzleSolutionBuilderFrame = item;
        updateView();
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        additionalText = "Unexpected Error occurred as subscription to GameFrame Object was terminated";
        updateView();
    }

    private void updateView(){
        SudokuState state = puzzleSolutionBuilderFrame.getCurrentState();
        view.render(state, additionalText);
    }

    public void setAdditionalText(String text){
        this.additionalText = text;
        updateView();
    }

    @NotNull
    public PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> getSolutionBuilder() {
        return puzzleSolutionBuilderFrame;
    }

    public void show(){
        updateView();
    }
}
