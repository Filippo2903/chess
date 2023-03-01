package client;

import client.audio.AudioPlayer;
import client.audio.AudioType;
import client.piece.Check;
import client.piece.Piece;
import client.piece.PieceMoves;
import client.piece.SpecialMovesMap;
import com.formdev.flatlaf.FlatClientProperties;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * The class that stores and manages all the Game
 */
public class Game {
    public static final int DIM_CHESSBOARD = 8;
    private static final int WINDOW_WIDTH = 510, WINDOW_HEIGHT = 570;
    private static final int MARGIN = WINDOW_WIDTH / (4 * DIM_CHESSBOARD + 2); // CELL_SIZE / 4
    public static final int CELL_SIZE = (WINDOW_WIDTH - MARGIN * 2) / DIM_CHESSBOARD;

    public static final int DIM_BUTTON_X = WINDOW_WIDTH / 2;
    public static final int DIM_BUTTON_Y = WINDOW_HEIGHT - (CELL_SIZE * DIM_CHESSBOARD + MARGIN * 3);

    private JLabel loadingGif;
    private final JLabel fromCell;
    private final JLabel toCell;

    // The board where all the pieces will be stored
    private final Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];

    private PlayerColor clientColor = PlayerColor.WHITE;
    private PlayerColor playerTurn;
    private Point[] enemyMove = new Point[2];
    private JFrame window;
    public JPanel chessboardPanel;

    public Game() {
        window = new JFrame();
        fromCell = new JLabel();
        toCell = new JLabel();
    }

    /**
     * DEBUG
     */
    public static void printBoard(Piece[][] _board) {
        for (Piece[] line : _board) {
            for (Piece piece : line) {
                System.out.print(piece == null ? "   " : (piece.getColor().toString().charAt(0) + piece.getType().algebraicNotation) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Get the player color
     * @return The player color
     */
    public PlayerColor getPlayerColor() {
        return clientColor;
    }

    /**
     * Get the player who has to move
     * @return The player
     */
    public PlayerColor getPlayerTurn() {
        return playerTurn;
    }

    /**
     * Get the board where all the pieces are stored
     * @return The board
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Change the client's player turn
     */
    public void changePlayerTurn() {
        playerTurn = playerTurn == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    /**
     * Edit a cell of the data board
     * @param cell  Cell to edit
     * @param value Value to assign to the cell
     */
    public void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }

    /**
     * Set the FromCell position
     * @param cell The position to set the FromCell
     */
    public void setPositionFromCell(Point cell) {
        fromCell.setVisible(true);
        fromCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }

    /**
     * Set the ToCell position
     * @param cell The position to set the ToCell
     */
    public void setPositionToCell(Point cell) {
        toCell.setVisible(true);
        toCell.setLocation(cell.x * CELL_SIZE, cell.y * CELL_SIZE);
    }

    /**
     * Highlight both the attacker piece and the attacked piece cell to denote checkmate
     * @param attackerCell The cell where the attacker piece is
     * @param attackedCell The cell where the attacked piece is
     */
    public void highlightCheckmate(Point attackerCell, Point attackedCell) {
        final Color HIGHLIGHTED_ATTACKER = new Color(0xED4337);
        final Color HIGHLIGHTED_ATTACKED = new Color(0xED4337);

        fromCell.setVisible(true);
        fromCell.setLocation(attackerCell.x * CELL_SIZE, attackerCell.y * CELL_SIZE);
        fromCell.setBackground(HIGHLIGHTED_ATTACKER);

        toCell.setVisible(true);
        toCell.setLocation(attackedCell.x * CELL_SIZE, attackedCell.y * CELL_SIZE);
        toCell.setBackground(HIGHLIGHTED_ATTACKED);
    }

    /**
     * Find the king in the board
     * @param board The board where to look for the king
     * @param kingColor The king color
     * @return The king's position
     */
    public Point findKing(Piece[][] board, PlayerColor kingColor) {
        for (int x = 0; x < Game.DIM_CHESSBOARD; x++) {
            for (int y = 0; y < Game.DIM_CHESSBOARD; y++) {
                if (board[y][x] != null &&
                    board[y][x].getType() == PieceType.KING &&
                    board[y][x].getColor() == kingColor) {

                    return new Point(x, y);
                }
            }
        }

        return null;
    }

    /**
     * Get the type of the promotion
     * @return the type decided from the player
     */
    public PieceMoves promptPromotionType() {
        return PieceMoves.QUEEN;
    }

    /**
     * Promote a given piece
     * @param piece The piece that has to promote
     * @param promoteType The type the piece has to promote
     * @param promotingCell The cell where the piece will be after promotion
     */
    public void promote(Piece piece, PieceMoves promoteType, Point promotingCell) {
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
     * Get the opponent player's color
     * @return The opponent color
     */
    public PlayerColor getOpponentColor() {
        return clientColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    /**
     * Get the latest enemy move
     * @return A pair of coordinates, (From, To)
     */
    public Point[] getEnemyMove() {
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

        chessboardPanel.repaint();

        // Change the player turn
        changePlayerTurn();

        Point kingPosition = findKing(board, clientColor);

        if (kingPosition == null) {
            ErrorPopup.show(400);
            System.exit(-1);
        }

        Point attackerCell = Check.getAttackerCell(kingPosition, clientColor, board);

        // Update the from and to cell highlights
        setPositionFromCell(packet.from);
        setPositionToCell(packet.to);

        if (attackerCell != null) {
            if (Check.isCheckMate(kingPosition, clientColor)) {
                highlightCheckmate(attackerCell, kingPosition);
                System.out.println("Hai perso");
                endGame();
            }
        }
    }

    public void endGame() {
        window.setSize(WINDOW_WIDTH + 19, WINDOW_HEIGHT + 39);
        try {
            Client.socket.close();
        } catch (IOException e) {
            ErrorPopup.show(206);
        }
        playerTurn = PlayerColor.BLACK;
        clientColor = PlayerColor.WHITE;

        displayLobby();
    }

    /**
     * Display the play button
     */
    private void initPlayButton() {
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));

        playButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        playButton.setBorderPainted(false);

        playButton.setBackground(new Color(0xDF3E28));
        playButton.setForeground(new Color(0xFFFFFF));

        playButton.setBounds(
                WINDOW_WIDTH / 2 - DIM_BUTTON_X / 2,
                CELL_SIZE * DIM_CHESSBOARD + MARGIN * 2,
                DIM_BUTTON_X, DIM_BUTTON_Y
        );

        SwingUtilities.invokeLater(()->{
            URL path = ClassLoader.getSystemResource("loading.gif");
            if (path == null) {
                ErrorPopup.show(7);
                System.exit(-1);
            }

            Image gif = new ImageIcon(path).getImage();
            loadingGif = new JLabel(new ImageIcon(gif.getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
            loadingGif.setVisible(false);
            window.getContentPane().add(loadingGif);
            window.setComponentZOrder(loadingGif, 0);
        });

        playButton.addActionListener(e -> {
            window.remove(playButton);
            window.setSize(WINDOW_WIDTH + 19, WINDOW_HEIGHT - MARGIN - DIM_BUTTON_Y + 39);
            loadingGif.setVisible(true);

            new Thread(Client::createMatch).start();
        });

        window.add(playButton);
        window.repaint();
    }

    /**
     * Display the main window
     */
    private void displayWindow() {
        window = new JFrame();
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
        final Color BLACK_CELL = new Color(0xFFEFD5);
        final Color WHITE_CELL = new Color(0x654321);

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
        displayLobby();
    }

    public void displayLobby() {
        initPlayButton();
        drawBoard();
    }

    /**
     * Start the game
     */
    public void startGame(PlayerColor clientColor) {
        this.clientColor = clientColor;
        playerTurn = PlayerColor.WHITE;

        loadingGif.setVisible(false);
        drawBoard();
    }
}
