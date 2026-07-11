package org.evgensl.sreenreader.speech;

import java.io.IOException;
import java.nio.file.Path;

public class WindowsSpeechService implements TextToSpeechService {

    private Process currentProcess;

    @Override
    public Path generateAudio(String text) {
        stop();
        try {
            ProcessBuilder builder =
                    new ProcessBuilder(
                            "powershell",
                            "-Command",
                            buildCommand(text)
                    );
            currentProcess = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
    }

    private String buildCommand(String text) {
        return """
            Add-Type -AssemblyName System.Speech;
            $speaker = New-Object System.Speech.Synthesis.SpeechSynthesizer;
            $speaker.Speak('%s');
            """.formatted(
                escape(text)
        );
    }

    private String escape(String text) {
        return text.replace("'", "''");
    }
}
