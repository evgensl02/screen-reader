package org.evgensl.sreenreader.tts.edge;

import org.evgensl.sreenreader.tts.api.SpeechConfig;
import org.evgensl.sreenreader.tts.api.TextToSpeechService;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class EdgeTtsService implements TextToSpeechService {

    private final EdgeSpeechClient speechClient;
    private final SsmlBuilder ssmlBuilder;
    private final SpeechConfig config;

    public EdgeTtsService(EdgeSpeechClient edgeSpeechClient, SsmlBuilder ssmlBuilder, SpeechConfig config) {

        this.speechClient = edgeSpeechClient;
        this.ssmlBuilder = ssmlBuilder;
        this.config = config;
    }

    @Override
    public InputStream generateAudio(String text) {
        String ssml = ssmlBuilder.build(text, config);
        return speechClient.synthesize(ssml);
    }

    @Override
    public void stop() {

    }
}
