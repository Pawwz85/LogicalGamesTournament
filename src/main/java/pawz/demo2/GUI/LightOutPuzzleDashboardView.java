package pawz.demo2.GUI;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import pawz.Components.Internals.Component;
import pawz.Puzzle;
import pawz.demo2.LightOutMove;
import pawz.demo2.LightOutState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LightOutPuzzleDashboardView implements Component.ComponentObserver<List<Puzzle<LightOutMove, LightOutState>>> {

    private final ListView<Puzzle<LightOutMove, LightOutState>> view = new ListView<>();
    private final Pane pane = new Pane();

    private static final Color lightOn = Color.YELLOW;
    private static final Color lightOff = Color.DARKGRAY;

    public Function<Object, Object> repaintCallback = o-> o;

    private static class PuzzleCell extends ListCell<Puzzle<LightOutMove, LightOutState>>{

    private static class PuzzleCellFactory implements Callback<ListView<Puzzle<LightOutMove, LightOutState>>, ListCell<Puzzle<LightOutMove, LightOutState>>>{
        @Override
        public ListCell<Puzzle<LightOutMove, LightOutState>> call(ListView<Puzzle<LightOutMove, LightOutState>> puzzleListView) {
            return new PuzzleCell();
        }
    }

        private Node drawItem(Puzzle<LightOutMove, LightOutState> item, double targetSize){

            double areaSize = targetSize/3;
            Pane pane = new Pane();

            List<Node> sections = new ArrayList<>();

            int x, y;
            for (int i = 0; i<9; ++i){
                x = i%3;
                y = i/3;
                Rectangle rect = new Rectangle(areaSize*x, areaSize*y, areaSize, areaSize);
                Color paint =  (item.state.board[i] == 0)? lightOff : lightOn;
                rect.setFill(paint);
                sections.add(rect);
            }

            pane.getChildren().addAll(sections);
            return pane;
        }

        @Override
        protected void updateItem(Puzzle<LightOutMove, LightOutState> item, boolean empty){
            super.updateItem(item, empty);

            if(item != null){
                setText(String.format("Puzzle nr %d", item.puzzleId)); // We are not using text here
                setGraphic(drawItem(item, 100));
            }

        }
    }

    public LightOutPuzzleDashboardView(){
        view.setCellFactory(new PuzzleCell.PuzzleCellFactory());

        pane.getChildren().addAll(view);

    }


    private void render(List<Puzzle<LightOutMove, LightOutState>> puzzles){
        view.getItems().setAll(puzzles);
        repaintCallback.apply(null);
    }

    @Override
    public void onObservedDataUpdate(List<Puzzle<LightOutMove, LightOutState>> puzzles) {
        System.out.println("received update");
        Platform.runLater(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                render(puzzles);
                return null;
            }
        });
    }

    public Parent getAsParent(){
        return pane;
    }

    public Node getAsNode(){
        return pane;
    }
}
