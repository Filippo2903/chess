package client.popup;

import gameUtils.PieceType;
import modal.Theme;

import javax.swing.*;
import java.awt.*;

public class PromotionPopup {
    public static void show() {
        Theme.setTheme();

        JFrame frame = new JFrame("Choose piece");
        JPanel panel = new JPanel();

        // Create buttons and add them to panel
        JButton queenButton = new JButton("") {{
            addActionListener(e -> return PieceType.QUEEN;);
        }};
        JButton rookButton = new JButton("") {{
            addActionListener(e -> System.out.println("Button 2 clicked!"));
        }};
        JButton knightButton = new JButton("") {{
            addActionListener(e -> System.out.println("Button 3 clicked!"));
        }};
        JButton bishopButton = new JButton("") {{
            addActionListener(e -> System.out.println("Button 4 clicked!"));
        }};

        panel.add(queenButton);
        panel.add(rookButton);
        panel.add(knightButton);
        panel.add(bishopButton);

        // Set panel layout to a 2x2 grid
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(300, 300));

        // Create and show popup
        frame.add(panel);
        frame.pack();

        frame.setResizable(false);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        show();
    }
}
