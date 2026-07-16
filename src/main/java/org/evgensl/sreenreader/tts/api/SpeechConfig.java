package org.evgensl.sreenreader.tts.api;

public record SpeechConfig(
        String locale,
        String voice,
        int rate,
        int pitch,
        int volume
) {
}
