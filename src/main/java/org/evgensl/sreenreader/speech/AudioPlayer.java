package org.evgensl.sreenreader.speech;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioPlayer {

    private Thread playbackThread;
    private volatile boolean playing;

    public void playAudio(Path path, Runnable onFinished) {
        stopAudio();

        playing = true;

        playbackThread = new Thread(() -> {
            try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(path))) {
                Player player = new Player(input);
                player.play();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка воспроизведения", e);
            } finally {
                playing = false;
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });
        playbackThread.start();
    }

    public void stopAudio() {
        playing = false;

        if (playbackThread != null) {
            playbackThread.interrupt();
        }
    }
}
