package org.evgensl.screenreader.tts.edge;

import org.evgensl.screenreader.tts.api.SpeechConfig;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EdgeMessageBuilder {

    private final SpeechConfig config;

    public EdgeMessageBuilder(SpeechConfig config) {
        this.config = config;
    }

    public String buildSpeechConfig() {
        String timestamp = createTimestamp();

        return "X-Timestamp:" + timestamp + "\r\n"
                + "Content-Type:application/json; charset=utf-8\r\n"
                + "Path:speech.config\r\n\r\n"
                + """
            {
              "context": {
                "synthesis": {
                  "audio": {
                    "metadataoptions": {
                      "sentenceBoundaryEnabled":"false",
                      "wordBoundaryEnabled":"true"
                    },
                    "outputFormat":"audio-24khz-48kbitrate-mono-mp3"
                  }
                }
              }
            }
            """;
    }

    public String buildSsml(String text) {
        return """
            <speak version="1.0"
                   xmlns="http://www.w3.org/2001/10/synthesis"
                   xml:lang="%s">
                <voice name="%s">
                    <prosody pitch="%s"
                              rate="%s"
                              volume="%s">
                        %s
                    </prosody>
                </voice>
            </speak>
            """.formatted(
                config.locale(),
                config.voice(),
                formatPitch(),
                formatRate(),
                formatVolume(),
                escapeXml(text));
    }
    public String buildSsmlMessage(String ssml) {

        return "X-RequestId:" + createRequestId() + "\r\n"
                + "Content-Type:application/ssml+xml\r\n"
                + "X-Timestamp:" + createTimestamp() + "\r\n"
                + "Path:ssml\r\n\r\n"
                + ssml;
    }

    private String createTimestamp() {
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    private String createRequestId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

    private String escapeXml(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(text.length());

        for (char ch : text.toCharArray()) {
            switch (ch) {
                case '<' -> builder.append("&lt;");
                case '>' -> builder.append("&gt;");
                case '&' -> builder.append("&amp;");
                case '"' -> builder.append("&quot;");
                case '\'' -> builder.append("&apos;");

                default -> {
                    if ((ch >= 0x00 && ch <= 0x08)
                            || (ch >= 0x0B && ch <= 0x0C)
                            || (ch >= 0x0E && ch <= 0x1F)) {
                        builder.append(' ');
                    } else {
                        builder.append(ch);
                    }
                }
            }
        }

        return builder.toString();
    }

    private String formatRate() {
        return "%+d%%".formatted(config.rate());
    }

    private String formatPitch() {
        return "%+dHz".formatted(config.pitch());
    }

    private String formatVolume() {
        return "%+d%%".formatted(config.volume());
    }
}
