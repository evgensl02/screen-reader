package org.evgensl.sreenreader;

import org.evgensl.sreenreader.app.AppController;
import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.input.HotkeyService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
import org.evgensl.sreenreader.tts.audio.AudioPlayer;
import org.evgensl.sreenreader.tts.edge.EdgeTtsService;
import org.evgensl.sreenreader.tts.api.TextToSpeechService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static void main() {

        AppController appController = getAppController();

        HotkeyService hotkeyService = new HotkeyService(
                appController::startSelection,
                appController::cancelSelection
        );
        hotkeyService.start();
    }

    private static AppController getAppController() {
        ExecutorService executer = Executors.newSingleThreadExecutor();
        ScreenSelector selector = new ScreenSelector();
        ScreenshotService screenshotService = new ScreenshotService();
        OcrService ocrService = new OcrService();
        ImageProcessor imageProcessor = new ImageProcessor();
        AudioPlayer audioPlayer = new AudioPlayer();
        TextToSpeechService textToSpeechService = new EdgeTtsService();

        return new AppController(
                selector,
                screenshotService,
                ocrService,
                textToSpeechService,
                imageProcessor,
                audioPlayer,
                executer
        );
    }
}
