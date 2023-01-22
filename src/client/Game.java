package client;

import gameUtils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


public class Game {
    private final int DIM_WINDOW = 500,
            DIM_CHESSBOARD = 8,
            MARGIN = DIM_WINDOW / (4 * DIM_CHESSBOARD + 2), // CELL_SIZE / 4
            CELL_SIZE = (DIM_WINDOW - MARGIN * 2) / DIM_CHESSBOARD;

    private final JFrame window = new JFrame();
    private JPanel chessboardPanel;

    private static PlayerColor myColor;

    private static Piece[][] board;

    private static PlayerColor playerTurn;

    public Game(PlayerColor color) {
        myColor = color;

        playerTurn = PlayerColor.WHITE;
    }

    private void initWindow() {
        window.setTitle("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(DIM_WINDOW + 19, DIM_WINDOW + 39));

        Image icon = null;
        try {
            icon = ImageIO.read(Objects.requireNonNull(Game.class.getResource("assets/icon.png")));
        } catch (IOException e) {
            System.err.println("File not found");
            System.exit(-1);
        }

        window.setIconImage(icon);

        window.setBackground(Color.RED);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
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

        board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

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

    public static Piece[][] getBoard() {
        return board;
    }

    public static void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }

    public static PlayerColor getPlayerColor() {
        return myColor;
    }
    public static PlayerColor getPlayerTurn() {
        return playerTurn;
    }
    public static void changePlayerTurn() {
        playerTurn = playerTurn == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    public static void enemyMove(Packet packet) {
        Piece piece = board[packet.from.y][packet.from.x];
        board[packet.from.y][packet.from.x] = null;

        Piece pieceTo = board[packet.to.y][packet.to.x];
        if (pieceTo != null)
            pieceTo.kill();
        board[packet.to.y][packet.to.x] = piece;

        piece.setPosition(packet.to.x, packet.to.y);

        changePlayerTurn();
    }
}
