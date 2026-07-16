package org.evgensl.sreenreader.tts.audio;

import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {

    private final Logger log = LoggerFactory.getLogger(AudioPlayer.class);
    private volatile Thread playbackThread;
    private volatile Player currentPlayer;
    private volatile InputStream currentStream;


    public void playAudio(InputStream inputStream) {

        stopAudio();
        Thread thread = new Thread(() -> {

            try (
                    BufferedInputStream buffer =
                            new BufferedInputStream(inputStream)
            ) {

                currentStream = buffer;
                Player player = new Player(buffer);
                currentPlayer = player;

                player.play();

            } catch (Exception e) {
                if (!Thread.currentThread().isInterrupted()) {
                    log.error(e.toString());
                }
            } finally {
                currentPlayer = null;
                playbackThread = null;
            }


        }, "audio-player");


        playbackThread = thread;

        thread.setDaemon(true);
        thread.start();
    }


    public void stopAudio() {

        Player player = currentPlayer;

        if (player != null) {
            player.close();
            currentPlayer = null;
        }

        InputStream stream = currentStream;

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {

            }
            currentStream = null;
        }

        Thread thread = playbackThread;

        if (thread != null) {
            thread.interrupt();
            playbackThread = null;
        }
    }
}
