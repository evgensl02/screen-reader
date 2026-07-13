package org.evgensl.sreenreader.tts.api;

import java.io.InputStream;

public interface TextToSpeechService {

    InputStream generateAudio(String text);

    void stop();
}
