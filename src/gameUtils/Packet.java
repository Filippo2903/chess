package gameUtils;

import java.awt.*;
import java.io.*;
import java.util.Base64;

public class Packet implements Serializable {
    // Starting position
    public final Point from;
    public final Point to;

    public final PieceType type;

    public final boolean endGame;

    /**
     * @param from Starting position of the piece
     * @param to Arrival position of the piece
     */
    public Packet(Point from, Point to) {
        this(from, to, null, true);
    }

    /**
     * @param from Starting position of the piece
     * @param to Arrival position of the piece
     */
    public Packet(Point from, Point to, PieceType type) {
        this(from, to, type, true);
    }

    /**
     * @param from Starting position of the piece
     * @param to Arrival position of the piece
     * @param endGame Is game over?
     */
    public Packet(Point from, Point to, PieceType type, boolean endGame) {
        this.from = from;
        this.to = to;
        this.type = type;

        this.endGame = endGame;
    }

    public static Packet fromString(String input) throws IOException, ClassNotFoundException {
        byte[] byteData = Base64.getDecoder().decode(input);
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteData));
        Packet object = (Packet) inputStream.readObject();
        inputStream.close();
        return object;
    }

    public String serializeToString() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(output);
        objectOutput.writeObject(this);
        objectOutput.close();
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }
}
