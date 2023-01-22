package gameUtils;

public enum PieceType {
    PAWN("P", 1),
    KNIGHT("N", 3),
    BISHOP("B", 3),
    ROOK("R", 5),
    QUEEN("Q", 9),
    KING("K", 10);

    public final String algebraicNotation;
    public final int value;

    PieceType(String algebraicNotation, int value) {
        this.algebraicNotation = algebraicNotation;
        this.value = value;
    }
}