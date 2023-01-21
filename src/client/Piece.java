package client;

import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece extends JLabel {
    private PieceType type;

    private final Point newPosition = new Point();
    private final Point prevPosition = new Point();
    private final int cellSize;

    public Piece(int cellSize, PieceType type, PlayerColor playerColor) {//, Function<Piece, Boolean> canMove) {
        super();

        this.cellSize = cellSize;

        this.type = type;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        String colorName = playerColor.toString().toLowerCase();
        String typeName = this.type.toString().toLowerCase();

        String path = "assets/" + colorName + typeName.substring(0, 1).toUpperCase() + typeName.substring(1) + ".png";
        BufferedImage icon = null;

        try {
            icon = ImageIO.read(Objects.requireNonNull(Piece.class.getResource(path)));
        } catch (IOException e) {
            System.err.println("File not found");
            System.exit(-1);
        }

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
        this.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean undo = false;
            @Override
            public void mousePressed(MouseEvent e) {
                Piece piece = (Piece) e.getSource();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    undo = false;
                    piece.getParent().setComponentZOrder(piece, 0);

                    Piece.this.prevPosition.x =
                            (piece.getLocation().x + e.getX() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                    Piece.this.prevPosition.y =
                            (piece.getLocation().y + e.getY() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                }
                else {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.prevPosition.x,
                            Piece.this.prevPosition.y
                    );
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (undo) return;

                int x = (int) Math.floor(((double) Piece.this.newPosition.x + (cellSize >> 1)) / cellSize);
                int y = (int) Math.floor(((double) Piece.this.newPosition.y + (cellSize >> 1)) / cellSize);

                if (x < 0 || x > 7 || y < 0 || y > 7) {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.prevPosition.x,
                            Piece.this.prevPosition.y
                    );
                }

                Piece piece = (Piece) e.getSource();
                Piece.this.newPosition.x = piece.getLocation().x + e.getX() - (cellSize >> 1);
                Piece.this.newPosition.y = piece.getLocation().y + e.getY() - (cellSize >> 1);
                piece.setLocation(Piece.this.newPosition.x, Piece.this.newPosition.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (undo) return;

                int x = (Piece.this.newPosition.x + (cellSize >> 1)) / cellSize;
                int y = (Piece.this.newPosition.y + (cellSize >> 1)) / cellSize;

                Piece.this.setPosition(x, y);
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    public void setPosition(int x, int y) {
        this.prevPosition.x = x;
        this.prevPosition.y = y;

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
