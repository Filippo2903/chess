package gameUtils;

import java.awt.*;
import java.io.*;
import java.util.Base64;

public class Packet implements Serializable{
    public final Point from;
    public final Point to;

    public final boolean endGame;

    public Packet(Point from, Point to) {
        this(from, to, true);
    }

    public Packet(Point from, Point to, boolean endGame) {
        this.from = from;
        this.to = to;

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
