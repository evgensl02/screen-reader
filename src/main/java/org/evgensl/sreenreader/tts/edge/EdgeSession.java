package org.evgensl.sreenreader.tts.edge;

import org.evgensl.sreenreader.tts.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class EdgeSession implements Session {

    private final Logger log = LoggerFactory.getLogger(EdgeSession.class);
    private final CompletableFuture<WebSocket> webSocketFuture;
    private final EdgeMessageParser parser;
    private final InputStream stream;

    public EdgeSession(CompletableFuture<WebSocket> webSocketFuture, EdgeMessageParser parser, InputStream stream) {
        this.webSocketFuture = webSocketFuture;
        this.parser = parser;
        this.stream = stream;
    }

    @Override
    public InputStream getAudioStream() {
        return stream;
    }

    @Override
    public void close() {
        parser.close();
        try {
            stream.close();
        } catch (Exception ignored) {
        }
        if (webSocketFuture != null) {
            webSocketFuture.thenAccept(ws -> {
                if (ws != null) {
                    try {
                        ws.sendClose(
                                WebSocket.NORMAL_CLOSURE,
                                "Cancelled"
                        ).thenRun(() -> log.info("WebSocket closed"));
                    } catch (Exception e) {
                        log.warn("Failed to close WebSocket: {}", e.getMessage());
                    }
                }
            });
        }
    }
}
