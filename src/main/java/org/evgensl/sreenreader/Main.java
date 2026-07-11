package org.evgensl.sreenreader;

import org.evgensl.sreenreader.app.AppController;
import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.input.HotkeyService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
import org.evgensl.sreenreader.speech.AudioPlayer;
import org.evgensl.sreenreader.speech.EdgeTtsService;
import org.evgensl.sreenreader.speech.TextToSpeechService;
import org.evgensl.sreenreader.speech.WindowsSpeechService;

import java.io.IOException;

public class Main {
    static void main() throws IOException {

        ScreenSelector selector = new ScreenSelector();
        ScreenshotService screenshotService = new ScreenshotService();
        OcrService ocrService = new OcrService();
        ImageProcessor imageProcessor = new ImageProcessor();
        AudioPlayer audioPlayer = new AudioPlayer();
        TextToSpeechService textToSpeechService = new EdgeTtsService();

        AppController appController = new AppController(
                selector,
                screenshotService,
                ocrService,
                textToSpeechService,
                imageProcessor,
                audioPlayer
        );

        HotkeyService hotkeyService = new HotkeyService(
                appController::startSelection,
                appController::cancelSelection
        );
        hotkeyService.start();
    }
}
