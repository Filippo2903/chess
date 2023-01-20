package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.initGame();
//        BufferedImage image = null;
//        try {
//            image = ImageIO.read(Objects.requireNonNull(
//                    Main.class.getResource("assets/whiteBishop.png")
//            ));
//        } catch (IOException e) {
//            System.err.println("Erorr");
//        }
//
//        System.out.println(image != null ? image.toString() : null);
    }
}