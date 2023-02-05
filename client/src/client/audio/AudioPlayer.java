package client.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
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
                    new BufferedInputStream(Objects.requireNonNull(AudioPlayer.class.getResourceAsStream("/audio/" + audioType.filename)))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            clip.open(ais);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

        clip.start();

    }
}
