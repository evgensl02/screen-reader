package org.evgensl.sreenreader.tts.edge;

import org.evgensl.sreenreader.tts.api.SpeechConfig;
import org.evgensl.sreenreader.tts.api.TextToSpeechService;

import java.io.InputStream;

public class EdgeTtsService implements TextToSpeechService {

    private final EdgeSpeechClient speechClient;
    private final EdgeMessageBuilder edgeMessageBuilder;

    public EdgeTtsService(EdgeSpeechClient edgeSpeechClient, EdgeMessageBuilder ssmlBuilder) {

        this.speechClient = edgeSpeechClient;
        this.edgeMessageBuilder = ssmlBuilder;

    }

    @Override
    public EdgeSession generateAudio(String text) {
        String ssml = edgeMessageBuilder.buildSsml(text);
        return speechClient.synthesize(ssml);
    }

    @Override
    public void stop() {

    }
}
