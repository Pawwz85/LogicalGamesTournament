package pawz.demo2.GUI;



import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageBox {

    private final Theme theme;
    private final VBox box = new VBox();
    private List<String> msgs = new ArrayList<>();


    public MessageBox(Theme theme){
        this.theme = theme;

        BackgroundFill fill = new BackgroundFill(theme.getSecondaryFill(), theme.defaultRadii, theme.defaultInsets);
        Background background = new Background(fill);

        box.setBackground(background);
    }

    public void addMsg(String m){
        msgs.add(m);
        updateDisplay();
        // TODO: refresh }

    public VBox getBox(){
        return box;
    }


    private void updateDisplay(){
        box.getChildren().clear();

        StringBuilder builder = new StringBuilder();

        for(var msg: msgs)
            builder.append(msg).append('\n');


       Text text = new Text(builder.toString());
       text.setWrappingWidth(300);
       text.setFill(theme.getTextColor());

        box.getChildren().addAll(text);
    }

}
