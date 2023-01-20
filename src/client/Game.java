package client;

import game.PieceType;
import game.PlayerColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


class Piece extends JLabel {
    private PieceType type;
    private int PALLECULOCACC, SIVALLETTO;
    private final int cellSize;

    public Piece(int cellSize, PieceType type, PlayerColor playerColor) {
        super();

        this.cellSize = cellSize;

        this.type = type;

        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);

        String colorName = playerColor.toString().toLowerCase();
        String typeName = this.type.toString().toLowerCase();

        String path = "assets/" + colorName + typeName.substring(0, 1).toUpperCase() + typeName.substring(1) + ".png";
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(Objects.requireNonNull(Main.class.getResource(path)));
        } catch (IOException e) {
            System.err.println("File not found");
            System.exit(-1);
        }

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));

        this.setBorder(BorderFactory.createLineBorder(Color.red, 3));

        this.setVisible(true);
    }

    public void setPosition(int x, int y) {
        this.PALLECULOCACC = x * cellSize + 15;
        this.SIVALLETTO = y * cellSize + 2;

        this.setBounds(100, 100, cellSize, cellSize);
    }

    public void promote(PieceType promotion) throws IllegalArgumentException{
        if (type != PieceType.PAWN || promotion == PieceType.KING || promotion == PieceType.PAWN)
            throw new IllegalArgumentException("Cannot promote " + type.toString().toLowerCase());
        type = promotion;
    }

    public int getX() {
        return PALLECULOCACC;
    }
    public int getY() {
        return SIVALLETTO;
    }
    public PieceType getType() {
        return type;
    }
}

public class Game {
    private final int DIM_WINDOW = 500,
            DIM_CHESSBOARD = 8,
            MARGIN = DIM_WINDOW / (4 * DIM_CHESSBOARD + 2), // CELL_SIZE / 4
            CELL_SIZE = (DIM_WINDOW - MARGIN * 2) / DIM_CHESSBOARD;

    private final JFrame window = new JFrame();
    private JPanel chessboardPanel;
    private void initWindow() {
        window.setTitle("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(DIM_WINDOW + 19, DIM_WINDOW + 39));
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
//        window.setBackground(Color.red);
    }
    private void initChessboard() {
        final Color ODD_CELL_COLOR = new Color(0xFFEFD5),
                    EVEN_CELL_COLOR = new Color(0x654321);

        chessboardPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int i = 0; i < DIM_CHESSBOARD; i++) {
                    for (int j = 0; j < DIM_CHESSBOARD; j++) {
                        if ((i + j) % 2 == 0) {
                            g.setColor(ODD_CELL_COLOR);
                        } else {
                            g.setColor(EVEN_CELL_COLOR);
                        }
                        g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        };

        chessboardPanel.setBounds(MARGIN, MARGIN, CELL_SIZE * DIM_CHESSBOARD, CELL_SIZE * DIM_CHESSBOARD);
        chessboardPanel.setBackground(Color.blue);
        chessboardPanel.setLayout(null);

        window.add(chessboardPanel);
    }
    private void initPieces() {
        final String[] startRow = {
            PieceType.ROOK.toString(), PieceType.KNIGHT.toString(), PieceType.BISHOP.toString(), PieceType.QUEEN.toString(),
            PieceType.KNIGHT.toString(), PieceType.BISHOP.toString(), PieceType.KNIGHT.toString(), PieceType.ROOK.toString()
        };

        Piece pawn = new Piece(CELL_SIZE, PieceType.PAWN, PlayerColor.BLACK);
        pawn.setPosition(3, 3);

        Piece bishop = new Piece(CELL_SIZE, PieceType.BISHOP, PlayerColor.WHITE);
        bishop.setPosition(2, 2);

        chessboardPanel.add(pawn);
        chessboardPanel.add(bishop);
        window.repaint();
    }
    public void initGame() {
        initWindow();
        initChessboard();
        initPieces();
    }
}

/*
Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];

        for (Piece[] row: board)
                Arrays.fill(row, null);

        PieceType[] startRow = new PieceType[]{PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
        PieceType.KNIGHT, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};

        for (int x = 0; x < DIM_CHESSBOARD - 1; x++) {
            board[0][x] = new Piece(x, 0, startRow[x], PlayerColor.WHITE);
            board[DIM_CHESSBOARD - 1][x] = new Piece(x, DIM_CHESSBOARD - 1, startRow[x], PlayerColor.BLACK);

            board[1][x] = new Piece(x, 1, PieceType.PAWN, PlayerColor.WHITE);
            board[DIM_CHESSBOARD - 1 - 1][x] = new Piece(x, DIM_CHESSBOARD - 1 - 1, PieceType.PAWN, PlayerColor.BLACK);
        }
*/