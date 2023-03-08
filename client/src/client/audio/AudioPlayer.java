package client.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class AudioPlayer {

    /**
     * Play the audio of a move
     * @param soundEffect The audio to play
     */
    public static void play(SoundEffect soundEffect) {
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        AudioInputStream ais;
        try {
            ais = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(Objects.requireNonNull(AudioPlayer.class.getResourceAsStream("/audio/" + soundEffect.filename)))
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
