package org.evgensl.sreenreader.speech;

import java.nio.file.Path;

public interface TextToSpeechService {

    Path generateAudio(String text);

    void stop();
}
