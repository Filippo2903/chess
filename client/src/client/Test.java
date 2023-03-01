package client;

import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Test {
    public JFrame f;

    public Test() {
        f = new JFrame();
    }
    private void setGui() {
        try {
            URL path = ClassLoader.getSystemResource("loading.gif");
            if (path == null) {
                ErrorPopup.show(7);
                System.exit(-1);
            }
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Container cp = f.getContentPane();
            cp.add(new JLabel(new ImageIcon(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                Test test = new Test();
                test.setGui();
                test.f.pack();
                test.f.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
