package org.evgensl.sreenreader.tts.edge;

import org.evgensl.sreenreader.tts.api.SpeechConfig;

public interface SsmlBuilder {
    String buildSpeechConfig();
    String buildSsml(String ssml);
}
