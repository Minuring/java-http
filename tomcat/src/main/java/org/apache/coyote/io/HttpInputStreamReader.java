package org.apache.coyote.io;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.BODY;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.HEADERS;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.STARTLINE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HttpInputStreamReader extends InputStreamReader {

    private static final int MAX_REQUEST_SIZE = 4096;
    private static final char CR = '\r';
    private static final char LF = '\n';

    private final InputStream in;

    private byte[] buffer;
    private Phase phase = STARTLINE;
    private int startLineEndIndex;
    private int headerEndIndex;

    public HttpInputStreamReader(final InputStream in) {
        super(in);
        this.in = in;
    }

    public String readStartLine() throws IOException {
        ensureRead();
        final var bytes = Arrays.copyOfRange(buffer, 0, startLineEndIndex + 1);
        return new String(bytes, UTF_8);
    }

    public String readHeader() throws IOException {
        ensureRead();
        final var bytes = Arrays.copyOfRange(buffer, startLineEndIndex + 3, headerEndIndex + 1);
        return new String(bytes, UTF_8);
    }

    public String readBody() throws IOException {
        ensureRead();
        if (phase != BODY) {
            return "";
        }

        final var bytes = Arrays.copyOfRange(buffer, headerEndIndex + 5, buffer.length);
        return new String(bytes, UTF_8);
    }

    private void ensureRead() throws IOException {
        if (buffer != null) {
            return;
        }

        this.buffer = readUntilEnd();
        for (int i = 0; i < buffer.length; i++) {

            if (phase == STARTLINE && i < buffer.length - 2
                    && buffer[i] == CR && buffer[i + 1] == LF
            ) {
                this.startLineEndIndex = i - 1;
                this.phase = HEADERS;
                continue;
            }

            if (phase == HEADERS) {
                this.headerEndIndex = i;

                if (i < buffer.length - 4
                        && buffer[i] == CR && buffer[i + 1] == LF
                        && buffer[i + 2] == CR && buffer[i + 3] == LF) {

                    this.phase = BODY;
                    this.headerEndIndex = i - 1;
                }
            }
        }
    }

    private byte[] readUntilEnd() throws IOException {
        final var buffer = new byte[MAX_REQUEST_SIZE];

        int len = 0;
        for (int b; (b = in.read()) != -1; len++) {
            buffer[len] = (byte) b;
        }
        return Arrays.copyOfRange(buffer, 0, len);
    }

    enum Phase {
        STARTLINE, HEADERS, BODY
    }
}
