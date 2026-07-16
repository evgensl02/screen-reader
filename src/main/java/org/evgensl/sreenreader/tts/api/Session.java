package org.evgensl.sreenreader.tts.api;

import java.io.InputStream;

public interface Session {

    InputStream getAudioStream();
    void close();
}
