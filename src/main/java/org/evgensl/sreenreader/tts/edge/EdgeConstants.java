package org.evgensl.sreenreader.tts.edge;

public final class EdgeConstants {

    private EdgeConstants() {
    }

    public static final String CHROMIUM_VERSION = "143.0.3650.75";
    public static final String WSS_URL =
            "wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1";

    public static final String TRUSTED_CLIENT_TOKEN =
            "6A5AA1D4EAFF4E9FB37E23D68491D6F4";


    public static final String SEC_MS_GEC_VERSION =
            "1-" + CHROMIUM_VERSION;

    public static final String ORIGIN =
            "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold";

    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) "
                    + "Chrome/143.0.0.0 Safari/537.36 "
                    + "Edg/143.0.0.0";

    // ===== Connection =====



    // ===== Protocol =====

    public static final String CONTENT_TYPE_SSML =
            "application/ssml+xml";

    public static final String CONTENT_TYPE_JSON =
            "application/json; charset=utf-8";

    public static final String PATH_SPEECH_CONFIG =
            "speech.config";

    public static final String PATH_SSML =
            "ssml";

    // ===== Audio =====

    public static final String OUTPUT_FORMAT =
            "audio-24khz-48kbitrate-mono-mp3";
}
