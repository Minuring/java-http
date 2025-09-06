package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            ".html", "text/html",
            ".css", "text/css",
            ".js", "application/javascript"
    );

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var httpRequestMessage = readMessage(inputStream);
            final var response = createResponseMessage(httpRequestMessage);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readMessage(final InputStream inputStream) throws IOException {
        final var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final var bufferedReader = new BufferedReader(inputStreamReader);
        final var stringBuilder = new StringBuilder();

        while (true) {
            final var line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    private String createResponseMessage(final String requestMessage) throws IOException {
        final var requestLine = requestMessage.split("\r\n")[0].trim();
        if (!requestLine.startsWith("GET") || !requestLine.endsWith("HTTP/1.1")) {
            return createBadRequestResponse();
        }

        final var uri = requestLine.split(" ")[1];
        return createResponse(uri);
    }

    private String createBadRequestResponse() {
        final var errorMessage = "잘못된 요청입니다.";
        return "HTTP/1.1 400 Bad Request \r\n" +
                "Content-Type: text/plain;charset=utf-8 \r\n" +
                "Content-Length: " + errorMessage.getBytes().length + " \r\n" +
                "\r\n" +
                errorMessage;
    }

    private String createResponse(final String uri) throws IOException {
        if (uri.isEmpty() || uri.equals("/")) {
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + "Hello world!".getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    "Hello world!"
            );
        }

        if (uri.startsWith("/login")) {
            final var queryParams = extractQueryParams(uri);

            // 계정, 비밀번호를 입력한 경우 로그인 시도
            if (queryParams.containsKey("account") && queryParams.containsKey("password")) {
                final var isLoggedIn = login(queryParams.get("account"), queryParams.get("password"));

                // 로그인 성공한 경우 302 -> index.html 리다이렉트
                if (isLoggedIn) {
                    return "HTTP/1.1 302 Found \r\nLocation: /index.html";
                }

                // 로그인 실패한 경우 302 -> 401.html 리다이렉트
                return "HTTP/1.1 302 Found \r\nLocation: /401.html";
            }

            // 계정, 비밀번호를 입력하지 않은 경우 로그인 화면
            final var resource = getClass().getClassLoader().getResource("static/login.html");
            final var file = new File(resource.getPath());
            final var body = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body
            );
        }

        final var resource = getClass().getClassLoader().getResource("static" + uri);
        if (resource == null) {
            return "HTTP/1.1 404 Not Found \r\nContent-Length: 0";
        }

        final var file = new File(resource.getFile());
        if (file.exists() && !file.isDirectory()) {
            final var contentType = CONTENT_TYPE_MAP.getOrDefault(getExtension(uri), "text/html");
            final var body = Files.readString(file.toPath());
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body
            );
        }

        return "HTTP/1.1 404 Not Found \r\nContent-Length: 0 ";
    }

    private String getExtension(final String uri) {
        if (uri.lastIndexOf('.') == -1) {
            return "";
        }
        return uri.substring(uri.lastIndexOf('.'));
    }

    private boolean login(final String account, final String password) {
        final var optionalUser = InMemoryUserRepository.findByAccount(account);
        final var isLoggedIn = optionalUser
                .map(u -> u.checkPassword(password))
                .orElse(false);

        if (isLoggedIn) {
            log.info("user : {}", optionalUser.get());
            return true;
        }
        return false;
    }

    private Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) {
            return Collections.emptyMap();
        }

        final var queryParams = uri.substring(uri.indexOf("?") + 1);
        return Arrays.stream(queryParams.split("&"))
                .map(param -> param.split("="))
                .filter(param -> param.length == 2)
                .collect(toMap(
                        param -> param[0],
                        param -> param[1])
                );
    }
}
