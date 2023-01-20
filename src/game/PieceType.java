package game;

public enum PieceType {
    PAWN("", 1),
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

//public enum PieceType {
//    PAWN("", 1, new Coords[]{new Coords(0, 1)}),
//    KNIGHT("N",3, new Coords[]{new Coords(-2, 1), new Coords(-1, -2), new Coords(-1, -2), new Coords(-1, -2), }),
//    BISHOP("B", 3, new Coords[]{new Coords(-1, -1)}),
//    ROOK("R", 5, new Coords[]{new Coords(-1, -1)}),
//    QUEEN("Q", 9, new Coords[]{new Coords(-1, -1)}),
//    KING("K", 100, new Coords[]{new Coords(-1, -1)});
//    public final int value;
//    public final String algebraicNotation;
//    public final Coords[] allowedMoves;
//    public final Coords[] takeMoves;
//
//
//    PieceType(String algebraicNotation, int value, Coords[] allowedMoves) {
//        this.value = value;
//        this.allowedMoves = allowedMoves;
//        this.algebraicNotation = algebraicNotation;
//        if (algebraicNotation.equals("")) {
//            this.takeMoves = new Coords[]{new Coords(1, 1), new Coords(-1, 1)};
//        } else {
//            this.takeMoves = new Coords[0];
//        }
//    }
//}
