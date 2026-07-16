package org.evgensl.sreenreader.tts.edge;

import org.evgensl.sreenreader.tts.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.http.WebSocket;

public class EdgeSession implements Session {

    private Logger log = LoggerFactory.getLogger(EdgeSession.class);
    private final WebSocket webSocket;
    private final EdgeMessageParser parser;
    private final InputStream stream;

    public EdgeSession(WebSocket webSocket, EdgeMessageParser parser, InputStream stream) {
        this.webSocket = webSocket;
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
        webSocket.sendClose(
                WebSocket.NORMAL_CLOSURE,
                "Cancelled"
        ).thenRun(() ->
                log.info("WebSocket closed")
        );;
    }
}
