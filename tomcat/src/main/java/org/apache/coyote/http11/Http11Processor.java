package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.io.HttpInputStreamReader;
import org.apache.coyote.io.HttpMessageParser;
import org.apache.coyote.message.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            ".html", "text/html",
            ".css", "text/css",
            ".js", "application/javascript"
    );

    private static final Manager SESSION_MANAGER = new SessionManager();

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

            final var httpRequest = readMessage(inputStream);
            final var response = createResponseMessage(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readMessage(final InputStream inputStream) throws IOException {
        final var reader = new HttpInputStreamReader(inputStream);
        final var parser = new HttpMessageParser();

        final var startLine = parser.parseStartLine(reader.readStartLine());
        final var header = parser.parseHeader(reader.readHeader());
        final var body = reader.readBody();

        return new HttpRequest(startLine, header, body);
    }

    private String createResponseMessage(final HttpRequest request) throws IOException {
        final var httpCookie = request.header().getCookie();
        final var session = SESSION_MANAGER.findSession(httpCookie.getSessionId());
        if (!session.isNew()) {
            final var user = (User) session.getAttribute("user");
            log.info("session found. id = {}, user = {}", session.getId(), user);
        }

        return createResponse(request, session);
    }

    private String createResponse(
            final HttpRequest request,
            final HttpSession session
    ) throws IOException {
        final var httpMethod = request.startLine().httpMethod();
        final var uri = request.startLine().uri();

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

            if (session.getAttribute("user") != null) {
                return "HTTP/1.1 302 Found \r\nLocation: /index.html";
            }

            // 계정, 비밀번호를 입력한 경우 로그인 시도
            if (httpMethod.isPost()) {
                final var formData = Arrays.stream(request.body().split("&"))
                        .map(s -> s.split("="))
                        .collect(toMap(kv -> kv[0], kv -> kv[1]));
                final var loggedInUser = login(formData.get("account"), formData.get("password"));

                // 로그인 성공한 경우 302 -> index.html 리다이렉트
                if (loggedInUser != null) {
                    session.setAttribute("user", loggedInUser);
                    SESSION_MANAGER.add(session);
                    return "HTTP/1.1 302 Found \r\nLocation: /index.html \r\n"
                            + "Set-Cookie: JSESSIONID=" + session.getId();
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

        if (uri.startsWith("/register")) {
            if (httpMethod.isGet()) {
                final var resource = getClass().getClassLoader().getResource("static/register.html");
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

            if (httpMethod.isPost()) {
                final var formData = Arrays.stream(request.body().split("&"))
                        .map(s -> s.split("="))
                        .collect(toMap(kv -> kv[0], kv -> kv[1]));

                final var account = formData.get("account");
                final var password = formData.get("password");
                final var email = formData.get("email");
                final var user = new User(account, password, email);
                InMemoryUserRepository.save(user);
                return "HTTP/1.1 302 Found \r\nLocation: /index.html";
            }
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

    private User login(final String account, final String password) {
        final var optionalUser = InMemoryUserRepository.findByAccount(account);
        final var isLoggedIn = optionalUser
                .map(u -> u.checkPassword(password))
                .orElse(false);

        if (isLoggedIn) {
            final var user = optionalUser.get();
            log.info("user : {}", user);
            return user;
        }
        return null;
    }
}
