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

public class AppController {

    private final ScreenSelector selector;
    private final ScreenshotService screenshotService;
    private final OcrService ocrService;
    private final TextToSpeechService textToSpeechService;
    private final ImageProcessor imageProcessor;
    private final AudioPlayer audioPlayer;
    private final ExecutorService executor;
    private Session session;

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
        selector.select().thenAccept(rectangle -> {
            executor.submit(() -> {
                BufferedImage image = imageProcessor.upscale(screenshotService.capture(rectangle), 2);
                String text = cleanText(ocrService.recognize(image));
                if (!text.isBlank()) {
                    session = textToSpeechService.generateAudio(text);
                    InputStream audioFile = session.getAudioStream();
                    audioPlayer.playAudio(audioFile);
                }
            });

        });
    }

    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
    public void cancelSelection() {
        selector.cancelSelection();
        audioPlayer.stopAudio();
        if (session != null) {
            session.close();
            session = null;
        }
    }
}
