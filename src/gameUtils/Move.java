package gameUtils;

import client.Piece;

import java.awt.*;

public class Move {
    public final Piece piece;
    public final Point cell;

    public Move(Piece piece, Point cell) {
        this.piece = piece;
        this.cell = cell;
    }

    // TODO
//    public toJsonString() {
//
//    }
}
