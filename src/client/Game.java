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
    private int x, y;
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
        this.setVisible(true);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        this.setBounds(x * cellSize, y * cellSize, cellSize, cellSize);
    }

    public void promote(PieceType promotion) throws IllegalArgumentException{
        if (type != PieceType.PAWN || promotion == PieceType.KING || promotion == PieceType.PAWN)
            throw new IllegalArgumentException("Cannot promote " + type.toString().toLowerCase());
        type = promotion;
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

    // Todo
    private final PlayerColor myColor = PlayerColor.WHITE;

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
        final PieceType[] startRow = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        Piece piece;

        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                // Add pieces
                piece = new Piece(CELL_SIZE, startRow[x], playerColor);
                piece.setPosition(x, playerColor == myColor ? 7 : 0);
                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(CELL_SIZE, PieceType.PAWN, playerColor);
                piece.setPosition(x, playerColor == myColor ? 6 : 1);
                chessboardPanel.add(piece);
            }
        }

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