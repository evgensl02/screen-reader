package org.evgensl.screenreader.tts.api;

public interface TextToSpeechService {

    Session generateAudio(String text);

    void stop();
}
