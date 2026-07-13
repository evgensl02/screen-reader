package org.evgensl.sreenreader.tts.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioPlayer {

    private Thread playbackThread;
    private volatile boolean playing;

    public void playAudio(InputStream inputStream) {
        stopAudio();

        playing = true;

        playbackThread = new Thread(() -> {

            Player player = null;
            try {
                player = new Player(inputStream);
                player.play();
            } catch (JavaLayerException e) {
                throw new RuntimeException(e);
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
