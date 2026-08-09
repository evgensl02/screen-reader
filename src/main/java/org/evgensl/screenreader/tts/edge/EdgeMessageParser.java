package org.evgensl.screenreader.tts.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        if (audio.length == 0) {
            return;
        }
        try {
            output.write(audio);
            output.flush();
        } catch (IOException e) {
            if (!closed) {
                log.error("Audio stream error", e);
            }
        }
    }

    private byte[] extractAudio(byte[] data) {
        if (data == null || data.length < 2) {
            return new byte[0];
        }

        // Standard Edge TTS binary frame protocol:
        // Byte 0-1: 16-bit Big-Endian header length
        int headerLength = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
        if (headerLength > 0 && 2 + headerLength <= data.length) {
            String headers = new String(data, 2, headerLength, StandardCharsets.UTF_8);
            if (headers.contains("Path:audio")) {
                int start = 2 + headerLength;
                int audioLen = data.length - start;
                if (audioLen > 0) {
                    byte[] audio = new byte[audioLen];
                    System.arraycopy(data, start, audio, 0, audioLen);
                    return audio;
                }
            }
        }

        // Fallback: search for "Path:audio" and skip headers up to double newline
        byte[] path = "Path:audio".getBytes(StandardCharsets.UTF_8);
        int pathIndex = indexOf(data, path, 0);
        if (pathIndex == -1) {
            return new byte[0];
        }

        // Find end of headers (\r\n\r\n or \n\n) after Path:audio
        int audioStart = -1;
        for (int i = pathIndex; i < data.length - 1; i++) {
            if (data[i] == '\n' && data[i + 1] == '\n') {
                audioStart = i + 2;
                break;
            }
            if (i < data.length - 3 && data[i] == '\r' && data[i + 1] == '\n'
                    && data[i + 2] == '\r' && data[i + 3] == '\n') {
                audioStart = i + 4;
                break;
            }
        }

        if (audioStart != -1 && audioStart < data.length) {
            byte[] audio = new byte[data.length - audioStart];
            System.arraycopy(data, audioStart, audio, 0, audio.length);
            return audio;
        }

        return new byte[0];
    }

    private int indexOf(byte[] array, byte[] target, int start) {
        if (target.length == 0) {
            return start;
        }
        outer:
        for (int i = start; i <= array.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
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
