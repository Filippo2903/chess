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
 *
 * Runtime
 * 400 - 499
 */

public class ErrorPopup {
    static final String message = "Error Code: 0x%d";
    static final String title = "Error";

    /**
     * Show an error popup
     * @param title The popup top title
     * @param message The popup main message
     */
    public static void show(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an error popup with the default title
     * @param message The popup main message
     */
    public static void show(String message) {
        show(title, message);
    }

    /**
     * Show an error popup with a given code
     * @param errorCode The error code
     */
    public static void show(int errorCode) {
        if (errorCode > 0 && errorCode < 99) {
            show(String.format(title + " %d", errorCode), String.format(message, errorCode));
        }
    }
}
