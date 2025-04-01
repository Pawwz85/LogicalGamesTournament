package pawz.demo2.GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;

public class ComponentWindow {

    private final VBox pane = new VBox();
    private final @Nullable String componentTitle;

    private final Theme theme;

    public ComponentWindow(Node component, @Nullable String componentTitle, Theme theme,
                            double x, double y){
        this.componentTitle = componentTitle;
        this.theme = theme;

        pane.setBackground(new Background(new BackgroundFill(theme.getComponentAccentColor(), theme.defaultRadii, theme.defaultInsets)));

        Text title = new Text(componentTitle);
        title.setFill(theme.getTextColor());
        pane.getChildren().addAll(title, component);
    }

    public Parent getAsParent(){
        return pane;
    }
}

