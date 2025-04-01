package pawz.demo2.GUI;

import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Theme {


    // Filer color between components
    private int primaryBackgroundColor = 0x1a1a1a;

    // Background colors used as a background for components
    private int secondaryBackgroundColor = 0x1a001a;

    // Color used to differentiate component boundaries
    private int componentAccentColor =0x007f00;

    // Color used to mark buttons and other elements
    private int preferredButtonColour;

    private int textColor = 0xaaaaaa;

    public CornerRadii defaultRadii = null;

    public Insets defaultInsets = null;

    //private int onHoverColor;

    private static Color rgbToColor(int rgb){
        double blue = (rgb & 255) / 255.f;
        double green = ((rgb >> 8) & 255) / 255.f;
        double red = ((rgb >> 16) & 255) / 255.f;
        return new Color(red, green, blue, 1.);
    }

    public Color getPrimaryBackgroundColor(){
        return rgbToColor(primaryBackgroundColor);
    }

    public Color getSecondaryFill() {
        return rgbToColor(secondaryBackgroundColor);
    }

    public Color getComponentAccentColor(){
        return rgbToColor(componentAccentColor);
    }

    public Color getPreferredButtonColor(){
        return rgbToColor(preferredButtonColour);
    }

    public Color getTextColor(){
        return rgbToColor(textColor);
    }

}


