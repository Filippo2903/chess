package modal;

import javax.swing.*;

public class Theme {
    public static void setTheme() {
        // Set native windows look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            ErrorPopup.show(1);
        }
    }
}
