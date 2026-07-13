package org.evgensl.sreenreader.tts.edge;

public final class EdgeConstants {

    private EdgeConstants() {
    }

    public static final String CHROMIUM_FULL_VERSION = "143.0.3650.75";
    public static final String CHROMIUM_MAJOR_VERSION = CHROMIUM_FULL_VERSION.split("\\.")[0];
    public static final String SEC_MS_GEC_VERSION = "1-" + CHROMIUM_FULL_VERSION;

    public static final String URL =
            "wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1";

    public static final String OUTPUT_FORMAT =
            "audio-24khz-48kbitrate-mono-mp3";

    public static final String EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/" + CHROMIUM_MAJOR_VERSION + ".0.0.0 Safari/537.36 " +
            "Edg/" + CHROMIUM_MAJOR_VERSION + ".0.0.0";

    public static final String CONTENT_TYPE =
            "application/ssml+xml";

    public static final String SPEECH_CONFIG_PATH =
            "speech.config";

    public static final String SSML_PATH =
            "ssml";
}
