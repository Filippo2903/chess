package client;

import gameUtils.Move;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;



public class Game {
    private final int DIM_WINDOW = 500,
            DIM_CHESSBOARD = 8,
            MARGIN = DIM_WINDOW / (4 * DIM_CHESSBOARD + 2), // CELL_SIZE / 4
            CELL_SIZE = (DIM_WINDOW - MARGIN * 2) / DIM_CHESSBOARD;

    private final JFrame window = new JFrame();
    private JPanel chessboardPanel;

    // Todo colore deciso dal server
    private final PlayerColor myColor = PlayerColor.BLACK;

    // TODO dopo
    Function<Move, Boolean> canMove = move -> {return true;};

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
        chessboardPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        chessboardPanel.setLayout(null);

        window.add(chessboardPanel);
    }

    private void initPieces() {
        final PieceType[] startRow = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

        // TODO una variabile per ogni pezzo
        Piece piece;

        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                // Add pieces
                piece = new Piece(CELL_SIZE, startRow[x], playerColor);
                piece.setPosition(x, playerColor == myColor ? 7 : 0);
                board[playerColor == myColor ? 7 : 0][x] = piece;
                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(CELL_SIZE, PieceType.PAWN, playerColor);
                piece.setPosition(x, playerColor == myColor ? 6 : 1);
                board[playerColor == myColor ? 6 : 1][x] = piece;
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
