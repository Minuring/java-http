package org.apache.coyote.io;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.BODY;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.FINISHED;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.HEADERS;
import static org.apache.coyote.io.HttpInputStreamReader.Phase.REQUEST_LINE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class HttpInputStreamReader extends InputStreamReader {

    private static final int MAX_REQUEST_SIZE = 4096;
    private static final char CR = '\r';
    private static final char LF = '\n';

    private final InputStream in;

    private byte[] headBuffer;
    private byte[] bodyBuffer;
    private Phase phase = REQUEST_LINE;
    private int requestLineEndIndex;
    private int headerEndIndex;

    public HttpInputStreamReader(final InputStream in) {
        super(in);
        this.in = in;
    }

    public String readRequestLine() throws IOException {
        ensureHeaderRead();
        final var bytes = Arrays.copyOfRange(headBuffer, 0, requestLineEndIndex + 1);
        return new String(bytes, UTF_8);
    }

    public String readHeader() throws IOException {
        ensureHeaderRead();
        final var bytes = Arrays.copyOfRange(headBuffer, requestLineEndIndex + 3, headerEndIndex + 1);
        return new String(bytes, UTF_8);
    }

    public String readBody() throws IOException {
        ensureHeaderRead();
        final var contentLength = getContentLength();

        if (getContentLength() == 0) {
            return "";
        }

        if (phase == BODY) {
            readToBodyBuffer(contentLength);
            phase = FINISHED;
        }
        return new String(bodyBuffer, UTF_8);
    }

    private void readToBodyBuffer(final int contentLength) throws IOException {
        if (contentLength == 0) {
            bodyBuffer = new byte[0];
            return;
        }

        bodyBuffer = in.readNBytes(contentLength);
    }

    private int getContentLength() throws IOException {
        final var header = readHeader().toLowerCase();
        final var p = Pattern.compile("(?i)content-length\\s*:\\s*(\\d+)");
        final var m = p.matcher(header);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private void ensureHeaderRead() throws IOException {
        if (headBuffer != null) {
            return;
        }

        this.headBuffer = readUntilHeader();
    }

    private byte[] readUntilHeader() throws IOException {
        final var buffer = new byte[MAX_REQUEST_SIZE];

        int i = 0;
        while (true) {
            final var b = (byte) in.read();
            if (b == -1) {
                break;
            }

            buffer[i] = b;
            if (phase == REQUEST_LINE && i >= 1 && buffer[i] == LF && buffer[i - 1] == CR) {
                this.requestLineEndIndex = i - 2;
                this.phase = HEADERS;

            } else if (phase == HEADERS) {
                this.headerEndIndex = i;

                if (i >= 3
                        && buffer[i] == LF && buffer[i - 1] == CR
                        && buffer[i - 2] == LF && buffer[i - 3] == CR) {

                    this.headerEndIndex = i - 4;
                    break;
                }
            }

            i++;
        }

        this.phase = BODY;
        return Arrays.copyOfRange(buffer, 0, i + 1);
    }

    enum Phase {
        REQUEST_LINE, HEADERS, BODY, FINISHED
    }
}
