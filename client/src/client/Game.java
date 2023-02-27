package client;

import client.piece.Check;
import client.piece.Piece;
import client.piece.PieceMoves;
import client.piece.SpecialMovesMap;
import client.audio.AudioPlayer;
import client.audio.AudioType;
import com.formdev.flatlaf.FlatClientProperties;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * The class that stores and manages all the Game
 */
public class Game {
    public static final int DIM_CHESSBOARD = 8;
    private static final int WINDOW_WIDTH = 510, WINDOW_HEIGHT = 570;
    private static final int MARGIN = WINDOW_WIDTH / (4 * DIM_CHESSBOARD + 2);
    public static final int CELL_SIZE = (WINDOW_WIDTH - MARGIN * 2) / DIM_CHESSBOARD;

    // The board where all the pieces will be stored
    public static final Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];
    private static final JLabel fromCell = new JLabel(),
                                toCell = new JLabel();
    public static JPanel chessboardPanel;
    private static PlayerColor clientColor = PlayerColor.WHITE;
    private static PlayerColor playerTurn;
    private static Point[] enemyMove = new Point[2];
    private static final JFrame window = new JFrame();

    /** DEBUG **/
    public static void printBoard(Piece[][] _board) {
        for (Piece[] line : _board) {
              for(Piece piece : line) {
                      System.out.print(piece == null ? "   " : (piece.getColor().toString().charAt(0) + piece.getType().algebraicNotation) + " ");
              }
              System.out.println();
        }
        System.out.println();
    }
    /** DEBUG **/

    public static PlayerColor getPlayerColor() {
        return clientColor;
    }

    /**
     * Get the player who has to move
     * @return The player
     */
    public static PlayerColor getPlayerTurn() {
        return playerTurn;
    }

    /**
     * Get the board where all the pieces are stored
     * @return The board
     */
    public static Piece[][] getBoard() {
        return board;
    }

    /**
     * Change the client's player turn
     */
    public static void changePlayerTurn() {
        playerTurn = playerTurn == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    /**
     * Edit a cell of the data board
     * @param cell  Cell to edit
     * @param value Value to assign to the cell
     */
    public static void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }

    /**
     * Set the FromCell position
     * @param cell The position to set the FromCell
     */
    public static void setPositionFromCell(Point cell) {
        fromCell.setVisible(true);
        fromCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }

    /**
     * Set the ToCell position
     * @param cell The position to set the ToCell
     */
    public static void setPositionToCell(Point cell) {
        toCell.setVisible(true);
        toCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }

    /**
     * Returns if the king is in check in the given board
     * @param board The board to check
     * @param kingColor The king's color to look for
     * @return <code>true</code> if the king is in check, otherwise <code>false</code>
     */
    public static boolean isKingInCheck(Piece[][] board, PlayerColor kingColor) {
//        System.out.println("===");
//        Game.printBoard(board);

        for (int x = 0; x < Game.DIM_CHESSBOARD; x++) {
            for (int y = 0; y < Game.DIM_CHESSBOARD; y++) {
                if (board[y][x] != null &&
                    board[y][x].getType() == PieceType.KING &&
                    board[y][x].getColor() == kingColor) {
//                    System.out.println("Checking " + kingColor + " king at x: " + x + " y: " + y);

                    boolean isCellAttacked = Check.isCellAttacked(new Point(x, y), kingColor, board);
//                    System.out.println("Cell is" + (!isCellAttacked ? " not " : " ") + "attacked");

                    return isCellAttacked;
                }
            }
        }

        return false;
    }

    public static PieceMoves inputPromotionType() {
        return PieceMoves.QUEEN;
    }

    /**
     * Promote a given piece
     * @param piece The piece that has to promote
     * @param promoteType The type the piece has to promote
     * @param promotingCell The cell where the piece will be after promotion
     */
    public static void promote(Piece piece, PieceMoves promoteType, Point promotingCell) {
        // Kill the old piece
        piece.kill();

        Piece promotedPiece = new Piece(piece.getColor(), promoteType);

        // Place the newly created piece in the board
        board[promotingCell.y][promotingCell.x] = promotedPiece;

        // Set position, size and icon
        promotedPiece.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);
        promotedPiece.setImage();
        promotedPiece.setPosition(promotingCell);

        // Add the piece to the UI
        chessboardPanel.add(promotedPiece);
        chessboardPanel.setComponentZOrder(promotedPiece, 0);
    }

    /**
     * Get the latest enemy move
     * @return A pair of coordinates, (From, To)
     */
    public static Point[] getEnemyMove() {
        return enemyMove;
    }

    /**
     * Move the enemy piece
     * @param packet Packet to get data from
     */
    public void moveEnemy(Packet packet) {
        Piece enemyPiece = board[packet.from.y][packet.from.x];

        enemyMove = new Point[]{packet.from, packet.to};

        // Delete previous position
        editBoardCell(packet.from, null);

        SpecialMovesMap.specialMovesMap.get(packet.specialMoveType).move(packet.from, packet.to);

        if (board[packet.to.y][packet.to.x] != null) {
            AudioPlayer.play(AudioType.TAKE);
            board[packet.to.y][packet.to.x].kill();
        } else if (packet.specialMoveType == null) {
            AudioPlayer.play(AudioType.MOVE);
        }

        // Move the piece to the target cell
        enemyPiece.animatedMove(packet.to);

        // Check if the piece type has to change
        if (packet.newType != null && packet.to.y == 7) {
            promote(enemyPiece, PieceMoves.valueOf(packet.newType.toString()), packet.to);
        }

        // Update the from and to cell highlights
        setPositionFromCell(packet.from);
        setPositionToCell(packet.to);

        Game.chessboardPanel.repaint();

        // Change the player turn
        changePlayerTurn();
    }

    /**
     * Display the play button
     */
    private void initPlayButton() {
        final int DIM_BUTTON_X = WINDOW_WIDTH / 2, DIM_BUTTON_Y = WINDOW_HEIGHT - (CELL_SIZE * DIM_CHESSBOARD + MARGIN * 3);

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));
        playButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        playButton.setBackground(Color.decode("#DF3E28"));
        playButton.setForeground(Color.decode("#FFFFFF"));

        playButton.setBounds(
                WINDOW_WIDTH / 2 - DIM_BUTTON_X / 2,
                CELL_SIZE * DIM_CHESSBOARD + MARGIN * 2,
                DIM_BUTTON_X, DIM_BUTTON_Y
        );

        URL path = ClassLoader.getSystemResource("loading.gif");
        if (path == null) {
            ErrorPopup.show(7);
            System.exit(-1);
        }

        Image icon = new ImageIcon(path).getImage();
        JLabel loadingGif = new JLabel();
        loadingGif.setIcon(new ImageIcon(icon.getScaledInstance(Game.CELL_SIZE, Game.CELL_SIZE, Image.SCALE_SMOOTH)));

        playButton.addActionListener(e -> {
            chessboardPanel.add(loadingGif);
            new Thread(Client::createMatch).start();
        });

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
                        "Sei sicuro?", "Abbandona",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

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

        initPlayButton();
    }

    /**
     * Initialize the highlighted cells to highlight the last move that has been made
     */
    private void initHighlightedCells() {
        Color HIGHLIGHTED_CELL = new Color(237, 255, 33, 150);

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
        chessboardPanel.setBackground(Color.WHITE);
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
        final PieceMoves[] startRow = {
                PieceMoves.ROOK,
                PieceMoves.KNIGHT,
                PieceMoves.BISHOP,
                PieceMoves.QUEEN,
                PieceMoves.KING,
                PieceMoves.BISHOP,
                PieceMoves.KNIGHT,
                PieceMoves.ROOK
        };

        // Fill the board with null
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

        // Create, paint and store every piece in the chessboard
        Piece piece;
        PieceMoves pieceMoves;
        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                pieceMoves = startRow[x];

                // Add pieces
                piece = new Piece(playerColor, pieceMoves);
                piece.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);
                piece.setImage();
                piece.setPosition(new Point(x, (playerColor == clientColor ? 7 : 0)));

                board[playerColor == clientColor ? 7 : 0][x] = piece;
                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(playerColor, PieceMoves.PAWN);
                piece.setBounds(-1, -1, CELL_SIZE, CELL_SIZE);
                piece.setImage();
                piece.setPosition(new Point(x, (playerColor == clientColor ? 6 : 1)));

                board[playerColor == clientColor ? 6 : 1][x] = piece;
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
