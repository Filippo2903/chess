package client.piece;

import client.movements.*;
import gameUtils.PieceType;

import java.util.ArrayList;
import java.util.List;

public enum PieceMoves {
    PAWN(PieceType.PAWN,
        new ArrayList<>(List.of(
            new SingleStepMovement(),
            new DoubleStepMovement(),
            new PawnTakeMovement(),
            new EnPassant()
        ))
    ),
    KNIGHT(PieceType.KNIGHT,
        new LMovement()
    ),
    BISHOP(PieceType.BISHOP,
        new DiagonalMovement(true)
    ),
    ROOK(PieceType.ROOK,
        new StraightMovement(true)
    ),
    QUEEN(PieceType.QUEEN,
        new ArrayList<>(List.of(
            new StraightMovement(true),
            new DiagonalMovement(true)
        ))
    ),
    KING(PieceType.KING,
        new ArrayList<>(List.of(
            new StraightMovement(false),
            new DiagonalMovement(false),
            new KingsideCastle(),
            new QueensideCastle()
        ))
    );

    public final PieceType type;
    public final ArrayList<Movement> movements;

    PieceMoves(PieceType type, Movement movement) {
        this(type, new ArrayList<>(List.of(movement)));
    }

    PieceMoves(PieceType type, ArrayList<Movement> movements) {
        this.type = type;
        this.movements = movements;
    }
}
