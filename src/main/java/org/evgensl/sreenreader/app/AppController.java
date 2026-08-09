package org.evgensl.sreenreader.app;

import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
import org.evgensl.sreenreader.tts.api.Session;
import org.evgensl.sreenreader.tts.audio.AudioPlayer;
import org.evgensl.sreenreader.tts.api.TextToSpeechService;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class AppController {

    private final ScreenSelector selector;
    private final ScreenshotService screenshotService;
    private final OcrService ocrService;
    private final TextToSpeechService textToSpeechService;
    private final ImageProcessor imageProcessor;
    private final AudioPlayer audioPlayer;
    private final ExecutorService executor;
    private final AtomicReference<Session> currentSession = new AtomicReference<>();
    private volatile boolean cancelled = false;

    public AppController(ScreenSelector selector,
                         ScreenshotService screenshotService,
                         OcrService ocrService,
                         TextToSpeechService textToSpeechService,
                         ImageProcessor imageProcessor,
                         AudioPlayer audioPlayer,
                         ExecutorService executor
    ) {
        this.selector = selector;
        this.screenshotService = screenshotService;
        this.ocrService = ocrService;
        this.textToSpeechService = textToSpeechService;
        this.imageProcessor = imageProcessor;
        this.audioPlayer = audioPlayer;
        this.executor = executor;
    }

    public void startSelection() {
        cancelCurrentSessionAndAudio();
        cancelled = false;
        selector.select().thenAccept(rectangle -> {
            if (cancelled) {
                return;
            }
            executor.submit(() -> {
                if (cancelled) {
                    return;
                }
                BufferedImage captured = screenshotService.capture(rectangle);
                BufferedImage image = imageProcessor.upscale(captured, 2);
                String text = cleanText(ocrService.recognize(image));
                if (!text.isBlank() && !cancelled) {
                    Session session = textToSpeechService.generateAudio(text);
                    Session previous = currentSession.getAndSet(session);
                    if (previous != null) {
                        previous.close();
                    }
                    if (cancelled) {
                        Session s = currentSession.getAndSet(null);
                        if (s != null) {
                            s.close();
                        }
                        return;
                    }
                    InputStream audioFile = session.getAudioStream();
                    audioPlayer.playAudio(audioFile);
                }
            });
        });
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("\r\n", "\n")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public void cancelSelection() {
        cancelled = true;
        selector.cancelSelection();
        cancelCurrentSessionAndAudio();
    }

    private void cancelCurrentSessionAndAudio() {
        audioPlayer.stopAudio();
        Session previous = currentSession.getAndSet(null);
        if (previous != null) {
            previous.close();
        }
    }
}
