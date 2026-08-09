package org.evgensl.screenreader.tts.api;

import java.io.InputStream;

public interface Session {

    InputStream getAudioStream();
    void close();
}
