package org.evgensl.sreenreader;

import org.evgensl.sreenreader.app.AppController;
import org.evgensl.sreenreader.image.ImageProcessor;
import org.evgensl.sreenreader.image.ScreenshotService;
import org.evgensl.sreenreader.input.HotkeyService;
import org.evgensl.sreenreader.orc.OcrService;
import org.evgensl.sreenreader.screen.ScreenSelector;
import org.evgensl.sreenreader.tts.api.SpeechConfig;
import org.evgensl.sreenreader.tts.audio.AudioPlayer;
import org.evgensl.sreenreader.tts.edge.EdgeMessageBuilder;
import org.evgensl.sreenreader.tts.edge.EdgeSpeechClient;
import org.evgensl.sreenreader.tts.edge.EdgeTtsService;
import org.evgensl.sreenreader.tts.api.TextToSpeechService;

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
        SpeechConfig speechConfig = new SpeechConfig("ru-RU",
                "ru-RU-DmitryNeural",
                0,
                0,
                0
        );
        EdgeMessageBuilder edgeMessageBuilder = new EdgeMessageBuilder(speechConfig);
        EdgeSpeechClient edgeSpeechClient = new EdgeSpeechClient(edgeMessageBuilder);
        TextToSpeechService textToSpeechService = new EdgeTtsService(edgeSpeechClient, edgeMessageBuilder);

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
