import javax.swing.*;
import java.awt.*;

public class Sivalletto {
    private final JFrame window = new JFrame();
    private final JLayeredPane layeredPane = new JLayeredPane();
    private final int DIM_WINDOW = 700;
    private final int DIM_CHESSBOARD = 8;
    private final int MARGIN = DIM_WINDOW / (6 * DIM_CHESSBOARD + 2); // CELL_SIZE / 6
    final int CELL_SIZE = (DIM_WINDOW - MARGIN * 2) / DIM_CHESSBOARD;

    private void drawBackground() {
        final Color ODD_CELL_COLOR = new Color(0xFFEFD5), EVEN_CELL_COLOR = new Color(0x654321);

        JPanel background = new JPanel(new GridLayout(DIM_CHESSBOARD, DIM_CHESSBOARD));

        background.setBounds(MARGIN, MARGIN, DIM_CHESSBOARD * CELL_SIZE, DIM_CHESSBOARD * CELL_SIZE);
        background.setBackground(Color.GREEN);
        background.setLayout(null);

        JLabel cell;

        for (int y = 0; y < DIM_CHESSBOARD; y++) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                cell = new JLabel("");

                cell.setOpaque(true);

                // Set the background color
                cell.setBackground((y + x) % 2 == 0 ? ODD_CELL_COLOR : EVEN_CELL_COLOR);

                // Add the cell to the background
                background.add(cell);
            }
        }

        // Add the panel to the frame and display the frame
        layeredPane.add(background, 0, 0);
    }

    private void initPieces() {
        JPanel piecesLayer = new JPanel(new GridLayout(DIM_CHESSBOARD, DIM_CHESSBOARD));

        piecesLayer.setBounds(MARGIN, MARGIN, DIM_CHESSBOARD * CELL_SIZE, DIM_CHESSBOARD * CELL_SIZE);
        piecesLayer.setBackground(Color.GREEN);
        piecesLayer.setLayout(null);

//        JLabel cell;

        // Add the panel to the frame and display the frame
        layeredPane.add(piecesLayer, 1, 0);
    }

    private void initWindow () {
        window.setTitle("Snake - Sivalletto");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setPreferredSize(new Dimension(DIM_WINDOW, DIM_WINDOW));

        // DEBUG
        window.setBackground(Color.RED);

//        window.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("..\\assets\\icon.jpg"))).getImage());
        window.setResizable(false);

        window.pack();
        window.setVisible(true);

    }

    private void initMatch () {
        drawBackground();
        initPieces();
        window.add(layeredPane);
    }

    public void initGame () {
        initWindow();
        initMatch();
    }
}
