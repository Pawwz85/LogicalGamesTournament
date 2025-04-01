package pawz.demo2.GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;
import pawz.Components.Internals.Component;
import pawz.Components.PuzzleDashboardComponent;
import pawz.Components.SolutionBuilderComponent;
import pawz.Components.SolutionBuilderFrame;
import pawz.Puzzle;
import pawz.demo2.LightOutGameDefinition;
import pawz.demo2.LightOutMove;
import pawz.demo2.LightOutState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LightOutSolutionBuilderView implements Component.ComponentObserver<SolutionBuilderFrame<LightOutMove, LightOutState>> {


    private final Pane pane = new Pane();

    private final double componentWidth;
    private final double componentHeight;

    private final Theme theme;

    private final @NotNull SolutionBuilderComponent<LightOutMove, LightOutState> model;

    private final double boardOffsetX;

    private final double boardSize;

    private static final Color lightOn = Color.YELLOW;
    private static final Color lightOff = Color.DARKGRAY;

    public Function<Object, Object> repaintCallback = o-> o;

    public LightOutSolutionBuilderView(double width, double height, Theme theme, @NotNull SolutionBuilderComponent<LightOutMove, LightOutState> model){
        this.componentHeight = height;
        this.componentWidth = width;
        this.theme = theme;
        this.model = model;

        boardSize = 0.75 * Math.min(width, height);
        boardOffsetX = (width - boardSize)/2;

        pane.setPrefWidth(componentWidth);
        pane.setPrefHeight(componentHeight);
        pane.setBackground(new Background(new BackgroundFill(theme.getSecondaryFill(), theme.defaultRadii, theme.defaultInsets)));
    }

    private void registerClick(int i){
        model.addMove(new LightOutMove(i));
    }

    private Rectangle createRectangle(int i, @NotNull SolutionBuilderFrame<LightOutMove, LightOutState> frame){

        double x = this.boardOffsetX + boardSize*(i % 3)/3;
        double y = boardSize*(i/3)/3;

        Rectangle result = new Rectangle(x, y, boardSize/3, boardSize/3);

        Color paint =  (frame.currentState.board[i] == 0)? lightOff : lightOn;
        result.setFill(paint);
        result.setOnMouseClicked(e -> {registerClick(i);});

        return result;
    }

    private void render(@NotNull SolutionBuilderFrame<LightOutMove, LightOutState> frame){
        pane.getChildren().clear();

        List<Node> children = new ArrayList<>();

        int i;
        for( i = 0; i < 9; ++i){
            children.add(createRectangle(i, frame));
        }

        pane.getChildren().addAll(children);
        repaintCallback.apply(null);
    }

    private @NotNull SolutionBuilderFrame<LightOutMove, LightOutState> getEmptyFrame(){
        LightOutState state = new LightOutState();

        return new SolutionBuilderFrame<>(
                state,
                state,
                new ArrayList<>(),
                new LightOutGameDefinition()
        );

    }

    @Override
    public void onObservedDataUpdate(SolutionBuilderFrame<LightOutMove, LightOutState> frame) {
        render((frame == null) ? getEmptyFrame() : frame);
    }

    public Parent getAsParent() {
        return pane;
    }
    public Node getAsNode(){
        return pane;
    }
}
