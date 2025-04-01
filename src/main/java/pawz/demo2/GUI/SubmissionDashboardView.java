package pawz.demo2.GUI;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import pawz.Components.DTO.SolutionSubmissionRecord;
import pawz.Components.Internals.Component;
import pawz.demo2.LightOutMove;
import pawz.demo2.LightOutState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SubmissionDashboardView implements Component.ComponentObserver<List<SolutionSubmissionRecord<LightOutMove, LightOutState>>> {

    private final ListView<SolutionSubmissionRecord<LightOutMove, LightOutState>> view = new ListView<>();
    private final Pane pane = new Pane();
    public Function<Object, Object> repaintCallback = o -> o;

    private static class SubmissionCell extends ListCell<SolutionSubmissionRecord<LightOutMove, LightOutState>> {

        private static class SubmissionCellFactory implements Callback<ListView<SolutionSubmissionRecord<LightOutMove, LightOutState>>, ListCell<SolutionSubmissionRecord<LightOutMove, LightOutState>>> {
            @Override
            public ListCell<SolutionSubmissionRecord<LightOutMove, LightOutState>> call(ListView<SolutionSubmissionRecord<LightOutMove, LightOutState>> listView) {
                return new SubmissionCell();
            }
        }

        private Node drawItem(SolutionSubmissionRecord<LightOutMove, LightOutState> item) {
            //double cellWidth = targetSize;
            Pane pane = new Pane();



            List<Node> sections = new ArrayList<>();
            int padding = 15;

            pane.setPrefHeight(6*padding);

            var playerID = new Text("Player: " + item.playerId);
            playerID.setX(padding);
            playerID.setY(padding);

            var ticketID = new Text("Ticket: " + item.ticketId);
            ticketID.setX(padding);
            ticketID.setY(2*padding);

            var phase = new Text("Phase: " + item.phase);
            phase.setX(padding);
            phase.setY(3*padding);

            var time = new Text("Time: " + (item.getSolvingTime().isPresent() ? item.getSolvingTime().get() : "N/A"));
            time.setX(padding);
            time.setY(4*padding);

            sections.add(playerID);
            sections.add(ticketID);
            sections.add(phase);
            sections.add(time);

            // Add the moves if needed
            // sections.add(new Text("Moves: " + String.join(", ", item.solution)).setX(cellWidth - padding*2).setY(3 * padding));

            pane.getChildren().addAll(sections);
            return pane;
        }

        @Override
        protected void updateItem(SolutionSubmissionRecord<LightOutMove, LightOutState> item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(null); // Example: Full string representation of the record
                setGraphic(drawItem(item));
            }
        }
    }

    public SubmissionDashboardView() {
        view.setCellFactory(new SubmissionCell.SubmissionCellFactory());

        pane.getChildren().addAll(view);
    }

    private void render(List<SolutionSubmissionRecord<LightOutMove, LightOutState>> records) {
        view.getItems().setAll(records);
        repaintCallback.apply(null);
    }

    @Override
    public void onObservedDataUpdate(List<SolutionSubmissionRecord<LightOutMove, LightOutState>> records) {
        System.out.println("received update");
        Platform.runLater(new Task<Object>() {

            @Override
            protected Object call() throws Exception {
                render(records);
                return null;
            }
        });
    }

    public Parent getAsParent() {
        return pane;
    }

    public Node getAsNode(){
        return pane;
    }
}
