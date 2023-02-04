package gameUtils;

import client.Movements.*;

import java.util.ArrayList;
import java.util.List;

public enum PieceType {
    PAWN("P", 1,
            new ArrayList<>(List.of(
                    new SingleStepMovement(),
                    new DoubleStepMovement(),
                    new PawnTakeMovement(),
                    new EnPassant())
            )
    ),

    KNIGHT("N", 3,
            new LMovement()
    ),

    BISHOP("B", 3,
            new DiagonalMovement(true)
    ),

    ROOK("R", 5,
            new StraightMovement(true)
    ),

    QUEEN("Q", 9,
            new ArrayList<>(List.of(
                    new StraightMovement(true),
                    new DiagonalMovement(true))
            )
    ),

    KING("K", 10,
            new ArrayList<>(List.of(
                    new StraightMovement(false),
                    new DiagonalMovement(false),
                    new KingsideCastle(),
                    new QueensideCastle())
            )
    );

    public final String algebraicNotation;
    public final int value;

    PieceType(String algebraicNotation, int value) {
        this.algebraicNotation = algebraicNotation;
        this.value = value;
    }
}
