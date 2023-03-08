package gameUtils;

import java.awt.*;
import java.io.*;
import java.util.Base64;

public class Packet implements Serializable {
    // Starting position
    public final Point from;

    // Arrival position
    public final Point to;

    public final SpecialMoveType specialMoveType;

    public final PieceType newType;

    /**
     * Create a packet that represents a promotion
     *
     * @param from    The starting cell
     * @param to      The arrival cell
     * @param newType The promoted type
     */
    public Packet(Point from, Point to, PieceType newType) {
        this(from, to, null, newType);
    }

    /**
     * Creata a packet that represents a special move
     *
     * @param from            The starting cell
     * @param to              The arrival cell
     * @param specialMoveType The special move that has been made
     */
    public Packet(Point from, Point to, SpecialMoveType specialMoveType) {
        this(from, to, specialMoveType, null);
    }

    /**
     * @param from            Starting position of the piece
     * @param to              Arrival position of the piece
     * @param specialMoveType Special move
     */
    public Packet(Point from, Point to, SpecialMoveType specialMoveType, PieceType newType) {
        this.from = from;
        this.to = to;

        this.specialMoveType = specialMoveType;

        this.newType = newType;

        this.continuePlaying = continuePlaying;
    }

    /**
     * Deserialize a base-64 encoded string to a Packet object
     *
     * @param input String to be decoded and deserialized
     * @return The serialized Packet object
     */
    public static Packet fromString(String input) throws IOException, ClassNotFoundException {
        // Decode the base-64 string
        byte[] byteData = Base64.getDecoder().decode(input);

        // Deserialize the object
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteData));

        // Cast the deserialized object to Packet
        Packet object = (Packet) inputStream.readObject();

        inputStream.close();

        return object;
    }

    /**
     * Serialize the Packet to a base-64 encoded String
     *
     * @return The serialized, encoded string
     */
    public String serializeToString() throws IOException {
        // Serialize the object to string
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(output);
        objectOutput.writeObject(this);
        objectOutput.close();

        // Encode the string to base-64 and return
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }
}
