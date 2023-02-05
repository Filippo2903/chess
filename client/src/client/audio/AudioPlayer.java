package client.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class AudioPlayer {
    public static void play(AudioType audioType) {
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        AudioInputStream ais;
        try {
            ais = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(AudioPlayer.class.getResourceAsStream("/audio/" + audioType.filename))
            );
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            clip.open(ais);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

        clip.start();
//        clip.loop(Clip.LOOP_CONTINUOUSLY);
//        SwingUtilities.invokeLater(() -> {
//            // A GUI element to prevent the Clip's daemon Thread
//            // from terminating at the end of the main()
//            JOptionPane.showMessageDialog(null, "Close to exit!");
//        });
    }
}
