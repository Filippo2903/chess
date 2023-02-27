package client.piece;

import client.Game;
import client.audio.AudioPlayer;
import client.audio.AudioType;
import gameUtils.SpecialMoveType;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class SpecialMovesMap {
    private static void castle(SpecialMoveType castleSide, Point cell) {
        AudioPlayer.play(AudioType.CASTLE);

        final Point ROOK_START_POSITION = new Point(castleSide == SpecialMoveType.KINGSIDE_CASTLE ? 7 : 0, cell.y);
        final Point ROOK_ARRIVAL_POSITION = new Point(castleSide == SpecialMoveType.KINGSIDE_CASTLE ? 5 : 3, cell.y);

        // Move the rook
        Game.board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);
    }

    /**
     * A map that stores all the special moves
     */
    public static final Map<SpecialMoveType, SpecialMove> specialMovesMap = new HashMap<>() {{
        put(null, (from, to) -> {});

        put(SpecialMoveType.KINGSIDE_CASTLE, (from, to) -> {
            castle(SpecialMoveType.KINGSIDE_CASTLE, from);
        });

        put(SpecialMoveType.QUEENSIDE_CASTLE, (from, to) -> {
            castle(SpecialMoveType.QUEENSIDE_CASTLE, from);
        });

        put(SpecialMoveType.EN_PASSANT, (from, to) -> {
            AudioPlayer.play(AudioType.TAKE);

            Game.board[from.y][to.x].kill();
        });
    }};
}
