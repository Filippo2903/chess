package modal;

import javax.swing.JOptionPane;

/**
 * Setup
 * 0-99
 *
 * Connessione
 * 100-199
 *
 * Comunicazione
 * 200-299
 *
 * IO
 * 300-399
 */

public class ErrorPopup {
    static final String message = "Codice Errore: 0x%d";
    static final String title = "Errore";

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
