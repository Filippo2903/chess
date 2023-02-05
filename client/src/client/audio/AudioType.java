package client.audio;

public enum AudioType {
    CASTLE,
    CHECK,
    CHECKMATE,
    GAME_OVER,
    GAME_START,
    MOVE,
    STALEMATE,
    TAKE,;

    public final String filename;

    AudioType() {
        this.filename = this.toString().toLowerCase() + ".wav";
    }
}
