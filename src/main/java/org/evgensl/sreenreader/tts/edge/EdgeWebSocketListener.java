package org.evgensl.sreenreader.tts.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class EdgeWebSocketListener implements WebSocket.Listener {

    private final Logger log = LoggerFactory.getLogger(EdgeWebSocketListener.class);
    private final EdgeMessageParser parser;

    public EdgeWebSocketListener(EdgeMessageParser parser) {
        this.parser = parser;
    }


    @Override
    public void onOpen(WebSocket webSocket) {
        log.info("WebSocket connected");
        webSocket.request(1);
    }


    @Override
    public CompletionStage<?> onText(
            WebSocket webSocket,
            CharSequence data,
            boolean last
    ) {
        if (data.toString().contains("Path:turn.end")) {
            parser.close();
        }
        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletionStage<?> onBinary(
            WebSocket webSocket,
            ByteBuffer data,
            boolean last
    ) {
        parser.parseBinary(data);
        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletionStage<?> onClose(
            WebSocket webSocket,
            int statusCode,
            String reason
    ) {
        log.info("Edge TTS closed: {}", reason);
        parser.close();
        return CompletableFuture.completedFuture(null);
    }


    @Override
    public void onError(
            WebSocket webSocket,
            Throwable error
    ) {
        log.error(error.getMessage());
        parser.close();
    }
}
