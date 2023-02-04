package modal;

import javax.swing.JOptionPane;

/**
 * Setup
 * 0-99
 *
 * Connection
 * 100-199
 *
 * Communication
 * 200-299
 *
 * IO
 * 300-399
 */

public class ErrorPopup {
    static final String message = "Error Code: 0x%d";
    static final String title = "Error";

    public static void show(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void show(String message) {
        show(title, message);
    }

    public static void show(int errorCode) {
        if (errorCode > 0 && errorCode < 99) {
            show(String.format(title + " %d", errorCode), String.format(message, errorCode));
        }
    }
}
