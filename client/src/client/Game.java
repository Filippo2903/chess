package client;

import com.formdev.flatlaf.FlatClientProperties;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import themes.CustomTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Game {
    private static PlayerColor clientColor = PlayerColor.WHITE;
    private static PlayerColor playerTurn;

    private static final int WINDOW_WIDTH = 510, WINDOW_HEIGHT = 570;
    private static final int DIM_CHESSBOARD = 8;
    private static final int MARGIN = WINDOW_WIDTH / (4 * DIM_CHESSBOARD + 2);
    private static final int CELL_SIZE = (WINDOW_WIDTH - MARGIN * 2) / DIM_CHESSBOARD;

    // The board where all the pieces will be stored
    private static final Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];

    private final JFrame window = new JFrame();

    private JPanel chessboardPanel;

    public static PlayerColor getPlayerColor() {
        return clientColor;
    }
    public static PlayerColor getPlayerTurn() {
        return playerTurn;
    }
    public static Piece[][] getBoard() {
        return board;
    }
    public static void changePlayerTurn() {
        playerTurn = playerTurn == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }


    /**
     * Edit a cell of the board data
     * @param cell Cell to edit
     * @param value Value to assign to the cell
     */
    public static void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }

    /**
     * Move the enemy piece
     * @param packet Packet to get data from
     */
    public void enemyMove(Packet packet) {
        Piece enemyPiece = board[packet.from.y][packet.from.x];

        // Delete previous position
        editBoardCell(packet.from, null);

        // If the target cell is a piece, kill it
        Piece pieceTo = board[packet.to.y][packet.to.x];
        if (pieceTo != null) {
            pieceTo.kill();
        }

        if (packet.type != null) {
            enemyPiece.kill();

            enemyPiece = new Piece(packet.type, clientColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE, packet.to);
            chessboardPanel.add(enemyPiece);
            chessboardPanel.repaint();
        }

        // Place the piece in the new position
        editBoardCell(packet.to, enemyPiece);

        // Place the piece in front of the other
        enemyPiece.getParent().setComponentZOrder(enemyPiece, 0);

        // Move the piece
        enemyPiece.move(packet.to);

        // Change the player turn
        changePlayerTurn();
    }

    /**
     * Display the play button
     */
    private void initPlayButton() {
        final int DIM_BUTTON_X = WINDOW_WIDTH / 2, DIM_BUTTON_Y = WINDOW_HEIGHT - (CELL_SIZE * DIM_CHESSBOARD + MARGIN * 3);

        CustomTheme.setup();

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));
        playButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        playButton.setBounds(
                WINDOW_WIDTH / 2 - DIM_BUTTON_X / 2,
                CELL_SIZE * DIM_CHESSBOARD + MARGIN * 2,
                DIM_BUTTON_X, DIM_BUTTON_Y
        );

        playButton.addActionListener(e -> {});

        window.add(playButton);
    }

    /**
     * Display the main window
     */
    private void displayWindow() {
        window.setTitle("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(WINDOW_WIDTH + 19, WINDOW_HEIGHT + 39));

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/icon.png"))).getImage();

        window.setIconImage(icon);

        window.setBackground(Color.RED);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);

        initPlayButton();
    }

    /**
     * Draw the chessboard in the UI
     */
    private void initChessboard() {
        // TODO config
        final Color BLACK_CELL = new Color(0xFFEFD5),
                    WHITE_CELL = new Color(0x654321);

        // Create and paint the background of the panel that will contain the chessboard
        chessboardPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int i = 0; i < DIM_CHESSBOARD; i++) {
                    for (int j = 0; j < DIM_CHESSBOARD; j++) {
                        if ((i + j) % 2 == 0) {
                            g.setColor(clientColor == PlayerColor.WHITE ? BLACK_CELL : WHITE_CELL);
                        } else {
                            g.setColor(clientColor == PlayerColor.WHITE ? WHITE_CELL : BLACK_CELL);
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

        // Add the panel to the frame
        window.add(chessboardPanel);
    }


    /**
     * Draw the pieces to the UI and initialize the board
     */
    private void initPieces() {

        /* Paint all the pieces in the board */

        final PieceType[] startRow = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        // Set cell size and fill the board
        Piece.setCellSize(CELL_SIZE);
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

        // Create, paint and store every piece in the chessboard
        Piece piece;
        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                // Add pieces
                piece = new Piece(startRow[x], playerColor, new Point(x, playerColor == clientColor ? 7 : 0));
                board[playerColor == clientColor ? 7 : 0][x] = piece;
                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(PieceType.PAWN, playerColor, new Point(x, playerColor == clientColor ? 6 : 1));
                board[playerColor == clientColor ? 6 : 1][x] = piece;
                chessboardPanel.add(piece);
            }
        }
        // Repaint to apply changes
        chessboardPanel.repaint();
//
//        // TODO ?
//
//        // Repaint to apply changes
//        window.repaint();
//
//        // TODO ?
    }


    /**
     * Draws the chessboard inside the window
     */
    private void drawBoard() {
        if (chessboardPanel != null)
            window.remove(chessboardPanel);

        initChessboard();
        initPieces();
    }


    /**
     * Displays the window and paints the board
     */
    public void startWindow() {
        displayWindow();
        drawBoard();
    }


    /**
     * Start the game
     */
    public void startGame(PlayerColor clientColor) {
        Game.clientColor = clientColor;
        playerTurn = PlayerColor.WHITE;

        drawBoard();
    }
}
