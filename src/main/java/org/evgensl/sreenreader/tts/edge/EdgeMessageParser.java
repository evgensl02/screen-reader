package org.evgensl.sreenreader.tts.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;

public class EdgeMessageParser {

    private final Logger log = LoggerFactory.getLogger(EdgeMessageParser.class);
    private final PipedOutputStream output;
    private volatile boolean closed;

    public EdgeMessageParser(PipedOutputStream output) {
        this.output = output;
    }

    public void parseBinary(ByteBuffer buffer) {
        if (closed) {
            return;
        }
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        byte[] audio = extractAudio(data);
        if (audio.length == 0){
            return;
        }
        try {
            output.write(audio);
            output.flush();
        } catch(IOException e){
            if (!closed) {
                log.error("Audio stream error", e);
            }
        }
    }


    private byte[] extractAudio(byte[] data) {
        byte[] path =
                "Path:audio".getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );
        int pathIndex = -1;

        for (int i = 0; i <= data.length - path.length; i++) {
            boolean found = true;
            for (int j = 0; j < path.length; j++) {
                if (data[i + j] != path[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                pathIndex = i;
                break;
            }
        }
        if (pathIndex == -1) {
            return new byte[0];
        }
        int start = pathIndex;
        while (start < data.length
                && data[start] != '\n') {

            start++;
        }
        start++;
        if (start >= data.length) {
            return new byte[0];
        }
        byte[] audio =
                new byte[data.length - start];
        System.arraycopy(
                data,
                start,
                audio,
                0,
                audio.length
        );
        return audio;
    }


    public void close() {
        closed = true;
        try {
            output.close();
        } catch (IOException e) {
            log.error("Cannot close audio stream", e);
        }
    }
}
