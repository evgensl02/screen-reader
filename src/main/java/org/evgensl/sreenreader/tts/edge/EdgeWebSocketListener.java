package org.evgensl.sreenreader.tts.edge;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class EdgeWebSocketListener implements WebSocket.Listener {

    private final EdgeMessageParser edgeMessageParser;

    public EdgeWebSocketListener(EdgeMessageParser edgeMessageParser) {
        this.edgeMessageParser = edgeMessageParser;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("Connected");
        WebSocket.Listener.super.onOpen(webSocket);
    }


    @Override
    public CompletionStage<?> onText(
            WebSocket webSocket,
            CharSequence data,
            boolean last
    ) {
        edgeMessageParser.parseText(data.toString());

        webSocket.request(1);

        return CompletableFuture.completedFuture(null);
    }


    @Override
    public CompletionStage<?> onBinary(
            WebSocket webSocket,
            ByteBuffer data,
            boolean last
    ) {
        edgeMessageParser.parseBinary(data);

        webSocket.request(1);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println(reason);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(
            WebSocket webSocket,
            Throwable error
    ) {
        error.printStackTrace();
    }
}
