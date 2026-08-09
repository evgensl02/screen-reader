package org.evgensl.screenreader.app;

import org.evgensl.screenreader.image.ImageProcessor;
import org.evgensl.screenreader.image.ScreenshotService;
import org.evgensl.screenreader.ocr.OcrService;
import org.evgensl.screenreader.screen.ScreenSelector;
import org.evgensl.screenreader.tts.api.Session;
import org.evgensl.screenreader.tts.audio.AudioPlayer;
import org.evgensl.screenreader.tts.api.TextToSpeechService;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AtomicInteger generation = new AtomicInteger();

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
        int gen = generation.incrementAndGet();
        selector.select().thenAccept(rectangle -> {
            if (generation.get() != gen) {
                return;
            }
            executor.submit(() -> {
                if (generation.get() != gen) {
                    return;
                }
                BufferedImage captured = screenshotService.capture(rectangle);
                BufferedImage image = imageProcessor.upscale(captured, 2);
                String text = cleanText(ocrService.recognize(image));
                if (!text.isBlank() && generation.get() == gen) {
                    Session session = textToSpeechService.generateAudio(text);
                    if (generation.get() != gen) {
                        session.close();
                        return;
                    }
                    Session previous = currentSession.getAndSet(session);
                    if (previous != null) {
                        previous.close();
                    }
                    if (generation.get() != gen) {
                        Session s = currentSession.getAndSet(null);
                        if (s != null) {
                            s.close();
                        }
                        return;
                    }
                    InputStream audioFile = session.getAudioStream();
                    audioPlayer.playAudio(audioFile, () -> {
                        if (currentSession.compareAndSet(session, null)) {
                            session.close();
                        }
                    });
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
        generation.incrementAndGet();
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
