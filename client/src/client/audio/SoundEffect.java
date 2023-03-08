package client.audio;

public enum SoundEffect {
    CASTLE,
    CHECK,
    CHECKMATE,
    GAME_OVER,
    GAME_START,
    MOVE,
    STALEMATE,
    TAKE,;

    public final String filename;

    SoundEffect() {
        this.filename = this.toString().toLowerCase() + ".wav";
    }
}
