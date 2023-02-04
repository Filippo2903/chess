package client;

import com.formdev.flatlaf.FlatClientProperties;
import gameUtils.Packet;
import gameUtils.PlayerColor;
import gameUtils.SpecialMove;
import themes.CustomTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Game {
    private static PlayerColor clientColor = PlayerColor.WHITE;
    private static PlayerColor playerTurn;

    private static Point[] enemyMove = new Point[2];

    public static final int DIM_CHESSBOARD = 8;
    private static final int WINDOW_WIDTH = 510, WINDOW_HEIGHT = 510; // 570
    private static final int MARGIN = WINDOW_WIDTH / (4 * DIM_CHESSBOARD + 2);
    public static final int CELL_SIZE = (WINDOW_WIDTH - MARGIN * 2) / DIM_CHESSBOARD;

    // The board where all the pieces will be stored
    private static final Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];
    private final JFrame window = new JFrame();
    private JPanel chessboardPanel;
    private static final JLabel fromCell = new JLabel(),
                         toCell = new JLabel();

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
     * Edit a cell of the data board
     * @param cell Cell to edit
     * @param value Value to assign to the cell
     */
    public static void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }

    public static void setFromCellVisible() {
        fromCell.setVisible(true);
    }
    public static void setToCellVisible() {
        toCell.setVisible(true);
    }
    public static void setPositionFromCell(Point cell) {
        fromCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }
    public static void setPositionToCell(Point cell) {
        toCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }

    /**
     * Move the enemy piece
     * @param packet Packet to get data from
     */
    public void enemyMove(Packet packet) {
        Piece enemyPiece = board[packet.from.y][packet.from.x];

        enemyMove = new Point[] {packet.from, packet.to};

        // Delete previous position
        editBoardCell(packet.from, null);

        if (packet.specialMove == SpecialMove.NONE) {
            if (board[packet.to.y][packet.to.x] != null) {
                board[packet.to.y][packet.to.x].kill();
            }
        }

        else if (packet.specialMove == SpecialMove.EN_PASSANT) {
            Piece eatenPiece = board[packet.to.y - 1][packet.to.x];
            eatenPiece.kill();
        }

        else if (packet.specialMove == SpecialMove.KINGSIDE_CASTLE) {
            final Point ROOK_START_POSITION = new Point(7, 0);
            final Point ROOK_ARRIVAL_POSITION = new Point(5, 0);

            Thread animatedMove = new Thread(()-> {
                board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);
            });

            animatedMove.start();
        }

        else if (packet.specialMove == SpecialMove.QUEENSIDE_CASTLE) {
            final Point ROOK_START_POSITION = new Point(0, 0);
            final Point ROOK_ARRIVAL_POSITION = new Point(3, 0);

            Thread animatedMove = new Thread(()-> {
                board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);
            });

            animatedMove.start();
        }

        // Place the piece in front of the other
        chessboardPanel.setComponentZOrder(enemyPiece, 0);

        Thread animatedMove = new Thread(()-> {
            enemyPiece.animatedMove(packet.to);
        });

        animatedMove.start();

        fromCell.setLocation(packet.from.x * CELL_SIZE, packet.from.y * CELL_SIZE);
        toCell.setLocation(packet.to.x * CELL_SIZE, packet.to.y * CELL_SIZE);
        setFromCellVisible();
        setToCellVisible();

        // Change the player turn
        changePlayerTurn();
    }

    public static Point[] getEnemyMove() {
        return enemyMove;
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

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(window,
                        "Are you sure?", "Quit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

                    System.exit(0);
                }
            }
        });

        window.setSize(new Dimension(WINDOW_WIDTH + 19, WINDOW_HEIGHT + 39));

        Image icon = new ImageIcon(Objects.requireNonNull(ClassLoader.getSystemResource("icon.png"))).getImage();

        window.setIconImage(icon);

        window.setBackground(Color.RED);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);

        // TODO matchmaking
        //initPlayButton();
    }

    private void initHighlightedCells() {
        Color HIGHLIGHTED_CELL = new Color(237, 255, 33, 180);

        fromCell.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);
        fromCell.setBackground(HIGHLIGHTED_CELL);
        fromCell.setOpaque(true);
        fromCell.setVisible(false);

        toCell.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);
        toCell.setBackground(HIGHLIGHTED_CELL);
        toCell.setOpaque(true);
        toCell.setVisible(false);

        chessboardPanel.add(fromCell);
        chessboardPanel.add(toCell);
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
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK
        };

        // Fill the board with null
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

        // Create, paint and store every piece in the chessboard
        Piece piece;
        PieceType pieceType;
        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                pieceType = startRow[x];

                // Add pieces
                piece = new Piece(playerColor, pieceType);

                board[playerColor == clientColor ? 7 : 0][x] = piece;

                piece.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);

                piece.setImage();

                piece.setPosition(new Point(x, (playerColor == clientColor ? 7 : 0)));

                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(playerColor, PieceType.PAWN);

                board[playerColor == clientColor ? 6 : 1][x] = piece;

                piece.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);

                piece.setImage();

                piece.setPosition(new Point(x, (playerColor == clientColor ? 6 : 1)));

                chessboardPanel.add(piece);
            }
        }

        // Repaint to apply changes
        chessboardPanel.repaint();
    }


    /**
     * Draws the chessboard inside the window
     */
    private void drawBoard() {
        if (chessboardPanel != null)
            window.remove(chessboardPanel);

        initChessboard();
        initPieces();
        initHighlightedCells();
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
