package org.evgensl.sreenreader.app;

import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
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
                String text = ocrService.recognize(image);
                if (!text.isBlank()) {
                    InputStream audioFile = textToSpeechService.generateAudio(text);
                    audioPlayer.playAudio(audioFile);
                }
            });

        });
    }


    public void cancelSelection() {
        selector.cancelSelection();
    }
}
