package org.evgensl.screenreader.tts.audio;

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


    public void playAudio(InputStream inputStream, Runnable onFinished) {

        stopAudio();
        Thread thread = new Thread(() -> {
            Player player = null;
            BufferedInputStream buffer = null;

            try {
                buffer = new BufferedInputStream(inputStream);
                currentStream = buffer;
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                player = new Player(buffer);
                currentPlayer = player;

                player.play();

            } catch (Exception e) {
                if (!Thread.currentThread().isInterrupted()) {
                    log.error(e.toString());
                }
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException ignored) {
                    }
                    if (currentStream == buffer) {
                        currentStream = null;
                    }
                }
                if (player != null && currentPlayer == player) {
                    currentPlayer = null;
                }
                if (playbackThread == Thread.currentThread()) {
                    playbackThread = null;
                }
                if (onFinished != null) {
                    onFinished.run();
                }
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
