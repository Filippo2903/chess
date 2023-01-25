import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

class Test{
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("NON SIVALLETTO");
        }

        JButton button = new JButton("Play");
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.setBounds(10, 10, 200, 70);
        button.setBackground(new Color(0xDF3E28));
        button.setBorder(BorderFactory.createLineBorder(new Color(0xb12c1b),1));
        button.setForeground(new Color(0xffffff));
        button.setLayout(null);
        button.addActionListener(e -> System.out.println("Gay"));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.add(button);
    }
}
