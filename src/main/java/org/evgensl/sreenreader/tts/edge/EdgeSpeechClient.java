package org.evgensl.sreenreader.tts.edge;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class EdgeSpeechClient {
    private final HttpClient httpClient;

    public EdgeSpeechClient() {
        httpClient = HttpClient.newHttpClient();
    }

    public InputStream synthesize(String ssml) {
        EdgeMessageParser parser = new EdgeMessageParser();
        EdgeWebSocketListener listener = new EdgeWebSocketListener(parser);
        WebSocket webSocket = connect(listener).join();
        return null;
    }

    private CompletableFuture<WebSocket> connect(EdgeWebSocketListener listener) {
        return httpClient.newWebSocketBuilder()
                .header("Origin", EdgeConstants.URL)
                .header("User-Agent", EdgeConstants.EDGE_UA)
                .buildAsync(
                        URI.create(EdgeConstants.URL),
                        listener
                );
    }

    private CompletableFuture<WebSocket> sendSpeechConfig(WebSocket webSocket) {
        String message = DefaultSsmlBuilder.buildSpeechConfig();
        return webSocket.sendText(message, true).thenApply(ws -> webSocket);
    }

    private CompletableFuture<WebSocket> sendSsml(WebSocket webSocket, String ssml) {
        String message = DefaultSsmlBuilder.buildSsml(ssml);

        return webSocket.sendText(message, true)
                .thenApply(ws -> webSocket);
    }
}
