package modal;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Theme {
    public static void setButtonTheme() {
        // Set native windows look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf() );
        } catch (UnsupportedLookAndFeelException e) {
            ErrorPopup.show(1);
        }
    }

    public static void setTheme() {
        // Set native windows look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf() );
        } catch (UnsupportedLookAndFeelException e) {
            ErrorPopup.show(1);
        }
    }
}
