package org.evgensl.sreenreader.speech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class EdgeTtsService implements TextToSpeechService {

    private Process currentProcess;
    private Path currentAudioFile;

    @Override
    public Path generateAudio(String text) {
        stop();
        return generateAudioFile(text);
    }

    @Override
    public void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
        if (currentAudioFile != null) {
            try {
                Files.deleteIfExists(currentAudioFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path generateAudioFile(String text) {
        try {
            currentAudioFile = createTempFile();
            currentProcess =
                    new ProcessBuilder(
                            "edge-tts",
                            "--voice",
                            "ru-RU-DmitryNeural",
                            "--text",
                            text,
                            "--write-media",
                            currentAudioFile.toString()
                    ).redirectErrorStream(true).start();
            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         currentProcess.getInputStream()
                                 )
                         )) {

                reader.lines()
                        .forEach(System.out::println);
            }


            int exitCode = currentProcess.waitFor();


            System.out.println("Exit code: " + exitCode);
            if (exitCode != 0) {
                throw new RuntimeException(
                        "Edge TTS завершился с ошибкой: " + exitCode
                );
            }
            return currentAudioFile;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка генерации речи", e);
        }
    }

    private Path createTempFile() {
        try {
            return Files.createTempFile("tts_", ".mp3").toAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
