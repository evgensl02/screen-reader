package org.evgensl.sreenreader.app;

import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
import org.evgensl.sreenreader.speech.AudioPlayer;
import org.evgensl.sreenreader.speech.TextToSpeechService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppController {

    private final ScreenSelector selector;
    private final ScreenshotService screenshotService;
    private final OcrService ocrService;
    private final TextToSpeechService textToSpeechService;
    private final ImageProcessor imageProcessor;
    private final AudioPlayer audioPlayer;

    public AppController(ScreenSelector selector,
                         ScreenshotService screenshotService,
                         OcrService ocrService,
                         TextToSpeechService textToSpeechService,
                         ImageProcessor imageProcessor,
                         AudioPlayer audioPlayer
    ) {
        this.selector = selector;
        this.screenshotService = screenshotService;
        this.ocrService = ocrService;
        this.textToSpeechService = textToSpeechService;
        this.imageProcessor = imageProcessor;
        this.audioPlayer = audioPlayer;
    }

    public void startSelection() {
        selector.select().thenAccept(rectangle -> {
            BufferedImage image = imageProcessor.upscale(screenshotService.capture(rectangle), 2);
            String text = ocrService.recognize(image);
            if (!text.isBlank()) {
                Path audioFile = textToSpeechService.generateAudio(text);
                try {
                    System.out.println(audioFile);
                    System.out.println(Files.exists(audioFile));
                    System.out.println(Files.size(audioFile));
                    audioPlayer.playAudio(audioFile, () -> deleteTempFile(audioFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void deleteTempFile(Path file) {
        if (file == null) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Не удалось удалить временный файл: " + file);
        }
    }

    public void cancelSelection() {
        selector.cancelSelection();
    }
}
