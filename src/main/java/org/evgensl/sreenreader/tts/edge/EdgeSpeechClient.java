package org.evgensl.sreenreader.tts.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class EdgeSpeechClient {

    private static final Logger log = LoggerFactory.getLogger(EdgeSpeechClient.class);
    private final HttpClient httpClient;
    private final EdgeMessageBuilder edgeMessageBuilder;
    private volatile WebSocket webSocket;


    public EdgeSpeechClient(EdgeMessageBuilder edgeMessageBuilder) {
        this.edgeMessageBuilder = edgeMessageBuilder;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }


    public EdgeSession synthesize(String ssml) {
        try {
            PipedInputStream input = new PipedInputStream();
            PipedOutputStream output = new PipedOutputStream(input);
            EdgeMessageParser parser = new EdgeMessageParser(output);
            EdgeWebSocketListener listener = new EdgeWebSocketListener(parser);
            connect(listener)
                    .thenApply(ws -> {
                        this.webSocket = ws;
                        return ws;
                    })
                    .thenCompose(this::sendSpeechConfig
                    )
                    .thenCompose(ws ->
                            sendSsml(ws, ssml)
                    )
                    .exceptionally(error -> {
                        parser.close();
                        closeWebSocket();
                        try {
                            input.close();
                        } catch (Exception ignored) {
                        }
                        log.error("Edge TTS error", error);
                        return null;
                    });
            return new EdgeSession(webSocket, parser, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private CompletableFuture<WebSocket> connect(
            EdgeWebSocketListener listener
    ) {
        URI uri = create();
        log.info("Connecting to {}", uri);

        return httpClient.newWebSocketBuilder()
                .header("Origin", EdgeConstants.ORIGIN)
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")
                .header("User-Agent", EdgeConstants.USER_AGENT)
                .buildAsync(
                        uri,
                        listener
                );
    }


    private CompletableFuture<WebSocket> sendSpeechConfig(
            WebSocket webSocket
    ) {
        return webSocket.sendText(edgeMessageBuilder.buildSpeechConfig(), true)
                .thenApply(ws -> webSocket);
    }


    private CompletableFuture<WebSocket> sendSsml(
            WebSocket webSocket,
            String ssml
    ) {
        return webSocket.sendText(edgeMessageBuilder.buildSsmlMessage(ssml), true)
                .thenApply(ws -> webSocket);
    }

    public URI create() {
        String url =
                EdgeConstants.WSS_URL
                        + "?TrustedClientToken="
                        + EdgeConstants.TRUSTED_CLIENT_TOKEN
                        + "&Sec-MS-GEC="
                        + generate()
                        + "&Sec-MS-GEC-Version="
                        + EdgeConstants.SEC_MS_GEC_VERSION;
        return URI.create(url);
    }

    private String generate() {

        try {

            long epoch1601 =
                    Instant.parse(
                            "1601-01-01T00:00:00Z"
                    ).toEpochMilli();


            long now =
                    Instant.now()
                            .toEpochMilli();


            long ticks =
                    (now - epoch1601)
                            * 10000;


            long rounded =
                    ticks - (ticks % 3000000000L);


            String input =
                    rounded + "6A5AA1D4EAFF4E9FB37E23D68491D6F4";


            MessageDigest sha =
                    MessageDigest.getInstance("SHA-256");


            byte[] hash =
                    sha.digest(
                            input.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );


            StringBuilder result =
                    new StringBuilder();


            for(byte b : hash) {

                result.append(String.format(
                                "%02x",
                                b
                        )
                );
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void closeWebSocket() {
        WebSocket ws = this.webSocket;
        if (ws != null) {
            ws.sendClose(
                    WebSocket.NORMAL_CLOSURE,
                    "Finished"
            );
            this.webSocket = null;
        }
    }
}
