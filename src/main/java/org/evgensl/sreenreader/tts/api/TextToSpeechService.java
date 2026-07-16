package org.evgensl.sreenreader.tts.api;

public interface TextToSpeechService {

    Session generateAudio(String text);

    void stop();
}
